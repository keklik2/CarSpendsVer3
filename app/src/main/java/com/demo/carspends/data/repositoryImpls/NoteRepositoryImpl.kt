package com.demo.carspends.data.repositoryImpls

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.mapper.NoteMapper
import com.demo.carspends.data.note.NoteDao
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val mapper: NoteMapper
) : NoteRepository {

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

    override fun getNoteItemsListUseCase(type: NoteType, date: Long): LiveData<List<NoteItem>> {
        return Transformations.map(noteDao.getNotesListLD(type, date)) {
            it.map {
                mapper.mapNoteDbModelToEntity(it)
            }
        }
    }

    override suspend fun getNoteItemsListByMileageUseCase(): List<NoteItem> {
        return noteDao.getNotesListByMileage().map {
            mapper.mapNoteDbModelToEntity(it)
        }
    }

    override suspend fun getNoteItemUseCase(id: Int): NoteItem {
        return mapper.mapNoteDbModelToEntity(noteDao.getNoteById(id))
    }
}