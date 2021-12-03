package com.demo.carspends.domain.car

import androidx.lifecycle.LiveData

interface CarRepository {
    suspend fun addCarItemUseCase(carItem: CarItem)
    suspend fun deleteCarItemUseCase(carItem: CarItem)
    suspend fun editCarItemUseCase(carItem: CarItem)
    fun getCarItemsListUseCase(): LiveData<List<CarItem>>
    suspend fun getCarItemUseCase(id: Int): CarItem
}