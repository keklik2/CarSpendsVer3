package com.demo.carspends.domain.note

interface NoteRepository {

    suspend fun addNoteItemUseCase(noteItem: NoteItem)
    suspend fun deleteNoteItemUseCase(noteItem: NoteItem)
    suspend fun editNoteItemUseCase(noteItem: NoteItem)
    suspend fun getNoteItemsListUseCase(delay: Long, type: NoteType?, date: Long): List<NoteItem>
    suspend fun getNoteItemsListUseCase(delay: Long, date: Long): List<NoteItem>
    suspend fun getNoteItemsListByMileageUseCase(): List<NoteItem>
    suspend fun getNoteItemUseCase(id: Int): NoteItem

    companion object {
        const val ALL_TIME = 0L
    }

}
