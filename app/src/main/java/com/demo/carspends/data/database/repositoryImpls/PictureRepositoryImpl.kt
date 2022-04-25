package com.demo.carspends.data.database.repositoryImpls

import android.app.Application
import android.util.Log
import com.demo.carspends.data.database.mapper.PictureMapper
import com.demo.carspends.data.database.pictures.PictureDao
import com.demo.carspends.domain.picture.InternalPicture
import com.demo.carspends.domain.picture.PictureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val dao: PictureDao,
    private val mapper: PictureMapper,
    private val app: Application
): PictureRepository {

    private val imageFolder = File(app.getExternalFilesDir(null), "images")

    override suspend fun deletePicture(noteId: Int, picture: InternalPicture) {
        if (deletePictureFromInternalStorage(picture.name))
            dao.delete(mapper.mapEntityToDbModel(noteId, picture))
    }

    private fun deletePictureFromInternalStorage(name: String): Boolean {
        return try {
            imageFolder.mkdirs()
            val file = File(imageFolder, name)
            if (file.exists()) file.delete()
            else false
        } catch (e: Exception) { false }
    }
}
