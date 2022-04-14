package com.demo.carspends.presentation.fragments.componentsList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.Screens
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.DeleteComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemsListUseCase
import com.demo.carspends.utils.NORMAL_LOADING_DELAY
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import javax.inject.Inject

class ComponentsListViewModel @Inject constructor(
    private val deleteComponentUseCase: DeleteComponentItemUseCase,
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getComponentItemsListUseCase: GetComponentItemsListUseCase,
    private val router: Router,
    app: Application
) : AndroidViewModel(app), PropertyHost {

    fun goToComponentAddOrEdit() = router.navigateTo(Screens.ComponentEditOrAdd(_carId))
    fun goToComponentAddOrEdit(id: Int) = router.navigateTo(Screens.ComponentEditOrAdd(_carId, id))

    private var _carId = UNDEFINED_ID
    private var _carItem: CarItem? by state(null)
    var mileage by state(0)

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    private val _componentsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getComponentItemsListUseCase.invoke(NORMAL_LOADING_DELAY) }
    )
    val componentsListState by stateFromFlow(_componentsListLoading.stateFlow)

    init {
        _carsListLoading.refresh()

        autorun(::carsListState) {
            _componentsListLoading.refresh()
            when (it) {
                is Loading.State.Data -> {
                    if (it.data.isNotEmpty()) {
                        val car = it.data.first()
                        _carItem = car
                        _carId = car.id
                    }
                }
                else -> {}
            }
        }

        autorun(::_carItem) {
            it?.let {
                mileage = it.mileage
            }
        }
    }


    fun deleteComponent(component: ComponentItem) {
        viewModelScope.launch {
            deleteComponentUseCase(component)
        }
    }

    fun refreshData() {
        _componentsListLoading.refresh()
        _carsListLoading.refresh()
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope

    companion object {
        private const val UNDEFINED_ID = -1
    }
}
