package com.demo.carspends.domain.component

class AddComponentItemUseCase(private val repository: ComponentRepository) {
    operator fun invoke(componentItem: ComponentItem) {
        repository.addComponentItemUseCase(componentItem)
    }
}