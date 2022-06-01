package com.demo.carspends.presentation.fragments.statistics.numerous

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.car.usecases.GetCarItemsListUseCase
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.domain.note.usecases.GetNoteItemsListByMileageUseCase
import com.demo.carspends.domain.note.usecases.GetNoteItemsListUseCase
import com.demo.carspends.domain.settings.GetSettingValueUseCase
import com.demo.carspends.domain.settings.SetSettingUseCase
import com.demo.carspends.domain.settings.SettingsRepository
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import com.demo.carspends.utils.ui.tipShower.TipModel
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class NumerousViewModel @Inject constructor(
    private val getCarItemsListUseCase: GetCarItemsListUseCase,
    private val getNoteItemsListByMileageUseCase: GetNoteItemsListByMileageUseCase,
    private val getSettingValueUseCase: GetSettingValueUseCase,
    private val setSettingUseCase: SetSettingUseCase,
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

    var isFirstLaunch = when(getSettingValueUseCase(SettingsRepository.SETTING_IS_FIRST_STATISTICS_LAUNCH)) {
        SettingsRepository.FIRST_LAUNCH -> true
        else -> false
    }
    var tipsCount by state(0)
    fun nextTip() { tipsCount++ }

    val tips = mutableListOf(
        TipModel(resId = R.id.tv_avg_fuel_title, description = getString(R.string.tip_statistics_avg_fuel_description)),
        TipModel(resId = R.id.tv_moment_fuel_title, description = getString(R.string.tip_statistics_moment_fuel_description)),
        TipModel(resId = R.id.start_date_ib, description = getString(R.string.tip_statistics_date_change_description))
    )

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

        autorun(::tipsCount) {
            if (it >= tips.size) {
                isFirstLaunch = false
                setSettingUseCase(
                    SettingsRepository.SETTING_IS_FIRST_STATISTICS_LAUNCH,
                    SettingsRepository.NOT_FIRST_LAUNCH
                )
            }
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
            return if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) it.avgFuel
            else {
                val notes = notesByDate.filter {
                    it.type == NoteType.FUEL
                            && it.date in startDate..endDate
                }
                return if (notes.isNotEmpty()) {
                    val notesBefore = notesByDate.filter {
                        it.type == NoteType.FUEL
                                && it.date < startDate
                    }
                    val beforeMileage =
                        if (notesBefore.isNotEmpty()) notesBefore.maxOf { it.mileage }
                        else it.startMileage
                    notes.sumOf { it.liters } / (abs(notes.maxOf { it.mileage } - beforeMileage) / 100)
                } else 0.0
            }
        }
        return 0.0
    }

    private fun calculateMomentFuel(): Double {
        carItem?.let {
            return if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                it.momentFuel
            } else 0.0
        }
        return 0.0
    }

    private fun calculateAllFuel(): Double {
        carItem?.let {
            if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                return it.allFuel
            } else {
                val notes = notesByDate.filter {
                    it.type == NoteType.FUEL
                            && it.date in startDate..endDate
                }
                return if (notes.isEmpty()) 0.0
                else notes.sumOf { it.liters }
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
                val notes = notesByDate.filter {
                    it.type == NoteType.FUEL
                            && it.date in startDate..endDate
                }
                return if (notes.isEmpty()) 0.0
                else notes.sumOf { it.totalPrice }
            }
        }
        return 0.0
    }

    private fun calculateMileagePrice(): Double {
        carItem?.let {
            return if (startDate <= notesByDate.lastOrNull()?.date ?: 0L
                && endDate >= notesByDate.firstOrNull()?.date ?: 0L) {
                it.milPrice
            } else {
                val notes = notesByDate.filter { it.date in startDate..endDate }
                if (notes.isEmpty()) 0.0
                else {
                    val tPrice = notes.sumOf { it.totalPrice }
                    val tMileage = notes.maxOf { it.mileage } - notes.minOf { it.mileage }
                    if (tMileage != 0) tPrice / tMileage
                    else 0.0
                }
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
                val notes = notesByDate.filter { it.date in startDate..endDate }
                return if (notes.isEmpty()) 0.0
                else notes.sumOf { it.totalPrice }
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
                val notes = notesByDate.filter { it.date in startDate..endDate }
                return if (notes.isEmpty()) 0
                else notes.maxOf { it.mileage } - notes.minOf { it.mileage }
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
