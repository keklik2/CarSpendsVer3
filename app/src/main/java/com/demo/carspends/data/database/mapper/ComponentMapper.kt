package com.demo.carspends.data.database.mapper

import com.demo.carspends.data.database.component.ComponentItemDbModel
import com.demo.carspends.domain.component.ComponentItem
import javax.inject.Inject

class ComponentMapper @Inject constructor(){

    fun mapComponentItemDbModelToEntity(componentItemDbModel: ComponentItemDbModel): ComponentItem
    = ComponentItem(
        componentItemDbModel.id,
        componentItemDbModel.title,
        componentItemDbModel.startMileage,
        componentItemDbModel.resourceMileage,
        componentItemDbModel.date
    )

    fun mapEntityToComponentItemDbModel(entity: ComponentItem): ComponentItemDbModel
    = ComponentItemDbModel(
        entity.id,
        entity.title,
        entity.startMileage,
        entity.resourceMileage,
        entity.date
    )
}
