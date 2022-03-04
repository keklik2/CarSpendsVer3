package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteRepository
import javax.inject.Inject

class GetNoteItemsListByMileageUseCase @Inject constructor(private val repository: NoteRepository) {
    suspend operator fun invoke() = repository.getNoteItemsListByMileageUseCase()
}