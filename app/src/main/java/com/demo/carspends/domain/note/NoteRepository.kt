package com.demo.carspends.domain.note

import com.demo.carspends.domain.picture.InternalPicture

interface NoteRepository {

    suspend fun addNoteItemUseCase(noteItem: NoteItem, pictures: List<InternalPicture>)
    suspend fun deleteNoteItemUseCase(noteItem: NoteItem)
    suspend fun editNoteItemUseCase(noteItem: NoteItem)
    suspend fun getNoteItemsListUseCase(delay: Long, type: NoteType?, date: Long): List<NoteItem>
    suspend fun getNoteItemsListUseCase(delay: Long, date: Long): List<NoteItem>
    suspend fun getNoteItemsListByMileageUseCase(): List<NoteItem>
    suspend fun getNoteItemUseCase(id: Int): Map<NoteItem, List<InternalPicture>>
    suspend fun dropData()

    companion object {
        const val ALL_TIME = 0L
    }

}
