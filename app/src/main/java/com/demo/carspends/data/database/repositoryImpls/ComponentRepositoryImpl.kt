package com.demo.carspends.data.database.repositoryImpls

import com.demo.carspends.data.database.component.ComponentDao
import com.demo.carspends.data.database.mapper.ComponentMapper
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.ComponentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class ComponentRepositoryImpl @Inject constructor(
    private val dao: ComponentDao,
    private val mapper: ComponentMapper
) : ComponentRepository {

    override suspend fun addComponentItemUseCase(component: ComponentItem) {
        dao.insert(mapper.mapEntityToComponentItemDbModel(component))
    }

    override suspend fun deleteComponentItemUseCase(component: ComponentItem) {
        dao.delete(mapper.mapEntityToComponentItemDbModel(component))
    }

    override suspend fun editComponentItemUseCase(component: ComponentItem) {
        dao.insert(mapper.mapEntityToComponentItemDbModel(component))
    }

    override suspend fun getComponentItemsListUseCase(delay: Long): List<ComponentItem> = withContext(Dispatchers.IO){
        try {
            delay(delay)
            dao.getComponents().map {
                mapper.mapComponentItemDbModelToEntity(it)
            }
        } catch (e: Exception) { throw e }
    }

    override suspend fun getComponentItemUseCase(id: Int): ComponentItem = withContext(Dispatchers.IO){
        try { mapper.mapComponentItemDbModelToEntity(dao.getComponent(id)) }
        catch (e: Exception) { throw e }
    }

    override suspend fun dropData() {
        dao.dropData()
    }
}
