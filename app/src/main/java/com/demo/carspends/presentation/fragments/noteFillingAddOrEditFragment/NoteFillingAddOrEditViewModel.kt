package com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.AddNoteItemUseCase
import com.demo.carspends.domain.note.usecases.EditNoteItemUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemUseCase
import com.demo.carspends.domain.others.Fuel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class NoteFillingAddOrEditViewModel(app: Application): AndroidViewModel(app) {
    private val repository = NoteRepositoryImpl(app)

    private val noteType = NoteType.FUEL
    private val noteTitle = "Заправка"
    private val noteFuelTypes = Fuel.values()

    private val addNoteItemUseCase = AddNoteItemUseCase(repository)
    private val editNoteItemUseCase = EditNoteItemUseCase(repository)
    private val getNoteItem = GetNoteItemUseCase(repository)

    /**
    // Rewrite when Car's list will be used in app
    private val getCarItem = GetCarItemUseCase(CarRepositoryImpl(app))
    // Rewrite when Car's list will be used in app
    private val _noteMileage = MutableLiveData<Int>()
    val noteMileage get() = _noteMileage */

    private val _noteDate = MutableLiveData<Long>()
    val noteDate get() = _noteDate

    private val _errorMileageInput = MutableLiveData<Boolean>()
    val errorMileageInput get() = _errorMileageInput

    private val _errorPriceInput = MutableLiveData<Boolean>()
    val errorPriceInput get() = _errorPriceInput

    private val _errorTotalPriceInput = MutableLiveData<Boolean>()
    val errorTotalPriceInput get() = _errorTotalPriceInput

    private val _errorVolumeInput = MutableLiveData<Boolean>()
    val errorVolumeInput get() = _errorVolumeInput

    private val _noteItem = MutableLiveData<NoteItem>()
    val noteItem get() = _noteItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    init {
        _noteDate.value = Date().time
        // Add mileage loading from cars' list
        // _noteMileage.value =
    }

    fun addNoteItem(fuelTypeId: Int, volume: String?, totalPrice: String?, price: String?, mileage: String?) {
        val rFuelType = refactorFuel(fuelTypeId)
        val rVolume = refactorDouble(volume)
        val rTotalPrice = refactorDouble(totalPrice)
        val rPrice = refactorDouble(price)
        val rMileage = refactorInt(mileage)

        if(areFieldsValid(rVolume, rTotalPrice, rPrice, rMileage)) {
            viewModelScope.launch {
                val nDate = _noteDate.value
                if (nDate != null) {
                    addNoteItemUseCase(
                        NoteItem(
                            title = noteTitle,
                            totalPrice = rTotalPrice,
                            price = rPrice,
                            liters = rVolume,
                            mileage = rMileage,
                            fuelType = rFuelType,
                            date = nDate,
                            type = noteType
                        )
                    )
                    // Add fun for changing curr mileage in cars' list
                    // If mileage in note > mileage in cars' list = replace it in list
                    setCanCloseScreen()
                } else Exception("Received NULL NoteItem for AddNoteItemUseCase()")
            }
        }
    }

    fun editNoteItem(fuelTypeId: Int, volume: String?, totalPrice: String?, price: String?, mileage: String?) {
        val rFuelType = refactorFuel(fuelTypeId)
        val rVolume = refactorDouble(volume)
        val rTotalPrice = refactorDouble(totalPrice)
        val rPrice = refactorDouble(price)
        val rMileage = refactorInt(mileage)

        if(areFieldsValid(rVolume, rTotalPrice, rPrice, rMileage)) {
            viewModelScope.launch {
                val nItem = _noteItem.value
                if (nItem != null) {
                    val nDate = _noteDate.value
                    if (nDate != null) {
                        editNoteItemUseCase(
                            nItem.copy(
                                title = noteTitle,
                                totalPrice = rTotalPrice,
                                price = rPrice,
                                liters = rVolume,
                                mileage = rMileage,
                                fuelType = rFuelType,
                                date = nDate,
                                type = noteType
                            )
                        )
                        setCanCloseScreen()
                    } else Exception("Received NULL NoteItem for AddNoteItemUseCase()")
                } else Exception("Received NULL NoteItem for EditNoteItemUseCase()")
            }
        }
    }

    private fun refactorFuel(id: Int): Fuel {
        return noteFuelTypes[id]
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

    private fun areFieldsValid(volume: Double, totalPrice: Double, price: Double, mileage: Int): Boolean {
        if (volume <= 0.0) {
            _errorVolumeInput.value = true
            return false
        }
        if (totalPrice <= 0.0) {
            _errorTotalPriceInput.value = true
            return false
        }
        if (price <= 0.0) {
            _errorPriceInput.value = true
            return false
        }
        if (mileage <= 0) {
            _errorMileageInput.value = true
            return false
        }
        return true
    }

    fun getFuelId(fuel: Fuel): Int {
        for ((i, f) in Fuel.values().withIndex()) {
            if (f == fuel) return i
        }
        return 0
    }

    fun setItem(id: Int) {
        viewModelScope.launch {
            val item = getNoteItem(id)
            _noteItem.value = item
            _noteDate.value = item.date
        }
    }

    fun resetMileageError() {
        _errorMileageInput.value = false
    }

    fun resetVolumeError() {
        _errorVolumeInput.value = false
    }

    fun resetTotalPriceError() {
        _errorTotalPriceInput.value = false
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
}