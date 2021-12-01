package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteRepository

class GetNoteItemUseCase(private val repository: NoteRepository) {
    operator fun invoke(id: Int) = repository.getNoteItemUseCase(id)
}