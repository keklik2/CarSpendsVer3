package com.demo.carspends.presentation.fragments.car

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.Screens
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.AddCarItemUseCase
import com.demo.carspends.domain.car.usecases.EditCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.utils.refactorDouble
import com.demo.carspends.utils.refactorInt
import com.demo.carspends.utils.refactorString
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CarAddOrEditViewModel @Inject constructor(
    private val addCarItemUseCase: AddCarItemUseCase,
    private val editCarItemUseCase: EditCarItemUseCase,
    private val getCarItemUseCase: GetCarItemUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val router: Router
) : ViewModel() {

    fun exit() = router.exit()
    fun goToHomeScreen() = router.replaceScreen(Screens.HomePage())

    private var carId = CarItem.UNDEFINED_ID

    private val _carItem = MutableLiveData<CarItem>()
    val carrItem get() = _carItem

    private val _canCloseScreen = MutableLiveData<Unit>()
    val canCloseScreen get() = _canCloseScreen

    fun addCar(name: String?, mileage: String?, engineCapacity: String?, power: String?) {
        val rName = refactorString(name)
        val rMileage = refactorInt(mileage)
        val rEngineCapacity = refactorDouble(engineCapacity)
        val rPower = refactorInt(power)

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

    fun editCar(name: String?, mileage: String?, engineCapacity: String?, power: String?) {
        val rName = refactorString(name)
        val rMileage = refactorInt(mileage)
        val rEngineCapacity = refactorDouble(engineCapacity)
        val rPower = refactorInt(power)

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
            } else 0.0

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

    fun setItem(id: Int) {
        carId = id
        viewModelScope.launch {
            _carItem.value = getCarItemUseCase.invoke(carId)
        }
    }

    private suspend fun updateItem() {
        _carItem.value = getCarItemUseCase.invoke(carId)
    }

    private fun setCanCloseScreen() {
        _canCloseScreen.value = Unit
    }

    companion object {
        private const val ERR_NULL_ITEM_EDIT = "Received NULL CarItem for EditNoteItemUseCase()"
        private const val ERR_NULL_ITEM_ADD = "Received NULL CarItem for AddNoteItemUseCase()"
    }
}
