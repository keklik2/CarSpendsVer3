package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository
import javax.inject.Inject

class DeleteCarItemUseCase @Inject constructor(private val repository: CarRepository) {
    suspend operator fun invoke(carItem: CarItem) = repository.deleteCarItemUseCase(carItem)
}