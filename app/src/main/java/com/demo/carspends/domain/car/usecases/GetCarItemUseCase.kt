package com.demo.carspends.domain.car.usecases

import com.demo.carspends.domain.car.CarRepository
import javax.inject.Inject

class GetCarItemUseCase @Inject constructor(private val repository: CarRepository) {
    suspend operator fun invoke(id: Int) = repository.getCarItemUseCase(id)
}