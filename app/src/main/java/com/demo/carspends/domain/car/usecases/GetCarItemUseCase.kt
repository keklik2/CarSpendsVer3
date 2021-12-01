package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarRepository

class GetCarItemUseCase(private val repository: CarRepository) {
    operator fun invoke(id: Int) = repository.getCarItemUseCase(id)
}