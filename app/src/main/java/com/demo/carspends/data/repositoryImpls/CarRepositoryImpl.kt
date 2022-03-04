package com.demo.carspends.data.repositoryImpls

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.car.CarDao
import com.demo.carspends.data.mapper.CarMapper
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val carDao: CarDao,
    private val mapper: CarMapper
) : CarRepository {

    override suspend fun addCarItemUseCase(carItem: CarItem) {
        carDao.insertCar(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override suspend fun deleteCarItemUseCase(carItem: CarItem) {
        carDao.deleteCar(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override suspend fun editCarItemUseCase(carItem: CarItem) {
        carDao.insertCar(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override fun getCarItemsListUseCase(): LiveData<List<CarItem>> {
        return Transformations.map(carDao.getCarsListLD()) {
            it.map {
                mapper.mapCarItemDbModelToEntity(it)
            }
        }
    }

    override suspend fun getCarItemUseCase(id: Int): CarItem {
        return mapper.mapCarItemDbModelToEntity(carDao.getCarById(id))
    }
}