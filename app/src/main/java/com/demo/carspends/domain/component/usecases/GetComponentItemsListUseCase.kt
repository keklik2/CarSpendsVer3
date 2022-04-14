package com.demo.carspends.domain.component.usecases

import com.demo.carspends.domain.component.ComponentRepository
import com.demo.carspends.utils.MIN_LOADING_DELAY
import javax.inject.Inject

class GetComponentItemsListUseCase @Inject constructor(private val repository: ComponentRepository) {
    suspend operator fun invoke(delay: Long = MIN_LOADING_DELAY) = repository.getComponentItemsListUseCase(delay)
}
