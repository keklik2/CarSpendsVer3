package com.demo.carspends.domain.component

class DeleteComponentItemUseCase(private val repository: ComponentRepository) {
    operator fun invoke(componentItem: ComponentItem) {
        repository.deleteComponentItemUseCase(componentItem)
    }
}