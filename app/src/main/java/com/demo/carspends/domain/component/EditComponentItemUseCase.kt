package com.demo.carspends.domain.component

class EditComponentItemUseCase(private val repository: ComponentRepository) {
    operator fun invoke(componentItem: ComponentItem) {
        repository.editComponentItemUseCase(componentItem)
    }
}