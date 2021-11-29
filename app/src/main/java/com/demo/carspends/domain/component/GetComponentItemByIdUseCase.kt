package com.demo.carspends.domain.component

class GetComponentItemByIdUseCase(private val repository: ComponentRepository) {
    operator fun invoke(id: Int) = repository.getComponentItem(id)
}