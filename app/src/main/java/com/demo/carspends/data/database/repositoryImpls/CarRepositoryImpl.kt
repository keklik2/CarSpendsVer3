package com.demo.carspends.data.database.repositoryImpls

import com.demo.carspends.data.database.car.CarDao
import com.demo.carspends.data.database.mapper.CarMapper
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val carDao: CarDao,
    private val mapper: CarMapper
) : CarRepository {

    override suspend fun addCarItemUseCase(carItem: CarItem) {
            carDao.insert(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override suspend fun deleteCarItemUseCase(carItem: CarItem) {
            carDao.delete(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override suspend fun editCarItemUseCase(carItem: CarItem) {
            carDao.insert(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override suspend fun getCarItemsListUseCase(): List<CarItem> = withContext(Dispatchers.IO) {
        try {
            val list = carDao.getCars()
            list.map {
                mapper.mapCarItemDbModelToEntity(it)
            }
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun getCarItemUseCase(id: Int): CarItem = withContext(Dispatchers.IO) {
        try {
            mapper.mapCarItemDbModelToEntity(carDao.getCar(id))
        } catch (e: Exception) {
            throw e
        }
    }
}
