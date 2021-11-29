package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteRepository.Companion.ALL_TIME
import com.demo.carspends.domain.note.NoteType

class GetSelectedNoteItemsListUseCase(private val repository: NoteRepository) {
    operator fun invoke(type: NoteType, date: Long = ALL_TIME) = repository.getSelectedNoteItemsListLD(type, date)
}