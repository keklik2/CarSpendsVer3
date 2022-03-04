package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteRepository.Companion.ALL_TIME
import com.demo.carspends.domain.note.NoteType
import javax.inject.Inject

class GetNoteItemsListUseCase @Inject constructor(private val repository: NoteRepository) {
    operator fun invoke(date: Long = ALL_TIME) = repository.getNoteItemsListUseCase(date)
    operator fun invoke(type: NoteType, date: Long = ALL_TIME) = repository.getNoteItemsListUseCase(type, date)
}