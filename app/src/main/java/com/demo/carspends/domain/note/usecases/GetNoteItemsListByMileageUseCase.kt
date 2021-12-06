package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteRepository

class GetNoteItemsListByMileageUseCase(private val repository: NoteRepository) {
    operator fun invoke() = repository.getNoteItemsListByMileageUseCase()
}