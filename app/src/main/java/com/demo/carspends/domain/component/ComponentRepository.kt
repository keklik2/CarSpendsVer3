package com.demo.carspends.domain.component

import androidx.lifecycle.LiveData

interface ComponentRepository {
    suspend fun addComponentItemUseCase(component: ComponentItem)
    suspend fun deleteComponentItemUseCase(component: ComponentItem)
    suspend  fun editComponentItemUseCase(component: ComponentItem)
    suspend fun getComponentItemsListUseCase(): List<ComponentItem>
    suspend fun getComponentItemUseCase(id: Int): ComponentItem
}
