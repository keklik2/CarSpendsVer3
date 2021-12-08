package com.demo.carspends.presentation.fragments.componentAddOrEditFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.ComponentRepositoryImpl
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.component.usecases.AddComponentItemUseCase
import com.demo.carspends.domain.component.usecases.EditComponentItemUseCase
import com.demo.carspends.domain.component.usecases.GetComponentItemByIdUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class ComponentAddOrEditViewModel(app: Application): AndroidViewModel(app) {
    private val repository = ComponentRepositoryImpl(app)

    private val addComponentUseCase = AddComponentItemUseCase(repository)
    private val editComponentUseCase = EditComponentItemUseCase(repository)
    private val getComponentItemUseCase = GetComponentItemByIdUseCase(repository)

    private val _carsList= GetCarItemsListLDUseCase(CarRepositoryImpl(app)).invoke()
    val carsList get() = _carsList

    private val _componentDate = MutableLiveData<Long>()
    val componentDate get() = _componentDate

    private val _errorMileageInput = MutableLiveData<Boolean>()
    val errorMileageInput get() = _errorMileageInput

    private val _errorResourceInput = MutableLiveData<Boolean>()
    val errorResourceInput get() = _errorResourceInput

    private val _errorTitleInput = MutableLiveData<Boolean>()
    val errorTitleInput get() = _errorTitleInput

    private val _componentItem = MutableLiveData<ComponentItem>()
    val componentItem get() = _componentItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _componentDate.value = Date().time
        // Add mileage loading from cars' list
        // _noteMileage.value =
    }

    fun addComponentItem(title: String?, resource: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rResource = refactorInt(resource)
        val rMileage = refactorInt(mileage)

        if(areFieldsValid(rTitle, rResource, rMileage)) {
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
                    // Add fun for changing curr mileage in cars' list
                    // If mileage in note > mileage in cars' list = replace it in list
                    setCanCloseScreen()
                } else Exception(ERR_NULL_ITEM_ADD)
            }
        }
    }

    fun editNoteItem(title: String?, resource: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rResource = refactorInt(resource)
        val rMileage = refactorInt(mileage)

        if(areFieldsValid(rTitle, rResource, rMileage)) {
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
    }

    private fun refactorString(title: String?): String {
        return title?.trim() ?: ""
    }

    private fun refactorInt(price: String?): Int {
        return try {
            price?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun areFieldsValid(title: String, resource: Int, mileage: Int): Boolean {
        if (title.isBlank()) {
            _errorTitleInput.value = true
            return false
        }
        if (resource <= 0.0) {
            _errorResourceInput.value = true
            return false
        }
        if (mileage <= 0) {
            _errorMileageInput.value = true
            return false
        }
        return true
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getComponentItemUseCase(id)
            _componentItem.value = item
            _componentDate.value = item.date
        }
    }

    fun resetMileageError() {
        _errorMileageInput.value = false
    }

    fun resetTitleError() {
        _errorTitleInput.value = false
    }

    fun resetResourceError() {
        _errorResourceInput.value = false
    }

    fun setComponentDate(date: Long) {
        _componentDate.value = date
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val ERR_NULL_ITEM_EDIT = "Received NULL ComponentItem for EditComponentItemUseCase()"
        private const val ERR_NULL_ITEM_ADD = "Received NULL ComponentItem for EditComponentItemUseCase()"
    }
}