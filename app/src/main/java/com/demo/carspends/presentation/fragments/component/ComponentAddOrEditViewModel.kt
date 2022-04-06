package com.demo.carspends.presentation.fragments.component

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.AddComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemsListUseCase
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.refactorString
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
import java.util.*
import javax.inject.Inject

class ComponentAddOrEditViewModel @Inject constructor(
    private val addComponentUseCase: AddComponentItemUseCase,
    private val getComponentItemsListUseCase: GetComponentItemsListUseCase,
    private val router: Router,
    private val app: Application
) : AndroidViewModel(app), PropertyHost {

    fun goBack() = router.exit()

    var cTitle: String? by state(null)
    var cResourceMileage: String? by state(null)
    var cStartMileage: String? by state(null)
    var cDate by state(getCurrentDate())
    var cId: Int? by state(null)

    private var componentItem: ComponentItem? by state(null)
    var canCloseScreen by state(false)

    private val _componentsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getComponentItemsListUseCase.invoke() }
    )
    private val componentsListState by stateFromFlow(_componentsListLoading.stateFlow)

    init {
        autorun(::cId) {
            if (it != null) _componentsListLoading.refresh()
            else {
                componentItem = null
                cDate = getCurrentDate()
            }
        }

        autorun(::componentsListState) { itState ->
            when (itState) {
                is Loading.State.Data -> {
                    cId?.let { itId ->
                        componentItem = itState.data.firstOrNull { itComponent ->
                            itComponent.id == itId
                        }
                    }

                }
                is Loading.State.Error -> Toast.makeText(
                    app.applicationContext,
                    app.getString(R.string.toast_components_loading_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        autorun(::componentItem) {
            if (it != null) {
                cTitle = it.title
                cResourceMileage = it.resourceMileage.toString()
                cStartMileage = it.startMileage.toString()
                cDate = it.date
            } else {
                cTitle = null
                cResourceMileage = null
                cStartMileage = null
                cDate = getCurrentDate()
            }
        }
    }

    fun addOrEditComponentItem(title: String?, resource: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rResource = refactorInt(resource)
        val rMileage = refactorInt(mileage)

        val newItem = componentItem?.copy(
            title = rTitle,
            startMileage = rMileage,
            resourceMileage = rResource,
            date = cDate
        )
            ?: ComponentItem(
                title = rTitle,
                startMileage = rMileage,
                resourceMileage = rResource,
                date = cDate
            )

        viewModelScope.launch {
            addComponentUseCase(
                newItem
            )
            setCanCloseScreen()
        }
    }

    private fun getCurrentDate(): Long = Date().time
    private fun setCanCloseScreen() { canCloseScreen = true }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
