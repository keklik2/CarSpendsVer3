package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository

class GetComponentItemsListLDUseCase(private val repository: ComponentRepository) {
    operator fun invoke() = repository.getComponentItemsListLD()
}