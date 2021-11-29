package com.demo.carspends.domain.component

import androidx.lifecycle.LiveData

interface ComponentRepository {
    fun addComponentItemUseCase(component: ComponentItem)
    fun deleteComponentItemUseCase(component: ComponentItem)
    fun editComponentItemUseCase(component: ComponentItem)
    fun getComponentItemsListLD(): LiveData<List<ComponentItem>>
    fun getComponentItemsList(): List<ComponentItem>
    fun getComponentItem(id: Int): ComponentItem
}