package com.demo.carspends.presentation.fragments.statistics.numerous

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListUseCase
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import java.util.*
import javax.inject.Inject

class NumerousViewModel @Inject constructor(
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val app: Application
) : BaseViewModel(app) {

    var startDate by state(Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.timeInMillis)
    var endDate by state(getCurrentDate())

    var sAvgFuel by state("-")
    var sMomentFuel by state("-")
    var sAllFuel by state("-")
    var sFuelPrice by state("-")
    var sMileagePrice by state("-")
    var sAllPrice by state("-")
    var sAllMileage by state("-")

    private var carItem: CarItem? by state(null)

    private val _carsListLoading = OrdinaryLoading(
        viewModelScope,
        load = { getCarItemsListUseCase.invoke() }
    )
    val carsListState by stateFromFlow(_carsListLoading.stateFlow)

    private var notesForCalculation = mutableListOf<NoteItem>()
    private var notesByDate = mutableListOf<NoteItem>()

    init {
        _carsListLoading.refresh()
        withScope {
            notesForCalculation = getNoteItemsListByMileageUseCase().toMutableList()
            notesByDate = notesForCalculation.sortedByDescending { it.date }.toMutableList()
            startDate = notesByDate.lastOrNull()?.date ?: Calendar.getInstance()
                .apply { add(Calendar.YEAR, -1) }.timeInMillis
        }

        autorun(::carsListState) { itState ->
            when (itState) {
                is Loading.State.Data -> carItem = itState.data.first()
            }
        }

        autorun(::carItem) {
            it?.let {
                initData(
                    it.avgFuel,
                    it.momentFuel,
                    it.allFuel,
                    it.fuelPrice,
                    it.milPrice,
                    it.allPrice,
                    it.allMileage
                )
            }
        }

        autorun(::startDate, ::endDate) { sDate, eDate ->
            initData(
                calculateAvgFuel(),
                calculateMomentFuel(),
                calculateAllFuel(),
                calculateFuelPrice(),
                calculateMileagePrice(),
                calculateAllPrice(),
                calculateAllMileage()
            )
        }
    }

    private fun initData(
        avgFuel: Double,
        momentFuel: Double,
        allFuel: Double,
        fuelPrice: Double,
        milPrice: Double,
        allPrice: Double,
        allMileage: Int
    ) {
        sAvgFuel = String.format(
            app.getString(R.string.text_measure_gas_charge_for_formatting),
            getFormattedDoubleAsStrForDisplay(avgFuel)
        )
        sMomentFuel = String.format(
            app.getString(R.string.text_measure_gas_charge_for_formatting),
            getFormattedDoubleAsStrForDisplay(momentFuel)
        )
        sAllFuel = String.format(
            app.getString(R.string.text_measure_gas_volume_unit_for_formatting),
            getFormattedDoubleAsStrForDisplay(allFuel)
        )
        sFuelPrice = String.format(
            app.getString(R.string.text_measure_currency_for_formatting),
            getFormattedDoubleAsStrForDisplay(fuelPrice)
        )
        sMileagePrice = String.format(
            app.getString(R.string.text_measure_currency_for_formatting),
            getFormattedDoubleAsStrForDisplay(milPrice)
        )
        sAllPrice = String.format(
            app.getString(R.string.text_measure_currency_for_formatting),
            getFormattedDoubleAsStrForDisplay(allPrice)
        )
        sAllMileage = String.format(
            app.getString(R.string.text_measure_mileage_unit_for_formatting),
            getFormattedIntAsStrForDisplay(allMileage)
        )
    }

    private fun calculateAvgFuel(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.avgFuel
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0.0
    }

    private fun calculateMomentFuel(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                // TODO("Return standard value from car item")
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0.0
    }

    private fun calculateAllFuel(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.allFuel
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0.0
    }

    private fun calculateFuelPrice(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.fuelPrice
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0.0
    }

    private fun calculateMileagePrice(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.milPrice
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0.0
    }

    private fun calculateAllPrice(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.allPrice
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0.0
    }

    private fun calculateAllMileage(): Int {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.allMileage
            } else {
                // TODO("Make calculations and return result")
            }
        }
        return 0
    }

    fun restoreDates() {
        startDate = notesByDate.lastOrNull()?.date ?: Calendar.getInstance()
            .apply { add(Calendar.YEAR, -1) }.timeInMillis
        endDate = getCurrentDate()
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
