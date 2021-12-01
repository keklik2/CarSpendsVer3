package com.demo.carspends.presentation.fragments.notesListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListUseCase
import com.demo.carspends.domain.others.Fuel
import java.util.*

class NotesListViewModel(app: Application): AndroidViewModel(app) {

    private val carRepository = CarRepositoryImpl(app)
    private val noteRepository = NoteRepositoryImpl(app)

    private val addNoteItemUseCase = AddNoteItemUseCase(noteRepository)

    private val _carsList = GetCarItemsListLDUseCase(carRepository).invoke()
    val carsList get() = _carsList

    private val dateTest = 0L
    private val _notesList = GetNoteItemsListUseCase(noteRepository).invoke(dateTest)
    val notesList get() = _notesList

    fun addNote() {
        addNoteItemUseCase(
            NoteItem(
            0,
            "test",
            10.0,
            10.0,
            10.0,
            133000,
                GregorianCalendar.getInstance().timeInMillis,
                NoteType.FUEL,
                Fuel.F95
            )
        )
    }
}