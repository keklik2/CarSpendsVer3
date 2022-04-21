package com.demo.carspends.domain.note.usecases

import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteRepository
import com.demo.carspends.domain.picture.InternalPicture
import javax.inject.Inject

class AddNoteItemUseCase @Inject constructor(private val repository: NoteRepository) {
    suspend operator fun invoke(noteItem: NoteItem, pictures: List<InternalPicture>) = repository.addNoteItemUseCase(noteItem, pictures)
}
