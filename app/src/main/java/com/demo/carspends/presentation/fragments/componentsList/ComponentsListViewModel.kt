package com.demo.carspends.presentation.fragments.componentsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.ComponentRepositoryImpl
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.DeleteComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemsListUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class ComponentsListViewModel @Inject constructor(
    private val carRepository: CarRepositoryImpl,
    private val componentRepository: ComponentRepositoryImpl,
    private val deleteComponentUseCase: DeleteComponentItemUseCase
) : ViewModel() {

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