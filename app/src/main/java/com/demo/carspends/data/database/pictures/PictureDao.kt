package com.demo.carspends.data.database.pictures

import androidx.room.*

@Dao
interface PictureDao {

    @Query("SELECT * FROM pictures_table WHERE noteId == :noteId ORDER BY id DESC")
    suspend fun getPictures(noteId: Int): List<PictureDbModel>

    @Query("SELECT * FROM pictures_table WHERE noteId == :noteId ORDER BY id DESC LIMIT 1")
    suspend fun hasPicture(noteId: Int): List<PictureDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pictureDbModel: PictureDbModel)

    @Delete
    suspend fun delete(pictureDbModel: PictureDbModel)

    @Query("DELETE FROM pictures_table")
    suspend fun dropData()
}
