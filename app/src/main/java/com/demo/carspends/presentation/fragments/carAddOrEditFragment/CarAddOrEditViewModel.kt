package com.demo.carspends.presentation.fragments.carAddOrEditFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.AddCarItemUseCase
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CarAddOrEditViewModel(app: Application): AndroidViewModel(app) {
    private val repository = CarRepositoryImpl(app)
    private val noteRepository = NoteRepositoryImpl(app)

    private var carId = CarItem.UNDEFINED_ID

    private val addCarItemUseCase = AddCarItemUseCase(repository)
    private val editCarItemUseCase = EditCarItemUseCase(repository)
    private val getCarItemUseCase = GetCarItemUseCase(repository)
    private val getNoteItemsListByMileageUseCase = GetNoteItemsListByMileageUseCase(noteRepository)

    private val _errorPowerInput = MutableLiveData<Boolean>()
    val errorPowerInput get() = _errorPowerInput

    private val _errorEngineCapacityInput = MutableLiveData<Boolean>()
    val errorEngineCapacityInput get() = _errorEngineCapacityInput

    private val _errorMileageInput = MutableLiveData<Boolean>()
    val errorMileageInput get() = _errorMileageInput

    private val _errorNameInput = MutableLiveData<Boolean>()
    val errorNameInput get() = _errorNameInput

    private val _carItem = MutableLiveData<CarItem>()
    val carrItem get() = _carItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    fun addCar(name: String?, mileage: String?, engineCapacity: String?, power: String?) {
        val rName = refactorString(name)
        val rMileage = refactorInt(mileage)
        val rEngineCapacity = refactorDouble(engineCapacity)
        val rPower = refactorInt(power)

        if(areFieldsValid(rName, rMileage, rEngineCapacity, rPower)) {
            viewModelScope.launch {
                addCarItemUseCase(
                    CarItem(
                        title = rName,
                        startMileage = rMileage,
                        mileage = rMileage,
                        engineVolume = rEngineCapacity,
                        power = rPower
                    )
                )
                setCanCloseScreen()
            }
        }
    }

    fun editCar(name: String?, mileage: String?, engineCapacity: String?, power: String?) {
        val rName = refactorString(name)
        val rMileage = refactorInt(mileage)
        val rEngineCapacity = refactorDouble(engineCapacity)
        val rPower = refactorInt(power)

        if(areFieldsValid(rName, rMileage, rEngineCapacity, rPower)) {
            val cItem = _carItem.value
            if (cItem != null) {
                viewModelScope.launch {

                    val notes = getNoteItemsListByMileageUseCase()
                    var newStartMileage: Int
                    if (notes.isNotEmpty()) {
                        newStartMileage = rMileage
                        for (i in notes) {
                            if (i.type != NoteType.EXTRA) {
                                newStartMileage = cItem.startMileage
                                break
                            }
                        }
                    } else newStartMileage = rMileage

                    val newCar = cItem.copy(
                        title = rName,
                        startMileage = newStartMileage,
                        mileage = rMileage,
                        engineVolume = rEngineCapacity,
                        power = rPower
                    )
                    editCarItemUseCase(newCar)

                    calculateAllMileage()
                    calculateAvgPrice()
                    setCanCloseScreen()
                }
            } else throw Exception(ERR_NULL_ITEM_EDIT)
        }
    }

    private suspend fun calculateAllMileage() {
        val notes = getNoteItemsListByMileageUseCase()
        val car = getCarItemUseCase(carId)

        val resMil = if (notes.isNotEmpty()) {
            val maxMil = max(car.mileage, notes[0].mileage)
            val lastNote = getLastNotExtraNote()
            val minMil =
                if (lastNote != null) min(
                    car.startMileage,
                    lastNote.mileage
                ) else car.startMileage
            abs(maxMil - minMil)
        } else 0

        editCarItemUseCase(
            car.copy(
                allMileage = resMil
            )
        )
        updateItem()
    }

    private suspend fun calculateAvgPrice() {
        val car = getCarItemUseCase(carId)
        val newMilPrice =
            if (car.allPrice > 0 && car.allMileage > 0) {
                val res = car.allPrice / car.allMileage
                if (res < 0) 0.0
                else res
            }
            else 0.0

        editCarItemUseCase(
            car.copy(
                milPrice = newMilPrice
            )
        )
        updateItem()
    }

    private suspend fun getLastNotExtraNote(): NoteItem? {
        val notes = getNoteItemsListByMileageUseCase()
        for (i in notes.reversed()) {
            if (i.type != NoteType.EXTRA) return i
        }
        return null
    }

    private fun areFieldsValid(name: String, mileage: Int, engineCapacity: Double, power: Int): Boolean {
        if (name.isBlank()) {
            _errorNameInput.value = true
            return false
        }
        if (mileage <= 0) {
            _errorMileageInput.value = true
            return false
        }
        if (engineCapacity <= 0) {
            _errorEngineCapacityInput.value = true
            return false
        }
        if (power <= 0) {
            _errorPowerInput.value = true
            return false
        }
        return true
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

    private fun refactorDouble(price: String?): Double {
        return try {
            price?.trim()?.toDouble() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun setItem(id: Int) {
        carId = id
        viewModelScope.launch {
            _carItem.value = getCarItemUseCase(carId)
        }
    }

    private suspend fun updateItem() {
        _carItem.value = getCarItemUseCase(carId)
    }

    fun resetPowerError() {
        _errorPowerInput.value = false
    }

    fun resetEngineCapacityError() {
        _errorEngineCapacityInput.value = false
    }

    fun resetMileageError() {
        _errorMileageInput.value = false
    }

    fun resetNameError() {
        _errorNameInput.value = false
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val ERR_NULL_ITEM_EDIT = "Received NULL CarItem for EditNoteItemUseCase()"
        private const val ERR_NULL_ITEM_ADD = "Received NULL CarItem for AddNoteItemUseCase()"
    }
}