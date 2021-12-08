package com.demo.carspends.presentation.fragments.notesListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class NotesListViewModel(app: Application): AndroidViewModel(app) {

    private val repository = NoteRepositoryImpl(app)
    private val carRepository = CarRepositoryImpl(app)

    private val deleteNoteItemUseCase = DeleteNoteItemUseCase(repository)
    private val getNoteItemsListByMileageUseCase = GetNoteItemsListByMileageUseCase(repository)
    private val getCarItemUseCase = GetCarItemUseCase(carRepository)
    private val editCarItemUseCase = EditCarItemUseCase(carRepository)

    private val _carsList = GetCarItemsListLDUseCase(carRepository).invoke()
    val carsList get() = _carsList

    private val _currCarItem = MutableLiveData<CarItem>()

    private val dateTest = 0L
    private val _notesList = GetNoteItemsListUseCase(repository).invoke(dateTest)
    val notesList get() = _notesList

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            deleteNoteItemUseCase(note)
            rollbackCarMileage(note.type)
        }
    }

    fun setCarItem(id: Int) {
        viewModelScope.launch {
            _currCarItem.value = getCarItemUseCase(id)
        }
    }

    private fun rollbackCarMileage(noteType: NoteType) {
        if (noteType != NoteType.EXTRA) {
            val cItem = _currCarItem.value
            viewModelScope.launch {
                val notesList = getNoteItemsListByMileageUseCase()
                notesList?.let {
                    cItem?.let {
                        var newMileage = cItem.startMileage
                        if (notesList.isNotEmpty()) {
                            for (i in notesList) {
                                if (i.mileage > newMileage) newMileage = i.mileage
                            }
                        }
                        viewModelScope.launch {
                            editCarItemUseCase(cItem.copy(
                                mileage = newMileage
                            ))
                        }
                    }
                }
            }
        }
    }
}