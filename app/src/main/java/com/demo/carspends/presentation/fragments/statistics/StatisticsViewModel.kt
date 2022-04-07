package com.demo.carspends.presentation.fragments.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.presentation.fragments.notesList.NotesListViewModel
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val app: Application
) : AndroidViewModel(app), PropertyHost {

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
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
