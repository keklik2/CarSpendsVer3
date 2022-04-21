package com.demo.carspends.data.database.note

import androidx.room.*
import com.demo.carspends.data.database.pictures.PictureDbModel
import com.demo.carspends.domain.note.NoteType

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes_table WHERE date >= :date ORDER BY date DESC")
    suspend fun getNotes(date: Long): List<NoteItemDbModel>

    @Query("SELECT * FROM notes_table WHERE date >= :date AND type == :type ORDER BY date DESC")
    suspend fun getNotes(type: NoteType, date: Long): List<NoteItemDbModel>

    @Query("SELECT * FROM notes_table ORDER BY mileage DESC")
    suspend fun getNotesForCounting(): List<NoteItemDbModel>

    @Transaction
    @Query("SELECT * FROM notes_table WHERE id == :requestedId LIMIT 1")
    suspend fun getNote(requestedId: Int): NoteWithPictureItem

//    @Transaction
//    @Query("SELECT * FROM notes_table WHERE id == :requestedId LIMIT 1")
//    suspend fun getNoteWithPicturesById(requestedId: Int): List<NoteWithPictureItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteItemDbModel: NoteItemDbModel)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertPicture(pictureDbModel: PictureDbModel)

    @Delete
    suspend fun delete(noteItemDbModel: NoteItemDbModel)
}
