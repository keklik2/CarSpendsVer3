package com.demo.carspends.data.repositoryImpls

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.demo.carspends.data.MainDataBase
import com.demo.carspends.data.mapper.ComponentMapper
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository

class ComponentRepositoryImpl(private val application: Application): ComponentRepository {

    private val componentDao = MainDataBase.getInstance(application).componentDao()
    private val mapper = ComponentMapper()

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