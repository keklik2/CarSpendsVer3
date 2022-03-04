package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository
import javax.inject.Inject

class GetComponentItemsListUseCase @Inject constructor(private val repository: ComponentRepository) {
    operator fun invoke() = repository.getComponentItemsListUseCase()
}