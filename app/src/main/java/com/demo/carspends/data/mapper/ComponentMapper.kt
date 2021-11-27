package com.demo.carspends.data.mapper

import com.demo.carspends.data.component.ComponentItemDbModel
import com.demo.carspends.domain.component.ComponentItem

class ComponentMapper {

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