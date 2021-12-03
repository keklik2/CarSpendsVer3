package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository

class GetComponentItemByIdUseCase(private val repository: ComponentRepository) {
    suspend operator fun invoke(id: Int) = repository.getComponentItemUseCase(id)
}