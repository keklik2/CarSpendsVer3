package com.demo.carspends.domain.note

class GetNoteItemUseCase(private val repository: NoteRepository) {
    operator fun invoke(id: Int) = repository.getNoteItem(id)
}