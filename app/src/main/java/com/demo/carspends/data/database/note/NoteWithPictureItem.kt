package com.demo.carspends.data.database.note

import androidx.room.Embedded
import androidx.room.Relation
import com.demo.carspends.data.database.pictures.PictureDbModel

class NoteWithPictureItem (
    @Embedded
    val note: NoteItemDbModel,
    @Relation(parentColumn = "id", entity = PictureDbModel::class, entityColumn = "noteId")
    val picturesList: List<PictureDbModel>
)
