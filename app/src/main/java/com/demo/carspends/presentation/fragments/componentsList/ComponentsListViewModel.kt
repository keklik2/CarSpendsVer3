package com.demo.carspends.presentation.fragments.componentsList

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.ComponentRepositoryImpl
import com.demo.carspends.domain.car.CarItem
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

    private var _carId = 0
    val carId get() = _carId

    private var _carMileage = 0
    val carMileage get() = _carMileage

    private val _carsList = GetCarItemsListLDUseCase(carRepository).invoke()
    val carsList get() = _carsList

    private val _componentsList = GetComponentItemsListUseCase(componentRepository).invoke()
    val componentsList get() = _componentsList

    private val carIdObserver: Observer<List<CarItem>> = Observer {
        _carId = it.first().id
    }

    private val carMileageObserver: Observer<List<CarItem>> = Observer {
        _carMileage = it.first().mileage
    }

    init {
        with(_carsList) {
            observeForever(carIdObserver)
            observeForever(carMileageObserver)
        }

    }

    fun deleteComponent(component: ComponentItem) {
        viewModelScope.launch {
            deleteComponentUseCase(component)
        }
    }

    override fun onCleared() {
        with (_carsList) {
            removeObserver(carIdObserver)
            removeObserver(carMileageObserver)
        }
        super.onCleared()
    }
}
