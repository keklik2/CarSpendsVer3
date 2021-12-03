package com.demo.carspends.data.mapper

import com.demo.carspends.data.car.CarItemDbModel
import com.demo.carspends.domain.car.CarItem

class CarMapper {

    fun mapCarItemDbModelToEntity(carItem: CarItemDbModel) = CarItem(
        carItem.id,
        carItem.title,
        carItem.mileage,
        carItem.engineVolume,
        carItem.power,
        carItem.avgFuel,
        carItem.momentFuel,
        carItem.milPrice
    )

    fun mapEntityToCarItemDbModel(entity: CarItem) = CarItemDbModel(
        entity.id,
        entity.title,
        entity.mileage,
        entity.engineVolume,
        entity.power,
        entity.avgFuel,
        entity.momentFuel,
        entity.milPrice
    )
}