package com.demo.carspends.domain.car

import androidx.lifecycle.LiveData

interface CarRepository {
    fun addCarItemUseCase(carItem: CarItem)
    fun deleteCarItemUseCase(carItem: CarItem)
    fun editCarItemUseCase(carItem: CarItem)
    fun getCarItemsListUseCase(): LiveData<List<CarItem>>
    fun getCarItemUseCase(id: Int): CarItem
}