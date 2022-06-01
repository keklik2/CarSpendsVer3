package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository
import javax.inject.Inject

class DropComponentsDataUseCase @Inject constructor(private val repository: ComponentRepository) {
    suspend operator fun invoke() = repository.dropData()
}
