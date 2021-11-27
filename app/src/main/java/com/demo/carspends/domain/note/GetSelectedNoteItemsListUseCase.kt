package com.demo.carspends.domain.note

import com.demo.carspends.domain.note.NoteRepository.Companion.ALL_TIME

class GetSelectedNoteItemsListUseCase(private val repository: NoteRepository) {
    operator fun invoke(type: NoteType, date: Long = ALL_TIME) = repository.getSelectedNoteItemsListLD(type, date)
}