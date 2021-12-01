package com.demo.carspends.domain.component

import androidx.lifecycle.LiveData

interface ComponentRepository {
    fun addComponentItemUseCase(component: ComponentItem)
    fun deleteComponentItemUseCase(component: ComponentItem)
    fun editComponentItemUseCase(component: ComponentItem)
    fun getComponentItemsListUseCase(): LiveData<List<ComponentItem>>
    fun getComponentItemUseCase(id: Int): ComponentItem
}