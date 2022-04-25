package com.demo.carspends.data.database.mapper

import android.net.Uri
import com.demo.carspends.data.database.pictures.PictureDbModel
import com.demo.carspends.domain.picture.InternalPicture
import javax.inject.Inject

class PictureMapper @Inject constructor(){

    fun mapEntityToDbModel(noteId: Int, entity: InternalPicture) = PictureDbModel(
        entity.id,
        entity.name,
        entity.uri.toString(),
        noteId
    )

    fun mapDbModelToEntity(dbModel: PictureDbModel) = InternalPicture(
        dbModel.id,
        dbModel.name,
        Uri.parse(dbModel.uri)
    )
}
