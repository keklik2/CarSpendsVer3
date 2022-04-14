package com.demo.carspends.domain.component

interface ComponentRepository {
    suspend fun addComponentItemUseCase(component: ComponentItem)
    suspend fun deleteComponentItemUseCase(component: ComponentItem)
    suspend fun editComponentItemUseCase(component: ComponentItem)
    suspend fun getComponentItemsListUseCase(delay: Long): List<ComponentItem>
    suspend fun getComponentItemUseCase(id: Int): ComponentItem
}
