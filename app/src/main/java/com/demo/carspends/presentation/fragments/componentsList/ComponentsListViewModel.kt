package com.demo.carspends.presentation.fragments.componentsList

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.Screens
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.DeleteComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemsListUseCase
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import javax.inject.Inject

class ComponentsListViewModel @Inject constructor(
    private val deleteComponentUseCase: DeleteComponentItemUseCase,
    private val getCarItemsListLDUseCase: GetCarItemsListLDUseCase,
    private val getComponentItemsListUseCase: GetComponentItemsListUseCase,
    private val router: Router
) : ViewModel() {

    fun goToComponentAddOrEdit() = router.navigateTo(Screens.ComponentEditOrAdd(carId))
    fun goToComponentAddOrEdit(id: Int) = router.navigateTo(Screens.ComponentEditOrAdd(carId, id))

    private var _carId = 0
    val carId get() = _carId

    private var _carMileage = 0
    val carMileage get() = _carMileage

    private val _carsList = getCarItemsListLDUseCase()
    val carsList get() = _carsList

    private val _componentsList = getComponentItemsListUseCase()
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
