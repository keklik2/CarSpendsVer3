package com.demo.carspends.domain.note

class AddNoteItemUseCase(private val repository: NoteRepository) {
    operator fun invoke(noteItem: NoteItem) = repository.addNoteItemUseCase(noteItem)
}