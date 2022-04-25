package com.demo.carspends.data.database.repositoryImpls

import com.demo.carspends.data.database.mapper.NoteMapper
import com.demo.carspends.data.database.note.NoteDao
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.utils.MIN_LOADING_DELAY
import kotlinx.coroutines.*
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

    override suspend fun getNoteItemsListUseCase(delay: Long, date: Long): List<NoteItem> {
        return noteDao.getNotesList(date).map {
            mapper.mapNoteDbModelToEntity(it)
        }
    }

    override suspend fun getNoteItemsListUseCase(
        delay: Long,
        type: NoteType?,
        date: Long
    ): List<NoteItem> =
        withContext(Dispatchers.IO) {
            try {
                delay(delay)
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
