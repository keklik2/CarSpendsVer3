package com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NoteFillingAddOrEditViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val editNoteItemUseCase: EditNoteItemUseCase,
    private val getNoteItemUseCase: GetNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getCarItemUseCase: GetCarItemUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val app: Application
) : AndroidViewModel(app) {

    private val noteType = NoteType.FUEL
    private val noteTitle = getApplication<Application>().getString(NOTE_TITLE_ID)
    private val noteFuelTypes = Fuel.values()
    private var carId: Int = CarItem.UNDEFINED_ID

    private val _noteDate = MutableLiveData<Long>()
    val noteDate get() = _noteDate

    private val _errorMileageInput = MutableLiveData<Boolean>()
    val errorMileageInput get() = _errorMileageInput

    private val _calcPrice = MutableLiveData<Double>()
    val calcPrice get() = _calcPrice

    private val _calcAmount = MutableLiveData<Double>()
    val calcAmount get() = _calcAmount

    private val _calcVolume = MutableLiveData<Double>()
    val calcVolume get() = _calcVolume

    private val _errorPriceInput = MutableLiveData<Boolean>()
    val errorPriceInput get() = _errorPriceInput

    private val _errorTotalPriceInput = MutableLiveData<Boolean>()
    val errorTotalPriceInput get() = _errorTotalPriceInput

    private val _errorVolumeInput = MutableLiveData<Boolean>()
    val errorVolumeInput get() = _errorVolumeInput

    private val _lastFuelType = MutableLiveData<Fuel>()
    val lastFuelType get() = _lastFuelType

    private val _noteItem = MutableLiveData<NoteItem>()
    val noteItem get() = _noteItem

    private val _currCarItem = MutableLiveData<CarItem>()
    val currCarItem get() = _currCarItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _noteDate.value = Date().time
    }

    fun addNoteItem(
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

        if (areFieldsValid(rVolume, rTotalPrice, rPrice, rMileage)) {
            viewModelScope.launch {
                val nDate = _noteDate.value
                if (nDate != null) {
                    val newNote = NoteItem(
                        title = noteTitle,
                        totalPrice = rTotalPrice,
                        price = rPrice,
                        liters = rVolume,
                        mileage = rMileage,
                        fuelType = rFuelType,
                        date = nDate,
                        type = noteType
                    )
                    addNoteItemUseCase(newNote)

                    updateMileage(rMileage)
                    calculateAllMileage()
                    addLastPrice(newNote)
                    calculateAvgFuel()
                    calculateAvgPrice()
                    addAllFuel(newNote)
                    addAllFuelPrice(newNote)
                    setCanCloseScreen()
                } else throw Exception("Received NULL NoteItem for AddNoteItemUseCase()")
            }
        }
    }

    fun editNoteItem(
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

        if (areFieldsValid(rVolume, rTotalPrice, rPrice, rMileage)) {
            viewModelScope.launch {
                val nItem = _noteItem.value
                if (nItem != null) {
                    val nDate = _noteDate.value
                    if (nDate != null) {
                        editNoteItemUseCase(
                            nItem.copy(
                                title = noteTitle,
                                totalPrice = rTotalPrice,
                                price = rPrice,
                                liters = rVolume,
                                mileage = rMileage,
                                fuelType = rFuelType,
                                date = nDate,
                                type = noteType
                            )
                        )

                        rollbackCarMileage()
                        calculateAllMileage()
                        addAllPrice()
                        calculateAvgFuel()
                        calculateAvgPrice()
                        calculateAllFuelPrice()
                        calculateAllFuel()
                        setCanCloseScreen()
                    } else throw Exception("Received NULL NoteItem for AddNoteItemUseCase()")
                } else throw Exception("Received NULL NoteItem for EditNoteItemUseCase()")
            }
        }
    }

    private suspend fun getFuelNotes(): List<NoteItem> {
        val notes = getNoteItemsListByMileageUseCase()
        val notesFuel = mutableListOf<NoteItem>()
        if (notes.isNotEmpty()) {
            for (i in notes) {
                if (i.type == NoteType.FUEL) notesFuel.add(i)
            }
        }
        return notesFuel
    }

    private suspend fun calculateAllFuelPrice() {
        val notes = getFuelNotes()
        var totalFuelPrice = 0.0
        if (notes.isNotEmpty()) {
            for (i in notes) {
                totalFuelPrice += i.totalPrice
            }
        }

        if (totalFuelPrice < 0) totalFuelPrice = 0.0

        editCarItemUseCase(
            getCarItemUseCase(carId).copy(
                fuelPrice = totalFuelPrice
            )
        )
    }

    private suspend fun addAllFuelPrice(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        editCarItemUseCase(
            carItem.copy(
                fuelPrice = carItem.fuelPrice + note.totalPrice
            )
        )
    }

    private suspend fun calculateAllFuel() {
        val notes = getFuelNotes()
        var totalFuel = 0.0
        if (notes.isNotEmpty()) {
            for (i in notes) {
                totalFuel += i.liters
            }
        }

        if (totalFuel < 0) totalFuel = 0.0

        editCarItemUseCase(
            getCarItemUseCase(carId).copy(
                allFuel = totalFuel
            )
        )
    }

    private suspend fun addAllFuel(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        editCarItemUseCase(
            carItem.copy(
                allFuel = carItem.allFuel + note.liters
            )
        )
    }

    private suspend fun calculateAvgPrice() {
        val carItem = getCarItemUseCase(carId)
        val newMilPrice =
            if (carItem.allPrice > 0 && carItem.allMileage > 0) {
                val res = carItem.allPrice / carItem.allMileage
                if (res < 0) 0.0
                else res
            } else 0.0

        editCarItemUseCase(
            carItem.copy(
                milPrice = newMilPrice
            )
        )
    }

    private suspend fun calculateAllMileage() {
        val carItem = getCarItemUseCase(carId)
        val notes = getNoteItemsListByMileageUseCase()

        val resMil = if (notes.isNotEmpty()) {
            val maxMil = max(carItem.mileage, notes[0].mileage)
            val lastNote = getLastNotExtraNote()
            val minMil =
                if (lastNote != null) min(
                    carItem.startMileage,
                    lastNote.mileage
                ) else carItem.startMileage
            abs(maxMil - minMil)
        } else 0

        editCarItemUseCase(
            carItem.copy(
                allMileage = resMil
            )
        )
    }

    private suspend fun getLastNotExtraNote(): NoteItem? {
        val notes = getNoteItemsListByMileageUseCase()
        for (i in notes.reversed()) {
            if (i.type != NoteType.EXTRA) return i
        }
        return null
    }

    private suspend fun addLastPrice(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        editCarItemUseCase(
            carItem.copy(
                allPrice = carItem.allPrice + note.totalPrice
            )
        )
    }

    private suspend fun addAllPrice() {
        var allPrice = 0.0
        for (i in getNoteItemsListByMileageUseCase()) {
            allPrice += i.totalPrice
        }

        if (allPrice < 0) allPrice = 0.0

        editCarItemUseCase(
            getCarItemUseCase(carId).copy(
                allPrice = allPrice
            )
        )
    }

    private suspend fun rollbackCarMileage() {
        val cItem = getCarItemUseCase(carId)
        val notesList = getNoteItemsListByMileageUseCase()
        var newMileage = cItem.startMileage
        if (notesList.isNotEmpty()) {
            for (i in notesList) {
                if (i.type != NoteType.EXTRA && i.mileage > newMileage) newMileage = i.mileage
            }
        }
        editCarItemUseCase(
            cItem.copy(
                mileage = newMileage
            )
        )
        updateCarItem()
    }

    private suspend fun calculateAvgFuel() {
        val notes = getNoteItemsListByMileageUseCase()
        if (notes.size > 1) {
            val listOfFuel = mutableListOf<NoteItem>()
            for (i in notes) {
                if (i.type == NoteType.FUEL) listOfFuel.add(i)
            }
            if (listOfFuel.size > 1) {
                val note1 = listOfFuel[0]
                val note2 = listOfFuel[1]
                if (note2 != note1) {
                    val cCarItem = getCarItemUseCase(carId)
                    val test = calculatedAvgFuelOfTwoNotes(note1, note2)
                    Log.d("tag_test", "Moment fuel: $test")
                    editCarItemUseCase(
                        cCarItem.copy(
                            momentFuel = test,
                            avgFuel = calculateAvgFuelOfAll(listOfFuel)
                        )
                    )
                    updateCarItem()
                }
            }
        }
    }

    private fun calculateAvgFuelOfAll(listOfFuel: List<NoteItem>): Double {
        if (listOfFuel.size > 1) {
            var allMileage = listOfFuel[0].mileage - listOfFuel[listOfFuel.size - 1].mileage
            var allFuel = 0.0
            for (i in 0 until listOfFuel.size - 1) {
                allFuel += listOfFuel[i].liters
            }

            val res = (allFuel / allMileage.toDouble()) * 100
            return if (res > 0) {
                if (res == Double.POSITIVE_INFINITY) Double.MAX_VALUE
                else res
            }
            else 0.0
        }
        return 0.0
    }

    private fun calculatedAvgFuelOfTwoNotes(n1: NoteItem, n2: NoteItem): Double {
        val distance = maxOf(n1.mileage, n2.mileage) - minOf(n1.mileage, n2.mileage)
        val res = (n1.liters / distance) * 100
        return if (res > 0) {
            if (res == Double.POSITIVE_INFINITY) Double.MAX_VALUE
            else res
        }
        else 0.0
    }

    private suspend fun updateMileage(newMileage: Int) {
        val cItem = getCarItemUseCase(carId)
        val oldMileage = cItem.mileage
        if (newMileage > oldMileage) {
            editCarItemUseCase(
                cItem.copy(mileage = newMileage)
            )
            updateCarItem()
        }
    }

    private fun refactorFuel(id: Int): Fuel {
        return noteFuelTypes[id]
    }

    private fun areFieldsValid(
        volume: Double,
        totalPrice: Double,
        price: Double,
        mileage: Int
    ): Boolean {
        if (volume <= 0.0) {
            _errorVolumeInput.value = true
            return false
        }
        if (totalPrice <= 0.0) {
            _errorTotalPriceInput.value = true
            return false
        }
        if (price <= 0.0) {
            _errorPriceInput.value = true
            return false
        }
        if (mileage <= 0) {
            _errorMileageInput.value = true
            return false
        }
        return true
    }

    fun setCarItem(id: Int) {
        carId = id
        viewModelScope.launch {
            _currCarItem.value = getCarItemUseCase(carId)
        }
    }

    private suspend fun updateCarItem() {
        _currCarItem.value = getCarItemUseCase(carId)
    }

    fun getFuelId(fuel: Fuel): Int {
        for ((i, f) in Fuel.values().withIndex()) {
            if (f == fuel) return i
        }
        return 0
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getNoteItemUseCase(id)
            _noteItem.value = item
            _noteDate.value = item.date
        }
    }

    fun resetMileageError() {
        _errorMileageInput.value = false
    }

    fun resetVolumeError() {
        _errorVolumeInput.value = false
    }

    fun resetTotalPriceError() {
        _errorTotalPriceInput.value = false
    }

    fun resetPriceError() {
        _errorPriceInput.value = false
    }

    fun setNoteDate(date: Long) {
        _noteDate.value = date
    }

    fun setLastRefillFuelType() {
        viewModelScope.launch {
            val notesByMileage = getNoteItemsListByMileageUseCase()
            notesByMileage.let {
                for (note in it) {
                    if (note.type == NoteType.FUEL) {
                        _lastFuelType.value = note.fuelType
                        break
                    }
                }
            }
        }
    }

    fun calculateVolume(amount: String?, price: String?) {
        val rAmount = refactorDouble(amount)
        val rPrice = refactorDouble(price)

        if (rAmount >= 0 && rPrice >= 0) {
            val res = rAmount / rPrice
            if (res in 0.0..100000.0) _calcVolume.value = rAmount / rPrice
            else _calcVolume.value = 0.0
        } else _calcVolume.value = 0.0

    }

    fun calculateAmount(volume: String?, price: String?) {
        val rVolume = refactorDouble(volume)
        val rPrice = refactorDouble(price)

        if (rVolume >= 0 && rPrice >= 0) {
            val res = rVolume * rPrice
            if (res in 0.0..100000.0) _calcAmount.value = rVolume * rPrice
            else _calcAmount.value = 0.0
        } else _calcAmount.value = 0.0
    }

    fun calculatePrice(amount: String?, volume: String?) {
        val rAmount = refactorDouble(amount)
        val rVolume = refactorDouble(volume)

        if (rAmount >= 0 && rVolume >= 0) {
            val res = rAmount / rVolume
            if (res in 0.0..100000.0) _calcPrice.value = res
            else _calcPrice.value = 0.0
        } else _calcPrice.value = 0.0
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val NOTE_TITLE_ID = R.string.text_refill
    }
}