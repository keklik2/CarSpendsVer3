package com.demo.carspends.presentation.fragments.notesListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.*
import com.demo.carspends.domain.others.Fuel
import kotlinx.coroutines.launch
import java.util.*

class NotesListViewModel(app: Application): AndroidViewModel(app) {

    private val carRepository = CarRepositoryImpl(app)
    private val noteRepository = NoteRepositoryImpl(app)

    private val deleteNoteItemUseCase = DeleteNoteItemUseCase(noteRepository)

    private val _carsList = GetCarItemsListLDUseCase(carRepository).invoke()
    val carsList get() = _carsList

    private val dateTest = 0L
    private val _notesList = GetNoteItemsListUseCase(noteRepository).invoke(dateTest)
    val notesList get() = _notesList

//    init {
//        TODO("Set car's name, avgFuel & avgPrice. Use ID = 0 (val startCarID = 0)")
//    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            deleteNoteItemUseCase(note)
        }
    }
}