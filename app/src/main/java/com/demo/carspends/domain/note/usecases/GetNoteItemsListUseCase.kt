package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.note.NoteRepository.Companion.ALL_TIME
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.utils.MIN_LOADING_DELAY
import javax.inject.Inject

class GetNoteItemsListUseCase @Inject constructor(private val repository: NoteRepository) {
    suspend operator fun invoke(delay: Long = MIN_LOADING_DELAY, date: Long = ALL_TIME) = repository.getNoteItemsListUseCase(delay, date)
    suspend operator fun invoke(delay: Long = MIN_LOADING_DELAY, type: NoteType?, date: Long = ALL_TIME) = repository.getNoteItemsListUseCase(delay, type, date)
}
