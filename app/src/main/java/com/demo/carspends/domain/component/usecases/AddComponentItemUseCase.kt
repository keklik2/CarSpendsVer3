package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository

class AddComponentItemUseCase(private val repository: ComponentRepository) {
    operator fun invoke(componentItem: ComponentItem) {
        repository.addComponentItemUseCase(componentItem)
    }
}