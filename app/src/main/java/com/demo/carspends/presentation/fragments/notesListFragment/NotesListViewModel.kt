package com.demo.carspends.presentation.fragments.notesListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.*
import kotlinx.coroutines.launch

class NotesListViewModel(app: Application) : AndroidViewModel(app) {

    private var carId = CarItem.UNDEFINED_ID

    private val repository = NoteRepositoryImpl(app)
    private val carRepository = CarRepositoryImpl(app)

    private val getNoteItemsListUseCase = GetNoteItemsListUseCase(repository)
    private val deleteNoteItemUseCase = DeleteNoteItemUseCase(repository)
    private val getNoteItemsListByMileageUseCase = GetNoteItemsListByMileageUseCase(repository)
    private val getCarItemUseCase = GetCarItemUseCase(carRepository)
    private val editCarItemUseCase = EditCarItemUseCase(carRepository)

    private val _carsList = GetCarItemsListLDUseCase(carRepository).invoke()
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
                calculateAvgPrice()

                if (noteType == NoteType.FUEL) {
                    calculateAvgFuel()
                }
            }
        }
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
        val notes = getNoteItemsListByMileageUseCase()
        val cItem = getCarItemUseCase(carId)
        val avgPrice =
            if (notes.isNotEmpty()) calculateAvgPriceOfAll(cItem.startMileage, notes)
            else START_AVG

        editCarItemUseCase(
            cItem.copy(
                milPrice = avgPrice
            )
        )
        updateCarItem()
    }

    private fun calculateAvgPriceOfAll(startMil: Int, notes: List<NoteItem>): Double {
        val list = mutableListOf<NoteItem>()
        for (i in notes) {
            if (i.type != NoteType.EXTRA) list.add(i)
        }

        if (list.size > 1) {
            val lastNote = list[list.size - 1]
            val allMileage =
                if (startMil < lastNote.mileage) list[0].mileage - startMil
                else list[0].mileage - lastNote.mileage
            var allPrice = 0.0
            for (i in 0 until list.size) {
                allPrice += list[i].totalPrice
            }

            val res = allPrice / allMileage.toDouble()
            return if (res > 0) res
            else 0.0
        }
        return 0.0
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