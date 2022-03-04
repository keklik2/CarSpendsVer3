package com.demo.carspends.data.repositoryImpls

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.component.ComponentDao
import com.demo.carspends.data.mapper.ComponentMapper
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository
import javax.inject.Inject

class ComponentRepositoryImpl @Inject constructor(
    private val componentDao: ComponentDao,
    private val mapper: ComponentMapper
) : ComponentRepository {

    override suspend fun addComponentItemUseCase(component: ComponentItem) {
        componentDao.insertComponent(mapper.mapEntityToComponentItemDbModel(component))
    }

    override suspend fun deleteComponentItemUseCase(component: ComponentItem) {
        componentDao.deleteComponent(mapper.mapEntityToComponentItemDbModel(component))
    }

    override suspend fun editComponentItemUseCase(component: ComponentItem) {
        componentDao.insertComponent(mapper.mapEntityToComponentItemDbModel(component))
    }

    override fun getComponentItemsListUseCase(): LiveData<List<ComponentItem>> {
        return Transformations.map(componentDao.getComponentsListLD()) {
            it.map {
                mapper.mapComponentItemDbModelToEntity(it)
            }
        }
    }

    override suspend fun getComponentItemUseCase(id: Int): ComponentItem {
        return mapper.mapComponentItemDbModelToEntity(componentDao.getComponentById(id))
    }
}