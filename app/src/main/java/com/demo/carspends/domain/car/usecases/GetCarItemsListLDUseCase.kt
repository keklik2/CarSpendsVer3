package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarRepository

class GetCarItemsListLDUseCase(private val repository: CarRepository) {
    operator fun invoke() = repository.getCarItemsListLD()
}