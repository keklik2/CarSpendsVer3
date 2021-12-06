package com.demo.carspends.domain.note

import androidx.lifecycle.LiveData

interface NoteRepository {

    suspend fun addNoteItemUseCase(noteItem: NoteItem)
    suspend fun deleteNoteItemUseCase(noteItem: NoteItem)
    suspend fun editNoteItemUseCase(noteItem: NoteItem)
    fun getNoteItemsListUseCase(type: NoteType, date: Long): LiveData<List<NoteItem>>
    fun getNoteItemsListUseCase(date: Long): LiveData<List<NoteItem>>
    fun getNoteItemsListByMileageUseCase(): LiveData<List<NoteItem>>
    suspend fun getNoteItemUseCase(id: Int): NoteItem

    companion object {
        const val ALL_TIME = 0L
    }

}