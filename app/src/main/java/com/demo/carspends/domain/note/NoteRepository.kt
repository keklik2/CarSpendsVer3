package com.demo.carspends.domain.note

import androidx.lifecycle.LiveData

interface NoteRepository {

    fun addNoteItemUseCase(noteItem: NoteItem)
    fun deleteNoteItemUseCase(noteItem: NoteItem)
    fun editNoteItemUseCase(noteItem: NoteItem)
    fun getNoteItemsListLD(): LiveData<List<NoteItem>>
    fun getNoteItemsList(): List<NoteItem>
    fun getNoteItem(id: Int): NoteItem

}