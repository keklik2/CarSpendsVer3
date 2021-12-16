package com.demo.carspends.presentation.fragments.carAddOrEditFragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.carspends.data.repositoryImpls.CarRepositoryImpl
import com.demo.carspends.data.repositoryImpls.NoteRepositoryImpl
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.AddCarItemUseCase
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
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
                    calculateAllMileage(newCar)
                    setCanCloseScreen()
                }
            } else throw Exception(ERR_NULL_ITEM_EDIT)
        }
    }

    private suspend fun calculateAllMileage(car: CarItem) {
        val notes = getNoteItemsListByMileageUseCase()

        val resMil = if (notes.isNotEmpty()) {
            val maxMil = max(car.mileage, notes[0].mileage)
            val minMil =
                if (notes[notes.size - 1].type != NoteType.EXTRA) min(
                    car.startMileage,
                    notes[notes.size - 1].mileage
                )
                else car.startMileage
            abs(maxMil - minMil)
        } else 0

        editCarItemUseCase(
            car.copy(
                allMileage = resMil
            )
        )
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
        viewModelScope.launch {
            val item = getCarItemUseCase(id)
            _carItem.value = item
        }
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