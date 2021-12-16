package com.demo.carspends.data.mapper

import com.demo.carspends.data.car.CarItemDbModel
import com.demo.carspends.domain.car.CarItem

class CarMapper {

    fun mapCarItemDbModelToEntity(carItem: CarItemDbModel) = CarItem(
        carItem.id,
        carItem.title,
        carItem.startMileage,
        carItem.mileage,
        carItem.engineVolume,
        carItem.power,
        carItem.avgFuel,
        carItem.momentFuel,
        carItem.allFuel,
        carItem.fuelPrice,
        carItem.milPrice,
        carItem.allPrice,
        carItem.allMileage
    )

    fun mapEntityToCarItemDbModel(entity: CarItem) = CarItemDbModel(
        entity.id,
        entity.title,
        entity.startMileage,
        entity.mileage,
        entity.engineVolume,
        entity.power,
        entity.avgFuel,
        entity.momentFuel,
        entity.allFuel,
        entity.fuelPrice,
        entity.milPrice,
        entity.allPrice,
        entity.allMileage
    )
}