package com.demo.carspends.domain.note

class EditNoteItemUseCase(private val repository: NoteRepository) {
    operator fun invoke(noteItem: NoteItem) {
        repository.editNoteItemUseCase(noteItem)
    }
}