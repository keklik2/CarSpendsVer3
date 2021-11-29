package com.demo.carspends.domain.component

class GetComponentItemsListUseCase(private val repository: ComponentRepository) {
    operator fun invoke() = repository.getComponentItemsListLD()
}