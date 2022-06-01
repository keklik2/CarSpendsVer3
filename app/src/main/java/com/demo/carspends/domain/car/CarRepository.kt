package com.demo.carspends.domain.car

interface CarRepository {
    suspend fun addCarItemUseCase(carItem: CarItem)
    suspend fun deleteCarItemUseCase(carItem: CarItem)
    suspend fun editCarItemUseCase(carItem: CarItem)
    suspend fun getCarItemsListUseCase(): List<CarItem>
    suspend fun getCarItemUseCase(id: Int): CarItem
    suspend fun dropData()
}
