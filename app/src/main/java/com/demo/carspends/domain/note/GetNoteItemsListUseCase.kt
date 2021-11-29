package com.demo.carspends.domain.note

import com.demo.carspends.domain.note.NoteRepository.Companion.ALL_TIME

class GetNoteItemsListUseCase(private val repository: NoteRepository) {
    operator fun invoke(date: Long = ALL_TIME) = repository.getNoteItemsListLD(date)
}