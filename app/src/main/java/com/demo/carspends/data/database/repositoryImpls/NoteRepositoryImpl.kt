package com.demo.carspends.data.database.repositoryImpls

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.demo.carspends.BuildConfig
import com.demo.carspends.data.database.mapper.NoteMapper
import com.demo.carspends.data.database.mapper.PictureMapper
import com.demo.carspends.data.database.note.NoteDao
import com.demo.carspends.data.database.pictures.PictureDao
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.picture.InternalPicture
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val pictureDao: PictureDao,
    private val mapper: NoteMapper,
    private val picturesMapper: PictureMapper,
    private val app: Application
) : NoteRepository {

    private val imageFolder = File(app.getExternalFilesDir(null), "images")

    override suspend fun deleteNoteItemUseCase(noteItem: NoteItem) {
        val pictures = noteDao.getNote(noteItem.id).picturesList
        noteDao.delete(mapper.mapEntityToNoteDbModel(noteItem))
        for (p in pictures) {
            if (deletePictureFromInternalStorage(p.name)) pictureDao.delete(p)
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
                val notes = if (type != null) {
                    noteDao.getNotes(type, date)
                } else {
                    noteDao.getNotes(date)
                }
                notes.map {
                    mapper.mapNoteDbModelToEntity(it, pictureDao.hasPicture(it.id).isNotEmpty())
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
            put(
                mapper.mapNoteDbModelToEntity(note.note),
                note.picturesList.map { picturesMapper.mapDbModelToEntity(it) })
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
                if (addPictureToExternalStorage(p)) {
                    pictureDao.insert(
                        picturesMapper.mapEntityToDbModel(
                            it.id,
                            p.copy(
                                uri = getPictureUri(p.name)
                            )
                        )
                    )
                }
            }
        }
    }

    private fun addPictureToExternalStorage(picture: InternalPicture): Boolean {
        return try {
            imageFolder.mkdirs()
            val file = File(imageFolder, picture.name)
            if (file.exists()) true
            else {
                val stream = FileOutputStream(file)
                val img = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            app.contentResolver,
                            picture.uri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(
                        app.contentResolver,
                        picture.uri
                    )
                }
                img?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                stream.flush()
                stream.close()
                true
            }
        } catch (e: IOException) { false }
    }

    private fun getPictureUri(name: String): Uri {
        imageFolder.mkdirs()
        return FileProvider.getUriForFile(
            app.applicationContext,
            BuildConfig.APPLICATION_ID + ".provider", File(imageFolder, name)
        )
    }

    private fun deletePictureFromInternalStorage(name: String): Boolean {
        return try {
            imageFolder.mkdirs()
            File(imageFolder, name).delete()
            true
        } catch (e: Exception) { false }
    }
}
