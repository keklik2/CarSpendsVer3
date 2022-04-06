package com.demo.carspends.presentation.fragments.noteFilling

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NoteFillingAddOrEditViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val app: Application,
    private val router: Router
) : AndroidViewModel(app), PropertyHost {

    fun goBack() = router.exit()

    private val noteFuelTypes = Fuel.values()
    private val nTitle = app.getString(NOTE_TITLE_ID)
    var nTotalPrice: String? by state(null)
    var nPrice: String? by state(null)
    var nVolume: String? by state(null)
    var nMileage: String? by state(null)
    var nDate by state(getCurrentDate())
    var nId: Int? by state(null)
    var cId: Int? by state(null)

    private var noteItem: NoteItem? by state(null)
    private var carItem: CarItem? by state(null)
    private val noteType = NoteType.FUEL
    var lastFuelType by state(Fuel.F92)
    var canCloseScreen by state(false)

    private val _notesListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getNoteItemsListByMileageUseCase.invoke() }
    )
    private val notesListState by stateFromFlow(_notesListLoading.stateFlow)
    private var notesListForCalculation = mutableListOf<NoteItem>()

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    private val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    init {
        autorun(::nId) {
            if (it != null) _notesListLoading.refresh()
            else {
                noteItem = null
                nDate = getCurrentDate()
            }
        }

        autorun(::cId) {
            if (it != null) _carsListLoading.refresh()
            else carItem = null
        }

        autorun(::notesListState) { itState ->
            when (itState) {
                is Loading.State.Data -> {
                    nId?.let { itId ->
                        noteItem = itState.data.firstOrNull { itNote ->
                            itNote.id == itId
                        }
                    }
                    notesListForCalculation.clear()
                    notesListForCalculation.addAll(itState.data)

                    notesListForCalculation.firstOrNull { itNotes ->
                        itNotes.type == NoteType.FUEL
                    }?.let { itItem ->
                        lastFuelType = itItem.fuelType
                    }
                }
                is Loading.State.Error -> Toast.makeText(
                    app.applicationContext,
                    app.getString(R.string.toast_notes_loading_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        autorun(::carsListState) { itState ->
            when (itState) {
                is Loading.State.Data -> {
                    cId?.let { itId ->
                        carItem = itState.data.firstOrNull { itCar ->
                            itCar.id == itId
                        }
                    }
                }
            }
        }

        autorun(::noteItem) {
            if (it != null) {
                nVolume = getFormattedDoubleAsStr(it.liters)
                nTotalPrice = getFormattedDoubleAsStr(it.totalPrice)
                nPrice = getFormattedDoubleAsStr(it.price)
                nMileage = it.mileage.toString()
                nDate = it.date
            } else {
//                nVolume = null
//                nPrice = null
//                nTotalPrice = null
//                nMileage = null
                nDate = getCurrentDate()
            }
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
            addNoteItemUseCase(newNote)
            notesListForCalculation.clear()
            notesListForCalculation.addAll(getNoteItemsListByMileageUseCase())

            if (noteItem == null) updateMileage(rMileage)
            else rollbackCarMileage()
            calculateAllMileage()
            addAllPrice()
            calculateAvgFuel()
            calculateAvgPrice()
            calculateAllFuelPrice()
            calculateAllFuel()

            updateCarItem()
            setCanCloseScreen()
        }
    }

    private fun calculateAllFuelPrice() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val totalFuelPrice = max(
                notes.sumOf { it.totalPrice },
                0.0
            )

            carItem = itCar.copy(
                fuelPrice = totalFuelPrice
            )
        }
    }

//    private suspend fun addAllFuelPrice(note: NoteItem) {
//        val carItem = getCarItemUseCase(carId)
//        editCarItemUseCase(
//            carItem.copy(
//                fuelPrice = carItem.fuelPrice + note.totalPrice
//            )
//        )
//    }

    private fun calculateAllFuel() {
        carItem?.let { itCar ->
            val notes = getFuelNotes()
            val totalFuel = max(
                notes.sumOf { it.liters },
                0.0
            )

            carItem = itCar.copy(
                allFuel = totalFuel
            )
        }
    }

//    private suspend fun addAllFuel(note: NoteItem) {
//        val carItem = getCarItemUseCase(carId)
//        editCarItemUseCase(
//            carItem.copy(
//                allFuel = carItem.allFuel + note.liters
//            )
//        )
//    }

    private fun calculateAvgPrice() {
        carItem?.let { itCar ->
            val newMilPrice =
                if (itCar.allPrice > 0 && itCar.allMileage > 0) {
                    val res = itCar.allPrice / itCar.allMileage
                    if (res < 0) 0.0
                    else res
                } else 0.0

            carItem = itCar.copy(
                milPrice = newMilPrice
            )
        }

    }

    private fun calculateAllMileage() {
        carItem?.let { itCar ->
            val resMil = if (notesListForCalculation.isNotEmpty()) {
                val lastNote = getLastNotExtraNote()
                val maxMil = max(itCar.mileage, lastNote?.mileage ?: 0)
                val minMil =
                    if (lastNote != null) min(
                        itCar.startMileage,
                        lastNote.mileage
                    ) else itCar.startMileage
                abs(maxMil - minMil)
            } else 0

            carItem = itCar.copy(
                allMileage = resMil
            )
        }
    }

    private fun rollbackCarMileage() {
        carItem?.let { itCar ->
            val newMileage = notesListForCalculation.lastOrNull {
                it.type != NoteType.EXTRA
                        && it.mileage > itCar.startMileage
            }?.mileage ?: itCar.startMileage


            carItem = itCar.copy(
                mileage = newMileage
            )
        }
    }

//    private suspend fun addLastPrice(note: NoteItem) {
//        val carItem = getCarItemUseCase(carId)
//        editCarItemUseCase(
//            carItem.copy(
//                allPrice = carItem.allPrice + note.totalPrice
//            )
//        )
//    }

    private fun addAllPrice() {
        carItem?.let { itCar ->
            val allPrice = max(
                notesListForCalculation.sumOf { it.totalPrice },
                0.0
            )

            carItem = itCar.copy(
                allPrice = allPrice
            )
        }
    }

    private fun calculateAvgFuel() {
        carItem?.let { itCar ->
            if (notesListForCalculation.size > 1) {
                val listOfFuel = getFuelNotes()
                if (listOfFuel.size > 1) {
                    val note1 = listOfFuel[0]
                    val note2 = listOfFuel[1]
                    if (note2 != note1) {
                        val newMomentFuel = calculatedAvgFuelOfTwoNotes(note1, note2)
                        carItem = itCar.copy(
                            momentFuel = newMomentFuel,
                            avgFuel = calculateAvgFuelOfAll(listOfFuel)
                        )
                    }
                }
            }
        }

    }

    private fun calculateAvgFuelOfAll(listOfFuel: List<NoteItem>): Double {
        if (listOfFuel.size > 1) {
            val allMileage = listOfFuel.first().mileage - listOfFuel.last().mileage
            val allFuel = max(
                listOfFuel.sumOf { it.liters },
                0.0
            )

            val res = (allFuel / allMileage.toDouble()) * 100
            return if (res > 0) {
                if (res == Double.POSITIVE_INFINITY) Double.MAX_VALUE
                else res
            } else 0.0
        }
        return 0.0
    }

    private fun calculatedAvgFuelOfTwoNotes(n1: NoteItem, n2: NoteItem): Double {
        val distance = maxOf(n1.mileage, n2.mileage) - minOf(n1.mileage, n2.mileage)
        val res = (n1.liters / distance) * 100
        return if (res > 0) {
            if (res == Double.POSITIVE_INFINITY) Double.MAX_VALUE
            else res
        } else 0.0
    }

    private fun updateMileage(newMileage: Int) {
        carItem?.let { itCar ->
            val oldMileage = itCar.mileage
            if (newMileage > oldMileage) {
                carItem = itCar.copy(
                    mileage = newMileage
                )
            }
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

//    fun setLastRefillFuelType() {
//        notesListForCalculation.firstOrNull { itNotes ->
//            itNotes.type == NoteType.FUEL
//        }?.let { itItem ->
//            lastFuelType = itItem.fuelType
//        }
//    }

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
    private fun getLastNotExtraNote(): NoteItem? =
        notesListForCalculation.firstOrNull { it.type != NoteType.EXTRA }
    private suspend fun updateCarItem() = carItem?.let { editCarItemUseCase(it) }
    private fun getCurrentDate(): Long = Date().time
    private fun setCanCloseScreen() {
        canCloseScreen = true
    }

    companion object {
        private const val NOTE_TITLE_ID = R.string.text_refill
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
