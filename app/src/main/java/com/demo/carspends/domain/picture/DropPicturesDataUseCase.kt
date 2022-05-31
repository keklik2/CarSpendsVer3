package com.demo.carspends.domain.picture

import javax.inject.Inject

class DropPicturesDataUseCase @Inject constructor(private val repository: PictureRepository) {
    suspend operator fun invoke() = repository.dropData()
}
