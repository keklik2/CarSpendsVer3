package com.demo.carspends.presentation.fragments.noteFilling

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.domain.picture.DeletePictureUseCase
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.ui.baseViewModel.NoteAddOrEditViewModel
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NoteFillingAddOrEditViewModel @Inject constructor(
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val addNoteItemUseCase: AddNoteItemUseCase,
    getNoteItemUseCase: GetNoteItemUseCase,
    getCarItemUseCase: GetCarItemUseCase,
    editCarItemUseCase: EditCarItemUseCase,
    deletePictureUseCase: DeletePictureUseCase,
    router: Router,
    app: Application
) : NoteAddOrEditViewModel(
    getCarItemUseCase,
    editCarItemUseCase,
    getNoteItemUseCase,
    deletePictureUseCase,
    router,
    app
) {

    var nTotalPrice: String? by state(null)
    var nPrice: String? by state(null)
    var nVolume: String? by state(null)
    var nMileage: String? by state(null)

    override val noteType = NoteType.FUEL
    private val noteFuelTypes = Fuel.values()
    private val nTitle = app.getString(NOTE_TITLE_ID)

    var lastFuelType by state(Fuel.F92)
    private var notesListForCalculation = mutableListOf<NoteItem>()

    init {
        withScope {
            notesListForCalculation = getNoteItemsListByMileageUseCase().toMutableList()
            if (noteItem == null) lastFuelType = getFuelNotes().firstOrNull()?.fuelType ?: Fuel.F95
        }

        autorun(::noteItem) {
            if (it != null) {
                lastFuelType = it.fuelType
                nVolume = getFormattedDoubleAsStr(it.liters)
                nTotalPrice = getFormattedDoubleAsStr(it.totalPrice)
                nPrice = getFormattedDoubleAsStr(it.price)
                nMileage = it.mileage.toString()
                nDate = it.date
            }
            else nDate = getCurrentDate()
        }

        autorun(::carItem) {
            it?.let {
                if (nMileage == null) nMileage = it.mileage.toString()
            }
        }
    }

    fun addOrEditNoteItem(
        fuelTypeId: Int,
        volume: String?,
        totalPrice: String?,
        price: String?,
        mileage: String?
    ) {
        val rFuelType = refactorFuel(fuelTypeId)
        val rVolume = refactorDouble(volume)
        val rTotalPrice = refactorDouble(totalPrice)
        val rPrice = refactorDouble(price)
        val rMileage = refactorInt(mileage)

        val newNote = noteItem?.copy(
            totalPrice = rTotalPrice,
            price = rPrice,
            liters = rVolume,
            mileage = rMileage,
            fuelType = rFuelType,
            date = nDate
        ) ?: NoteItem(
            title = nTitle,
            totalPrice = rTotalPrice,
            price = rPrice,
            liters = rVolume,
            mileage = rMileage,
            fuelType = rFuelType,
            date = nDate,
            type = noteType
        )

        viewModelScope.launch {
            addNoteItemUseCase(newNote, pictures)
            notesListForCalculation.clear()
            notesListForCalculation.addAll(getNoteItemsListByMileageUseCase())

            calculateAllMileage()
            calculateAllPrice()
            calculateAvgPrice()

            calculateAvgFuel()
            calculateMomentFuel()
            calculateAllFuel()
            calculateFuelPrice()

            updateCarItem()
            setCanCloseScreen()
        }
    }

    private fun calculateAllMileage() {
        carItem?.let { itCar ->
            val notes = getNotExtraNotes()
            val newMileage =
                if (notes.isNotEmpty())
                    abs(max(notes.maxOf { it.mileage }, itCar.startMileage) - min(notes.minOf { it.mileage }, itCar.startMileage))
                else 0

            carItem = itCar.copy(
                allMileage = newMileage,
                mileage = max(itCar.startMileage, notes.first().mileage)
            )
        }
    }

    private fun calculateAllPrice() {
        carItem?.let { itCar ->
            val allPrice = notesListForCalculation.sumOf { it.totalPrice }

            carItem = itCar.copy(allPrice = if (allPrice < 0) 0.0 else allPrice)
        }
    }

    private fun calculateAvgPrice() {
        carItem?.let { itCar ->
            val allPrice = if (itCar.allPrice > 0) itCar.allPrice else 0.0
            val allMileage = if (itCar.allMileage > 0) itCar.allMileage else 0

            carItem = itCar.copy(
                milPrice = if (allPrice <= 0.0 || allMileage <= 0) 0.0 else allPrice / allMileage
            )
        }
    }

    private fun calculateAvgFuel() {
        carItem?.let { itCar ->
            val notes = getFuelNotes().sortedByDescending { it.mileage }
            val newAvgFuel = if (notes.size >= 2) {
                val mileage = abs(notes.first().mileage - notes.last().mileage)
                val fuel = abs(notes.sumOf { it.liters } - notes.last().liters)
                if (fuel <= 0 || mileage <= 0) 0.0
                else fuel / (mileage / 100)
            } else 0.0

            carItem = itCar.copy(
                avgFuel = newAvgFuel
            )
        }
    }

    private fun calculateMomentFuel() {
        carItem?.let { itCar ->
            val sorted = getFuelNotes().sortedByDescending { it.mileage }
            val newMomentFuel = if (sorted.size >= 2) {
                val mileage = abs(sorted.first().mileage - sorted[sorted.size - 2].mileage)
                val fuel = sorted.first().liters
                if (fuel <= 0 || mileage <= 0) 0.0
                else fuel / (mileage / 100)
            } else 0.0

            carItem = itCar.copy(
                momentFuel = newMomentFuel
            )
        }
    }

    private fun calculateAllFuel() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val newFuel = if (notes.isNotEmpty()) {
                val fuel = notes.sumOf { it.liters }
                if (fuel <= 0 ) 0.0
                else fuel
            } else 0.0

            carItem = itCar.copy(
                allFuel = newFuel
            )
        }
    }

    private fun calculateFuelPrice() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val newFuelPrice = if (notes.isNotEmpty()) {
                val price = notes.sumOf { it.totalPrice }
                if (price <= 0 ) 0.0
                else price
            } else 0.0

            carItem = itCar.copy(
                fuelPrice = newFuelPrice
            )
        }
    }

    private fun refactorFuel(id: Int): Fuel {
        return noteFuelTypes[id]
    }

    fun getFuelId(fuel: Fuel): Int {
        for ((i, f) in Fuel.values().withIndex()) {
            if (f == fuel) return i
        }
        return 0
    }

    fun calculateVolume(amount: String?, price: String?) {
        val rAmount = refactorDouble(amount)
        val rPrice = refactorDouble(price)

        nVolume = if (rAmount >= 0 && rPrice >= 0) {
            val res = rAmount / rPrice
            if (res in 0.0..100000.0) getFormattedDoubleAsStr(rAmount / rPrice)
            else getFormattedDoubleAsStr(0.0)
        } else getFormattedDoubleAsStr(0.0)

    }

    fun calculateTotalPrice(volume: String?, price: String?) {
        val rVolume = refactorDouble(volume)
        val rPrice = refactorDouble(price)

        nTotalPrice = if (rVolume >= 0 && rPrice >= 0) {
            val res = rVolume * rPrice
            if (res in 0.0..100000.0) getFormattedDoubleAsStr(rVolume * rPrice)
            else getFormattedDoubleAsStr(0.0)
        } else getFormattedDoubleAsStr(0.0)
    }

    fun calculatePrice(amount: String?, volume: String?) {
        val rAmount = refactorDouble(amount)
        val rVolume = refactorDouble(volume)

        nPrice = if (rAmount >= 0 && rVolume >= 0) {
            val res = rAmount / rVolume
            if (res in 0.0..100000.0) getFormattedDoubleAsStr(res)
            else getFormattedDoubleAsStr(0.0)
        } else getFormattedDoubleAsStr(0.0)
    }

    private fun getFuelNotes(): List<NoteItem> =
        notesListForCalculation.filter { it.type == NoteType.FUEL }
    private fun getNotExtraNotes(): List<NoteItem> {
        return notesListForCalculation.filter { it.type != NoteType.EXTRA }
    }

    companion object {
        private const val NOTE_TITLE_ID = R.string.text_refill
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
