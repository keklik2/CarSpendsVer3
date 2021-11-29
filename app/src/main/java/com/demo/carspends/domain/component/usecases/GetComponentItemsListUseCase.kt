package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository

class GetComponentItemsListUseCase(private val repository: ComponentRepository) {
    operator fun invoke() = repository.getComponentItemsListLD()
}