package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarRepository
import javax.inject.Inject

class GetCarItemsListLDUseCase @Inject constructor(private val repository: CarRepository) {
    operator fun invoke() = repository.getCarItemsListUseCase()
}