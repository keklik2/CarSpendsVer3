package com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class NoteExtraAddOrEditViewModel @Inject constructor(
    private val addNoteItemUseCase: AddNoteItemUseCase,
    private val editNoteItemUseCase: EditNoteItemUseCase,
    private val getNoteItemUseCase: GetNoteItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getCarItemUseCase: GetCarItemUseCase,
    private val editCarItemUseCase: EditCarItemUseCase
) : ViewModel() {

    private var carId = CarItem.UNDEFINED_ID
    private val noteType = NoteType.EXTRA

    private val _noteDate = MutableLiveData<Long>()
    val noteDate get() = _noteDate

    private val _errorPriceInput = MutableLiveData<Boolean>()
    val errorPriceInput get() = _errorPriceInput

    private val _errorTitleInput = MutableLiveData<Boolean>()
    val errorTitleInput get() = _errorTitleInput

    private val _noteItem = MutableLiveData<NoteItem>()
    val noteItem get() = _noteItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _noteDate.value = Date().time
    }

    fun addNoteItem(title: String?, price: String?) {
        val rTitle = refactorTitle(title)
        val rPrice = refactorPrice(price)

        if (areFieldsValid(rTitle, rPrice)) {
            viewModelScope.launch {
                val nDate = _noteDate.value
                if (nDate != null) {
                    val newNote = NoteItem(
                        title = rTitle,
                        totalPrice = rPrice,
                        date = nDate,
                        type = noteType
                    )
                    addNoteItemUseCase(newNote)

                    addLastPrice(newNote)
                    calculateAvgPrice()
                    setCanCloseScreen()
                } else throw Exception("Received NULL NoteItem for AddNoteItemUseCase()")
            }
        }
    }

    fun editNoteItem(title: String?, price: String?) {
        val rTitle = refactorTitle(title)
        val rPrice = refactorPrice(price)

        if (areFieldsValid(rTitle, rPrice)) {
            viewModelScope.launch {
                val nItem = _noteItem.value
                if (nItem != null) {
                    val nDate = _noteDate.value
                    if (nDate != null) {
                        editNoteItemUseCase(
                            nItem.copy(
                                title = rTitle,
                                totalPrice = rPrice,
                                date = nDate,
                                type = noteType
                            )
                        )

                        addAllPrice()
                        calculateAvgPrice()
                        setCanCloseScreen()
                    } else throw Exception("Received NULL NoteDate for AddNoteItemUseCase()")
                } else throw Exception("Received NULL NoteItem for EditNoteItemUseCase()")
            }
        }
    }

    private suspend fun calculateAvgPrice() {
        val carItem = getCarItemUseCase(carId)
        val newMilPrice =
            if (carItem.allPrice > 0 && carItem.allMileage > 0) {
                val res = carItem.allPrice / carItem.allMileage
                if (res < 0) 0.0
                else res
            } else 0.0

        editCarItemUseCase(
            carItem.copy(
                milPrice = newMilPrice
            )
        )
    }

    private suspend fun addLastPrice(note: NoteItem) {
        val carItem = getCarItemUseCase(carId)
        editCarItemUseCase(
            carItem.copy(
                allPrice = carItem.allPrice + note.totalPrice
            )
        )
    }

    private suspend fun addAllPrice() {
        var allPrice = 0.0
        for (i in getNoteItemsListByMileageUseCase()) {
            allPrice += i.totalPrice
        }

        if (allPrice < 0) allPrice = 0.0

        editCarItemUseCase(
            getCarItemUseCase(carId).copy(
                allPrice = allPrice
            )
        )
    }

    private fun refactorTitle(title: String?): String {
        return title?.trim() ?: ""
    }

    private fun refactorPrice(price: String?): Double {
        return try {
            price?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun areFieldsValid(title: String, price: Double): Boolean {
        if (title.isBlank()) {
            _errorTitleInput.value = true
            return false
        }
        if (price <= 0.0) {
            _errorPriceInput.value = true
            return false
        }
        return true
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getNoteItemUseCase(id)
            _noteItem.value = item
            _noteDate.value = item.date
        }
    }

    fun resetTitleError() {
        _errorTitleInput.value = false
    }

    fun resetPriceError() {
        _errorPriceInput.value = false
    }

    fun setNoteDate(date: Long) {
        _noteDate.value = date
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    fun setCarId(id: Int) {
        carId = id
    }
}