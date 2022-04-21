package com.demo.carspends.domain.picture

import javax.inject.Inject

class DeletePictureUseCase @Inject constructor(
    private val repository: PictureRepository
) {
    suspend operator fun invoke(noteId: Int, picture: InternalPicture) = repository.deletePicture(noteId, picture)
}
