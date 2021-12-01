package com.demo.carspends.data.repositoryImpls

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.MainDataBase
import com.demo.carspends.data.mapper.CarMapper
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.CarRepository

class CarRepositoryImpl(private val app: Application): CarRepository {

    private val carDao = MainDataBase.getInstance(app).carDao()
    private val mapper = CarMapper()

    override fun addCarItemUseCase(carItem: CarItem) {
        carDao.insertCar(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override fun deleteCarItemUseCase(carItem: CarItem) {
        carDao.deleteCar(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override fun editCarItemUseCase(carItem: CarItem) {
        carDao.insertCar(mapper.mapEntityToCarItemDbModel(carItem))
    }

    override fun getCarItemsListUseCase(): LiveData<List<CarItem>> {
        return Transformations.map(carDao.getCarsListLD()) {
            it.map {
                mapper.mapCarItemDbModelToEntity(it)
            }
        }
    }

    override fun getCarItemUseCase(id: Int): CarItem {
        return mapper.mapCarItemDbModelToEntity(carDao.getCarById(id))
    }
}