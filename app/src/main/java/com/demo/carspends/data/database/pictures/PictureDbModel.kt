package com.demo.carspends.data.database.pictures

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures_table")
data class PictureDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = UNDEFINED_ID,
    val name: String,
    val uri: String,
    val noteId: Int
) {
    companion object {
        private const val UNDEFINED_ID = 0
    }
}
