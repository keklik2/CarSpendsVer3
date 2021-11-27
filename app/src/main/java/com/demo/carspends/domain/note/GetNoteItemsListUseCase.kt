package com.demo.carspends.domain.note

class GetNoteItemsListUseCase(private val repository: NoteRepository) {
    operator fun invoke() = repository.getNoteItemsListLD()
}