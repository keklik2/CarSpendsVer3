package com.demo.carspends.presentation.fragments.car

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.Screens
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.AddCarItemUseCase
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.utils.*
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
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CarAddOrEditViewModel @Inject constructor(
    private val addCarItemUseCase: AddCarItemUseCase,
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val router: Router,
    private val app: Application
) : AndroidViewModel(app), PropertyHost {

    fun exit() = router.exit()
    fun goToHomeScreen() = router.replaceScreen(Screens.HomePage())

    var cTitle: String? by state(null)
    var cMileage: String? by state(null)
    var cEngineCapacity: String? by state(null)
    var cPower: String? by state(null)

    var cAvgFuel: String? by state(null)
    var cMomentFuel: String? by state(null)
    var cAllFuel: String? by state(null)
    var cAllFuelPrice: String? by state(null)
    var cMileagePrice: String? by state(null)
    var cAllPrice: String? by state(null)
    var cAllMileage: String? by state(null)

    var cId: Int? by state(null)
    var canCloseScreen by state(false)
    private var carItem: CarItem? by state(null)

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    private val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    private val _notesListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getNoteItemsListByMileageUseCase.invoke() }
    )
    private val notesListState by stateFromFlow(_notesListLoading.stateFlow)
    private var notesListForCalculation = mutableListOf<NoteItem>()

    init {
        autorun(::cId) {
            if (it != null) {
                _carsListLoading.refresh()
                _notesListLoading.refresh()
            } else carItem = null
        }

        autorun(::carsListState) { itState ->
            when (itState) {
                is Loading.State.Data -> {
                    cId?.let { itId ->
                        carItem = itState.data.firstOrNull { itCar ->
                            itCar.id == itId
                        }
                    }
                }
                is Loading.State.Error -> Toast.makeText(
                    app.applicationContext,
                    app.getString(R.string.toast_cars_loading_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        autorun(::notesListState) { itState ->
            when (itState) {
                is Loading.State.Data -> notesListForCalculation.addAll(itState.data)
            }
        }

        autorun(::carItem) {
            if (it != null) {
                cTitle = it.title
                cMileage = it.mileage.toString()
                cEngineCapacity = getFormattedDoubleAsStr(it.engineVolume)
                cPower = it.power.toString()

                cAvgFuel = String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.avgFuel)
                )
                cMomentFuel = String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.momentFuel)
                )
                cAllFuel = String.format(
                    app.getString(R.string.text_measure_gas_volume_unit_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.allFuel)
                )
                cAllFuelPrice = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.fuelPrice)
                )
                cMileagePrice = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.milPrice)
                )
                cAllPrice = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.allPrice)
                )
                cAllMileage = String.format(
                    app.getString(R.string.text_measure_mileage_unit_for_formatting),
                    getFormattedIntAsStrForDisplay(it.allMileage)
                )
            } else {
                cTitle = null
                cMileage = null
                cEngineCapacity = null
                cPower = null
            }
        }
    }

    fun addOrEditCar(name: String?, mileage: String?, engineCapacity: String?, power: String?) {
        val rName = refactorString(name)
        val rMileage = refactorInt(mileage)
        val rEngineCapacity = refactorDouble(engineCapacity)
        val rPower = refactorInt(power)

        val newCar: CarItem = if (carItem != null) {
            val notes = notesListForCalculation
            val newStartMileage: Int = if (notes.isNotEmpty()) {
                if (notes.firstOrNull { it.type != NoteType.EXTRA } != null) carItem!!.startMileage
                else rMileage
            } else rMileage

            carItem!!.copy(
                title = rName,
                startMileage = newStartMileage,
                mileage = rMileage,
                engineVolume = rEngineCapacity,
                power = rPower
            )
        } else {
            CarItem(
                title = rName,
                startMileage = rMileage,
                mileage = rMileage,
                engineVolume = rEngineCapacity,
                power = rPower
            )
        }

        viewModelScope.launch {
            carItem = newCar
            calculateAllMileage()
            calculateAvgPrice()

            updateCarItem()
            setCanCloseScreen()
        }
    }

    private fun calculateAllMileage() {
        carItem?.let {
            val resMil = if (notesListForCalculation.isNotEmpty()) {
                val maxMil = max(it.mileage, notesListForCalculation[0].mileage)
                val lastNote = getLastNotExtraNote()
                val minMil =
                    if (lastNote != null) min(
                        it.startMileage,
                        lastNote.mileage
                    )
                    else it.startMileage
                abs(maxMil - minMil)
            } else 0

            carItem = it.copy(
                allMileage = resMil
            )
        }
    }

    private fun calculateAvgPrice() {
        carItem?.let {
            val newMilPrice =
                if (it.allPrice > 0 && it.allMileage > 0) {
                    val res = it.allPrice / it.allMileage
                    if (res < 0) 0.0
                    else res
                } else 0.0

            carItem = it.copy(
                milPrice = newMilPrice
            )
        }

    }

    private fun getLastNotExtraNote(): NoteItem? = notesListForCalculation.lastOrNull {
            it.type != NoteType.EXTRA
        }
    private suspend fun updateCarItem() = carItem?.let { addCarItemUseCase(it) }
    private fun setCanCloseScreen() { canCloseScreen = true }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
