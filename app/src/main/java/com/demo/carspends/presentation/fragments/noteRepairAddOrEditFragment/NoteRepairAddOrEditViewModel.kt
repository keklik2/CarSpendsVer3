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
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class NoteRepairAddOrEditViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = NoteRepositoryImpl(app)
    private val carRepository = CarRepositoryImpl(app)

    private val noteType = NoteType.REPAIR
    private var carId = CarItem.UNDEFINED_ID

    private val addNoteItemUseCase = AddNoteItemUseCase(repository)
    private val editNoteItemUseCase = EditNoteItemUseCase(repository)
    private val getNoteItemUseCase = GetNoteItemUseCase(repository)
    private val getNoteItemsListByMileageUseCase = GetNoteItemsListByMileageUseCase(repository)
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

        if (areFieldsValid(rTitle, rTotalPrice, rMileage)) {
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
                    calculateAvgPrice()
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

        if (areFieldsValid(rTitle, rTotalPrice, rMileage)) {
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
                        calculateAvgPrice()
                        rollbackCarMileage()
                        setCanCloseScreen()
                    } else Exception(ERR_NULL_ITEM_EDIT)
                } else Exception(ERR_NULL_ITEM_EDIT)
            }
        }
    }

    private suspend fun rollbackCarMileage() {
        val cItem = getCarItemUseCase(carId)
        val notesList = getNoteItemsListByMileageUseCase()
        var newMileage = cItem.startMileage
        if (notesList.isNotEmpty()) {
            for (i in notesList) {
                if (i.type != NoteType.EXTRA && i.mileage > newMileage) newMileage = i.mileage
            }
        }
        editCarItemUseCase(
            cItem.copy(
                mileage = newMileage
            )
        )
        updateCarItem()
    }

    private suspend fun calculateAvgPrice() {
        val notes = getNoteItemsListByMileageUseCase()
        if (notes.isNotEmpty()) {
            val cItem = getCarItemUseCase(carId)
            editCarItemUseCase(
                cItem.copy(
                    milPrice = calculateAvgPriceOfAll(cItem.startMileage, notes)
                )
            )
            updateCarItem()
        }
    }

    private fun calculateAvgPriceOfAll(startMil: Int, notes: List<NoteItem>): Double {
        val list = mutableListOf<NoteItem>()
        for (i in notes) {
            if (i.type != NoteType.EXTRA) list.add(i)
        }

        if (list.size > 1) {
            val lastNote = list[list.size - 1]
            val allMileage =
                if (startMil < lastNote.mileage) list[0].mileage - startMil
                else list[0].mileage - lastNote.mileage
            var allPrice = 0.0
            for (i in 0 until list.size) {
                allPrice += list[i].totalPrice
            }

            val res = allPrice / allMileage.toDouble()
            return if (res > 0 && res != Double.POSITIVE_INFINITY && res != Double.NEGATIVE_INFINITY) res
            else 0.0
        }
        return 0.0
    }

    private suspend fun updateMileage(newMileage: Int) {
        val cItem = getCarItemUseCase(carId)
        val oldMileage = cItem.mileage
        if (newMileage > oldMileage) {
            editCarItemUseCase(
                cItem.copy(mileage = newMileage)
            )
        }
        updateCarItem()
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

    fun setCarItem(id: Int) {
        viewModelScope.launch {
            carId = id
            _currCarItem.value = getCarItemUseCase(carId)
        }
    }

    private suspend fun updateCarItem() {
        _currCarItem.value = getCarItemUseCase(carId)
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