package com.demo.carspends.domain.note

import androidx.lifecycle.LiveData

interface NoteRepository {

    fun addNoteItemUseCase(noteItem: NoteItem)
    fun deleteNoteItemUseCase(noteItem: NoteItem)
    fun editNoteItemUseCase(noteItem: NoteItem)
    fun getNoteItemsListLD(date: Long): LiveData<List<NoteItem>>
    fun getSelectedNoteItemsListLD(type: NoteType, date: Long): LiveData<List<NoteItem>>
    fun getNoteItemsList(): List<NoteItem>
    fun getNoteItem(id: Int): NoteItem

    companion object {
        const val ALL_TIME = 0L
    }

}