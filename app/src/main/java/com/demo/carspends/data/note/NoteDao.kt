package com.demo.carspends.data.note

import androidx.lifecycle.LiveData
import androidx.room.*
import com.demo.carspends.domain.note.NoteType

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes_table WHERE date >= :date ORDER BY date DESC")
    fun getNotesListLD(date: Long): LiveData<List<NoteItemDbModel>>

    @Query("SELECT * FROM notes_table WHERE date >= :date AND type == :type ORDER BY date DESC")
    fun getNotesListLD(type: NoteType, date: Long): LiveData<List<NoteItemDbModel>>

    @Query("SELECT * FROM notes_table ORDER BY mileage DESC")
    suspend fun getNotesListByMileage(): List<NoteItemDbModel>

    @Query("SELECT * FROM notes_table WHERE id == :requestedId LIMIT 1")
    suspend fun getNoteById(requestedId: Int): NoteItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteItemDbModel: NoteItemDbModel)

    @Delete
    suspend fun deleteNote(noteItemDbModel: NoteItemDbModel)
}