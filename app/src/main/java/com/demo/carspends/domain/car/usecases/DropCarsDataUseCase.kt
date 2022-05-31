package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarRepository
import javax.inject.Inject

class DropCarsDataUseCase @Inject constructor(private val repository: CarRepository) {
    suspend operator fun invoke() = repository.dropData()
}
