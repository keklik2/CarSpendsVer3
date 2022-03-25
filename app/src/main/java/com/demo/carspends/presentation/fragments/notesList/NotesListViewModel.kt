package com.demo.carspends.presentation.fragments.notesList

import androidx.lifecycle.*
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NotesListViewModel @Inject constructor(
    private val getNoteItemsListUseCase: GetNoteItemsListUseCase,
    private val deleteNoteItemUseCase: DeleteNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getCarItemUseCase: GetCarItemUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val getCarItemsListLDUseCase: GetCarItemsListLDUseCase
) : ViewModel() {

    private var carId = CarItem.UNDEFINED_ID

    private val _carsList = getCarItemsListLDUseCase()
    val carsList get() = _carsList

    private val _currCarItem = MutableLiveData<CarItem>()

    private var _notesList: LiveData<List<NoteItem>> = getNoteItemsListUseCase(ALL_TIME)
    val notesList get() = _notesList

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            val noteType = note.type
            deleteNoteItemUseCase(note)

            if (noteType != NoteType.EXTRA) {
                rollbackCarMileage()
                calculateAllMileage()

                if (noteType == NoteType.FUEL) {
                    calculateAvgFuel()
                    calculateAllFuel(note)
                    calculateAllFuelPrice(note)
                }
            }

            rollbackAllPrice(note)
            calculateAvgPrice()
        }
    }

    private suspend fun calculateAllFuelPrice(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        val price = carItem.fuelPrice - note.totalPrice
        val finalAllFuelPrice =
            if (price < 0) 0.0
            else price

        editCarItemUseCase(
            carItem.copy(
                fuelPrice = finalAllFuelPrice
            )
        )
    }

    private suspend fun calculateAllFuel(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        val liters = carItem.allFuel - note.liters
        val finalAllFuel =
            if (liters < 0) 0.0
            else liters

        editCarItemUseCase(
            carItem.copy(
                allFuel = finalAllFuel
            )
        )
    }

    private suspend fun calculateAllMileage() {
        val carItem = getCarItemUseCase(carId)
        val notes = getNoteItemsListByMileageUseCase()

        val resMil = if (notes.isNotEmpty()) {
            val maxMil = max(carItem.mileage, notes[0].mileage)
            val minMil =
                if (notes[notes.size - 1].type != NoteType.EXTRA) min(
                    carItem.startMileage,
                    notes[notes.size - 1].mileage
                )
                else carItem.startMileage
            abs(maxMil - minMil)
        } else 0

        editCarItemUseCase(
            carItem.copy(
                allMileage = resMil
            )
        )
    }

    private suspend fun rollbackAllPrice(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        editCarItemUseCase(
            carItem.copy(
                allPrice = carItem.allPrice - note.totalPrice
            )
        )
    }

    private suspend fun calculateAvgFuel() {
        val notes = getNoteItemsListByMileageUseCase()
        val cCarItem = getCarItemUseCase(carId)
        var momentFuel = START_AVG
        var avgFuel = START_AVG
        if (notes.size > 1) {
            val listOfFuel = mutableListOf<NoteItem>()
            for (i in notes) {
                if (i.type == NoteType.FUEL) listOfFuel.add(i)
            }
            if (listOfFuel.size > 1) {
                val note1 = listOfFuel[0]
                val note2 = listOfFuel[1]
                if (note2 != note1) {
                    momentFuel = calculatedAvgFuelOfTwoNotes(note1, note2)
                    avgFuel = calculateAvgFuelOfAll(listOfFuel)
                }
            }
        }
        editCarItemUseCase(
            cCarItem.copy(
                momentFuel = momentFuel,
                avgFuel = avgFuel
            )
        )
        updateCarItem()
    }

    private suspend fun calculateAvgPrice() {
        val carItem = getCarItemUseCase(carId)
        val newMilPrice =
            if (carItem.allPrice > 0 && carItem.allMileage > 0) carItem.allPrice / carItem.allMileage
            else 0.0
        editCarItemUseCase(
            carItem.copy(
                milPrice = newMilPrice
            )
        )
    }

    private fun calculateAvgFuelOfAll(listOfFuel: List<NoteItem>): Double {
        if (listOfFuel.size > 1) {
            var allMileage = listOfFuel[0].mileage - listOfFuel[listOfFuel.size - 1].mileage
            var allFuel = 0.0
            for (i in 0 until listOfFuel.size - 1) {
                allFuel += listOfFuel[i].liters
            }

            val res = (allFuel / allMileage.toDouble()) * 100
            return if (res > 0) res
            else 0.0
        }
        return 0.0
    }

    private fun calculatedAvgFuelOfTwoNotes(n1: NoteItem, n2: NoteItem): Double {
        val distance = maxOf(n1.mileage, n2.mileage) - minOf(n1.mileage, n2.mileage)
        val res = (n1.liters / distance) * 100
        return if (res < 0) 0.0
        else res
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

    fun setCarItem(id: Int) {
        viewModelScope.launch {
            carId = id
            _currCarItem.value = getCarItemUseCase(carId)
        }
    }

    private suspend fun updateCarItem() {
        _currCarItem.value = getCarItemUseCase(carId)
    }

    fun setAllNotes(date: Long = ALL_TIME) {
        _notesList = getNoteItemsListUseCase(date)
    }

    fun setTypedNotes(type: NoteType, date: Long = ALL_TIME) {
        _notesList = getNoteItemsListUseCase(type, date)
    }

    companion object {
        private const val ALL_TIME = 0L
        private const val START_MIL = 0
        private const val START_AVG = 0.0
    }
}