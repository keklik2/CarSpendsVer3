package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository

class EditCarItemUseCase(private val repository: CarRepository) {
    suspend operator fun invoke(carItem: CarItem) = repository.editCarItemUseCase(carItem)
}