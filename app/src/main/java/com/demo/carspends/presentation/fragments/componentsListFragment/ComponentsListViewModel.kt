package com.demo.carspends.presentation.fragments.componentsListFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.ComponentRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.DeleteComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemsListUseCase
import kotlinx.coroutines.launch

class ComponentsListViewModel(app: Application): AndroidViewModel(app) {

    private val carRepository = CarRepositoryImpl(app)
    private val componentRepository = ComponentRepositoryImpl(app)

    private val deleteComponentUseCase = DeleteComponentItemUseCase(componentRepository)

    private val _carsList = GetCarItemsListLDUseCase(carRepository).invoke()
    val carsList get() = _carsList

    private val _componentsList = GetComponentItemsListUseCase(componentRepository).invoke()
    val componentsList get() = _componentsList

    fun deleteComponent(component: ComponentItem) {
        viewModelScope.launch {
            deleteComponentUseCase(component)
        }
    }
}