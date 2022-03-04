package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository
import javax.inject.Inject

class DeleteComponentItemUseCase @Inject constructor(private val repository: ComponentRepository) {
    suspend operator fun invoke(componentItem: ComponentItem) {
        repository.deleteComponentItemUseCase(componentItem)
    }
}