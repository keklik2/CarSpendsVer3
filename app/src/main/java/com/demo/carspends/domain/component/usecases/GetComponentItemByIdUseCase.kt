package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository
import javax.inject.Inject

class GetComponentItemByIdUseCase @Inject constructor(private val repository: ComponentRepository) {
    suspend operator fun invoke(id: Int) = repository.getComponentItemUseCase(id)
}