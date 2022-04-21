package com.demo.carspends.data.database.repositoryImpls

import com.demo.carspends.data.database.mapper.PictureMapper
import com.demo.carspends.data.database.pictures.PictureDao
import com.demo.carspends.domain.picture.InternalPicture
import com.demo.carspends.domain.picture.PictureRepository
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val dao: PictureDao,
    private val mapper: PictureMapper
): PictureRepository {
    override suspend fun deletePicture(noteId: Int, picture: InternalPicture) {
        dao.delete(mapper.mapEntityToDbModel(noteId, picture))
    }
}
