package com.demo.carspends.data.database.repositoryImpls

import com.demo.carspends.data.database.mapper.NoteMapper
import com.demo.carspends.data.database.mapper.PictureMapper
import com.demo.carspends.data.database.note.NoteDao
import com.demo.carspends.data.database.pictures.PictureDao
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.picture.InternalPicture
import kotlinx.coroutines.*
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val pictureDao: PictureDao,
    private val mapper: NoteMapper,
    private val picturesMapper: PictureMapper
) : NoteRepository {

    override suspend fun deleteNoteItemUseCase(noteItem: NoteItem) {
        val pictures = noteDao.getNote(noteItem.id).picturesList
        noteDao.delete(mapper.mapEntityToNoteDbModel(noteItem))
        for (p in pictures) {
            pictureDao.delete(p)
        }
    }

    override suspend fun editNoteItemUseCase(noteItem: NoteItem) {
        noteDao.insert(mapper.mapEntityToNoteDbModel(noteItem))
    }

    override suspend fun getNoteItemsListUseCase(delay: Long, date: Long): List<NoteItem> {
        return noteDao.getNotes(date).map {
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
                    noteDao.getNotes(type, date).map {
                        mapper.mapNoteDbModelToEntity(it)
                    }
                } else {
                    noteDao.getNotes(date).map {
                        mapper.mapNoteDbModelToEntity(it)
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        }

    override suspend fun getNoteItemsListByMileageUseCase(): List<NoteItem> {
        return noteDao.getNotesForCounting().map {
            mapper.mapNoteDbModelToEntity(it)
        }
    }

    override suspend fun getNoteItemUseCase(id: Int): Map<NoteItem, List<InternalPicture>> {
        return mutableMapOf<NoteItem, List<InternalPicture>>().apply {
            val note = noteDao.getNote(id)
            put(mapper.mapNoteDbModelToEntity(note.note), note.picturesList.map { picturesMapper.mapDbModelToEntity(it) })
        }
    }

    override suspend fun addNoteItemUseCase(
        noteItem: NoteItem,
        pictures: List<InternalPicture>
    ) {
        noteDao.insert(mapper.mapEntityToNoteDbModel(noteItem))
        getNoteItemsListByMileageUseCase().firstOrNull {
            it.title == noteItem.title &&
            it.mileage == noteItem.mileage &&
            it.date == noteItem.date &&
            it.type == noteItem.type
        }?.let {
            for (p in pictures) {
                pictureDao.insert(picturesMapper.mapEntityToDbModel(it.id, p))
            }
        }
    }
}
