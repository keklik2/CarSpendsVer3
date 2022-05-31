package com.demo.carspends.domain.picture

interface PictureRepository {
    suspend fun deletePicture(noteId: Int, picture: InternalPicture)
    suspend fun dropData()
}
