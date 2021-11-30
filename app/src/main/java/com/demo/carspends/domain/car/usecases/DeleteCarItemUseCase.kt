package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository

class DeleteCarItemUseCase(private val repository: CarRepository) {
    operator fun invoke(carItem: CarItem) = repository.deleteCarItemUseCase(carItem)
}