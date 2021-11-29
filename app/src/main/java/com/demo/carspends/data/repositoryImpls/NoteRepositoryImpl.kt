package com.demo.carspends.data.repositoryImpls

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.MainDataBase
import com.demo.carspends.data.mapper.NoteMapper
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType

class NoteRepositoryImpl(private val application: Application) : NoteRepository {

    private val noteDao = MainDataBase.getInstance(application).noteDao()
    private val mapper = NoteMapper()

    override fun addNoteItemUseCase(noteItem: NoteItem) {
        noteDao.insertNote(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override fun deleteNoteItemUseCase(noteItem: NoteItem) {
        noteDao.deleteNote(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override fun editNoteItemUseCase(noteItem: NoteItem) {
        noteDao.deleteNote(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override fun getNoteItemsListLD(date: Long): LiveData<List<NoteItem>> {
        return Transformations.map(noteDao.getNotesListLD(date)) {
            it.map {
                mapper.mapNoteDbModelToEntity(it)
            }
        }
    }

    override fun getSelectedNoteItemsListLD(type: NoteType, date: Long): LiveData<List<NoteItem>> {
        return Transformations.map(noteDao.getSelectedNotesListLD(type, date)) {
            it.map {
                mapper.mapNoteDbModelToEntity(it)
            }
        }
    }

    override fun getNoteItemsList(): List<NoteItem> {
        return noteDao.getNotesList().map{
            mapper.mapNoteDbModelToEntity(it)
        }
    }

    override fun getNoteItem(id: Int): NoteItem {
        return mapper.mapNoteDbModelToEntity(noteDao.getNoteById(id))
    }


}