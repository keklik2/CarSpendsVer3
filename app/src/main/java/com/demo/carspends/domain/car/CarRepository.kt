package com.demo.carspends.domain.car

import androidx.lifecycle.LiveData

interface CarRepository {
    fun addCarItemUseCase(carItem: CarItem)
    fun deleteCarItemUseCase(carItem: CarItem)
    fun editCarItemUseCase(carItem: CarItem)
    fun getCarItemsListLD(): LiveData<List<CarItem>>
    fun getCarItemsList(): List<CarItem>
    fun getCarItem(id: Int): CarItem
}