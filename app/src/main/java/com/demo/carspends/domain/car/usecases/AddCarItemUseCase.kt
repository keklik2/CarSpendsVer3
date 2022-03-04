package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository
import javax.inject.Inject

class AddCarItemUseCase @Inject constructor(private val repository: CarRepository) {
    suspend operator fun invoke(carItem: CarItem) = repository.addCarItemUseCase(carItem)
}