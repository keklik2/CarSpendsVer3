package com.demo.carspends.data.repositoryImpls

import android.util.Log
import com.demo.carspends.data.mapper.NoteMapper
import com.demo.carspends.data.note.NoteDao
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun getNoteItemsListUseCase(date: Long): List<NoteItem> {
        return noteDao.getNotesList(date).map {
                mapper.mapNoteDbModelToEntity(it)
            }
    }

    override suspend fun getNoteItemsListUseCase(type: NoteType?, date: Long): List<NoteItem> = withContext(Dispatchers.IO) {
        try {
            if (type != null) {
                noteDao.getNotesList(type, date).map {
                    mapper.mapNoteDbModelToEntity(it)
                }
            } else {
                noteDao.getNotesList(date).map {
                    mapper.mapNoteDbModelToEntity(it)
                }
            }
        } catch (e: Exception) {
            throw e
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
