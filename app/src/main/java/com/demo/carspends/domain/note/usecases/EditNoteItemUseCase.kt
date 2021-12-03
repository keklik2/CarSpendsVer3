package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository

class EditNoteItemUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteItem: NoteItem) {
        repository.editNoteItemUseCase(noteItem)
    }
}