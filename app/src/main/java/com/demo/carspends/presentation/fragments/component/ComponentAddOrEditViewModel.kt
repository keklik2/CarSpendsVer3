package com.demo.carspends.presentation.fragments.component

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.AddComponentItemUseCase
import com.demo.carspends.domain.component.usecases.EditComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemByIdUseCase
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.refactorString
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ComponentAddOrEditViewModel @Inject constructor(
    private val addComponentUseCase: AddComponentItemUseCase,
    private val editComponentUseCase: EditComponentItemUseCase,
    private val getComponentItemUseCase: GetComponentItemByIdUseCase,
    private val getCarItemsListLDUseCase: GetCarItemsListLDUseCase,
    private val router: Router
) : ViewModel() {

    fun goBack() = router.exit()

    private val _carsList = getCarItemsListLDUseCase()
    val carsList get() = _carsList

    private val _componentDate = MutableLiveData<Long>()
    val componentDate get() = _componentDate

    private val _componentItem = MutableLiveData<ComponentItem>()
    val componentItem get() = _componentItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _componentDate.value = Date().time
    }

    fun addComponentItem(title: String?, resource: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rResource = refactorInt(resource)
        val rMileage = refactorInt(mileage)

        viewModelScope.launch {
            val cDate = _componentDate.value
            if (cDate != null) {
                addComponentUseCase(
                    ComponentItem(
                        title = rTitle,
                        startMileage = rMileage,
                        resourceMileage = rResource,
                        date = cDate
                    )
                )
                setCanCloseScreen()
            } else Exception(ERR_NULL_ITEM_ADD)
        }
    }

    fun editComponentItem(title: String?, resource: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rResource = refactorInt(resource)
        val rMileage = refactorInt(mileage)

        viewModelScope.launch {
            val cItem = _componentItem.value
            if (cItem != null) {
                val cDate = _componentDate.value
                if (cDate != null) {
                    editComponentUseCase(
                        cItem.copy(
                            title = rTitle,
                            startMileage = rMileage,
                            resourceMileage = rResource,
                            date = cDate
                        )
                    )
                    setCanCloseScreen()
                } else Exception(ERR_NULL_ITEM_EDIT)
            } else Exception(ERR_NULL_ITEM_EDIT)
        }
    }


    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getComponentItemUseCase(id)
            _componentItem.value = item
            _componentDate.value = item.date
        }
    }

    fun setComponentDate(date: Long) {
        _componentDate.value = date
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val ERR_NULL_ITEM_EDIT =
            "Received NULL ComponentItem for EditComponentItemUseCase()"
        private const val ERR_NULL_ITEM_ADD =
            "Received NULL ComponentItem for EditComponentItemUseCase()"
    }
}
