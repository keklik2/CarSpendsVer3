package com.demo.carspends.presentation.fragments.statistics.numerous

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
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
    private val getNoteItemsListUseCase: GetNoteItemsListUseCase,
    private val app: Application
) : BaseViewModel(app) {

    var startDate by state(Calendar.getInstance().apply{ add(Calendar.YEAR, -1) }.timeInMillis)
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

    init {
        _carsListLoading.refresh()

        autorun(::carsListState) { itState ->
            when (itState) {
                is Loading.State.Data -> carItem = itState.data.first()
            }
        }

        autorun(::carItem) {
            it?.let {
                sAvgFuel = String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.avgFuel)
                )
                sMomentFuel = String.format(
                    app.getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.momentFuel)
                )
                sAllFuel = String.format(
                    app.getString(R.string.text_measure_gas_volume_unit_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.allFuel)
                )
                sFuelPrice = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.fuelPrice)
                )
                sMileagePrice = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.milPrice)
                )
                sAllPrice = String.format(
                    app.getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.allPrice)
                )
                sAllMileage = String.format(
                    app.getString(R.string.text_measure_mileage_unit_for_formatting),
                    getFormattedIntAsStrForDisplay(it.allMileage)
                )
            }
        }

        autorun(::startDate, ::endDate) { sDate, eDate ->
            withScope {  }
        }
    }

    fun calculateAvgFuel() {

    }

    fun calculateMomentFuel() {

    }

    fun calculateAllFuel() {

    }

    fun calculateFuelPrice() {

    }

    fun calculateMileagePrice() {

    }

    fun calculateAllPrice() {

    }

    fun calculateAllMileage() {

    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
