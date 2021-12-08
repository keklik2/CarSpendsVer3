package com.demo.carspends.presentation.fragments.noteRepairAddOrEditFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListLDUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class NoteRepairAddOrEditViewModel(app: Application): AndroidViewModel(app) {
    private val repository = NoteRepositoryImpl(app)
    private val carRepository = CarRepositoryImpl(app)

    private val noteType = NoteType.REPAIR

    private val addNoteItemUseCase = AddNoteItemUseCase(repository)
    private val editNoteItemUseCase = EditNoteItemUseCase(repository)
    private val getNoteItemUseCase = GetNoteItemUseCase(repository)
    private val getCarItemUseCase = GetCarItemUseCase(carRepository)
    private val editCarItemUseCase = EditCarItemUseCase(carRepository)

    private val _noteDate = MutableLiveData<Long>()
    val noteDate get() = _noteDate

    private val _errorMileageInput = MutableLiveData<Boolean>()
    val errorMileageInput get() = _errorMileageInput

    private val _errorTotalPriceInput = MutableLiveData<Boolean>()
    val errorTotalPriceInput get() = _errorTotalPriceInput

    private val _errorTitleInput = MutableLiveData<Boolean>()
    val errorTitleInput get() = _errorTitleInput

    private val _noteItem = MutableLiveData<NoteItem>()
    val noteItem get() = _noteItem

    private val _currCarItem = MutableLiveData<CarItem>()
    val currCarItem get() = _currCarItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _noteDate.value = Date().time
    }

    fun addNoteItem(title: String?, totalPrice: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rTotalPrice = refactorDouble(totalPrice)
        val rMileage = refactorInt(mileage)

        if(areFieldsValid(rTitle, rTotalPrice, rMileage)) {
            viewModelScope.launch {
                val nDate = _noteDate.value
                if (nDate != null) {
                    addNoteItemUseCase(
                        NoteItem(
                            title = rTitle,
                            totalPrice = rTotalPrice,
                            mileage = rMileage,
                            date = nDate,
                            type = noteType
                        )
                    )
                    updateMileage(rMileage)
                    setCanCloseScreen()
                } else Exception(ERR_NULL_ITEM_ADD)
            }
        }
    }

    fun editNoteItem(title: String?, totalPrice: String?, mileage: String?) {
        val rTitle = refactorString(title)
        val rTotalPrice = refactorDouble(totalPrice)
        val rMileage = refactorInt(mileage)

        if(areFieldsValid(rTitle, rTotalPrice, rMileage)) {
            viewModelScope.launch {
                val nItem = _noteItem.value
                if (nItem != null) {
                    val nDate = _noteDate.value
                    if (nDate != null) {
                        editNoteItemUseCase(
                            nItem.copy(
                                title = rTitle,
                                totalPrice = rTotalPrice,
                                mileage = rMileage,
                                date = nDate,
                                type = noteType
                            )
                        )
                        updateMileage(rMileage)
                        setCanCloseScreen()
                    } else Exception(ERR_NULL_ITEM_EDIT)
                } else Exception(ERR_NULL_ITEM_EDIT)
            }
        }
    }

    private fun refactorString(title: String?): String {
        return title?.trim() ?: ""
    }

    private fun refactorDouble(price: String?): Double {
        return try {
            price?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun refactorInt(price: String?): Int {
        return try {
            price?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun areFieldsValid(title: String, totalPrice: Double, mileage: Int): Boolean {
        if (title.isBlank()) {
            _errorTitleInput.value = true
            return false
        }
        if (totalPrice <= 0.0) {
            _errorTotalPriceInput.value = true
            return false
        }
        if (mileage <= 0) {
            _errorMileageInput.value = true
            return false
        }
        return true
    }

    private fun updateMileage(newMileage: Int) {
        val cItem = _currCarItem.value
        if (cItem != null) {
            val oldMileage = cItem.mileage
            if (newMileage > oldMileage) {
                viewModelScope.launch {
                    editCarItemUseCase(
                        cItem.copy(mileage = newMileage)
                    )
                }
            }
        }
    }

    fun setCarItem(id: Int) {
        viewModelScope.launch {
            _currCarItem.value = getCarItemUseCase(id)
        }
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getNoteItemUseCase(id)
            _noteItem.value = item
            _noteDate.value = item.date
        }
    }

    fun resetMileageError() {
        _errorMileageInput.value = false
    }

    fun resetTitleError() {
        _errorTitleInput.value = false
    }

    fun resetTotalPriceError() {
        _errorTotalPriceInput.value = false
    }

    fun setNoteDate(date: Long) {
        _noteDate.value = date
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val ERR_NULL_ITEM_EDIT = "Received NULL NoteItem for EditNoteItemUseCase()"
        private const val ERR_NULL_ITEM_ADD = "Received NULL NoteItem for AddNoteItemUseCase()"
    }
}