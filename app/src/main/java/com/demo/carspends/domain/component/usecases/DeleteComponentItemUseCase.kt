package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository

class DeleteComponentItemUseCase(private val repository: ComponentRepository) {
    operator fun invoke(componentItem: ComponentItem) {
        repository.deleteComponentItemUseCase(componentItem)
    }
}