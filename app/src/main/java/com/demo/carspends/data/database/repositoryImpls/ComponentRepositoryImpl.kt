package com.demo.carspends.data.database.repositoryImpls

import com.demo.carspends.data.database.component.ComponentDao
import com.demo.carspends.data.database.mapper.ComponentMapper
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
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

    override suspend fun getComponentItemsListUseCase(): List<ComponentItem> = withContext(Dispatchers.IO){
        try {
            componentDao.getComponentsList().map {
                mapper.mapComponentItemDbModelToEntity(it)
            }
        } catch (e: Exception) { throw e }
    }

    override suspend fun getComponentItemUseCase(id: Int): ComponentItem = withContext(Dispatchers.IO){
        try { mapper.mapComponentItemDbModelToEntity(componentDao.getComponentById(id)) }
        catch (e: Exception) { throw e }
    }
}
