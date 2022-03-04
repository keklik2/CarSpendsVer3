package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository
import javax.inject.Inject

class AddComponentItemUseCase @Inject constructor(private val repository: ComponentRepository) {
    suspend operator fun invoke(componentItem: ComponentItem) {
        repository.addComponentItemUseCase(componentItem)
    }
}