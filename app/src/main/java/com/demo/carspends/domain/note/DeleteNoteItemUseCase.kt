package com.demo.carspends.domain.note

class DeleteNoteItemUseCase(private val repository: NoteRepository) {
    operator fun invoke(noteItem: NoteItem) {
        repository.deleteNoteItemUseCase(noteItem)
    }
}