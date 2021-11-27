package com.demo.carspends.data.note

import androidx.lifecycle.LiveData
import androidx.room.*
import com.demo.carspends.domain.note.NoteItem

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getNotesListLD(): LiveData<List<NoteItemDbModel>>

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getNotesList(): List<NoteItemDbModel>

    @Query("SELECT * FROM notes WHERE id == :requestedId LIMIT 1")
    fun getNoteById(requestedId: Int): NoteItemDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(noteItemDbModel: NoteItemDbModel)

    @Delete
    fun deleteNote(noteItemDbModel: NoteItemDbModel)
}