package com.demo.carspends.data.repositoryImpls

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.MainDataBase
import com.demo.carspends.data.mapper.NoteMapper
import com.demo.carspends.data.note.NoteItemDbModel
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType

class NoteRepositoryImpl(private val application: Application) : NoteRepository {

    private val noteDao = MainDataBase.getInstance(application).noteDao()
    private val mapper = NoteMapper()

    override suspend fun addNoteItemUseCase(noteItem: NoteItem) {
        noteDao.insertNote(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override suspend fun deleteNoteItemUseCase(noteItem: NoteItem) {
        noteDao.deleteNote(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override suspend fun editNoteItemUseCase(noteItem: NoteItem) {
        noteDao.insertNote(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override fun getNoteItemsListUseCase(date: Long): LiveData<List<NoteItem>> {
        return Transformations.map(noteDao.getNotesListLD(date)) {
            it.map {
                mapper.mapNoteDbModelToEntity(it)
            }
        }
    }

    override fun getNoteItemsListUseCase(type: NoteType ,date: Long): LiveData<List<NoteItem>> {
        return Transformations.map(noteDao.getNotesListLD(type, date)) {
            it.map {
                mapper.mapNoteDbModelToEntity(it)
            }
        }
    }

    override fun getNoteItemsListByMileageUseCase(): LiveData<List<NoteItem>> {
        return Transformations.map(noteDao.getNotesListByMileage()) {
            it.map {
                mapper.mapNoteDbModelToEntity(it)
            }
        }
    }

    override suspend fun getNoteItemUseCase(id: Int): NoteItem {
        return mapper.mapNoteDbModelToEntity(noteDao.getNoteById(id))
    }
}