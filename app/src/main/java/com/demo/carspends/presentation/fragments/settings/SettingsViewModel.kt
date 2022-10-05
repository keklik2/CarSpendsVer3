package com.demo.carspends.presentation.fragments.settings

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.R
import com.demo.carspends.Screens
import com.demo.carspends.domain.settings.GetSettingValueUseCase
import com.demo.carspends.domain.settings.SetSettingUseCase
import com.demo.carspends.domain.settings.SettingsRepository.Companion.FONT_SIZE_LARGE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.FONT_SIZE_NORMAL
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.state
import javax.inject.Inject
import com.demo.carspends.domain.settings.SettingsRepository.Companion.SETTING_FONT_SIZE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.SETTING_STATISTIC_ONE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.SETTING_STATISTIC_TWO
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_ALL_FUEL
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_ALL_MILEAGE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_ALL_PRICE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_AVG_FUEL
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_FUEL_PRICE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_MILEAGE_PRICE
import com.demo.carspends.domain.settings.SettingsRepository.Companion.STATISTIC_MOMENT_FUEL
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.dialogs.AppItemDialogContainer
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.dataOrNull
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.stateFromFlow
import java.io.IOException
import java.lang.Integer.max

class SettingsViewModel @Inject constructor(
    private val setSettingUseCase: SetSettingUseCase,
    private val getSettingValueUseCase: GetSettingValueUseCase,
    private val router: Router,
    private val app: Application
) : BaseViewModel(app) {
    fun exit() = router.replaceScreen(Screens.HomePage())
    private fun reload() = router.replaceScreen(Screens.Settings())

    var isExtendedFont by state(false)
    var statistics1Id by state(0)
    var statistics2Id by state(0)

    private val fontSettingLoading = OrdinaryLoading(
        viewModelScope,
        load = { getSettingValueUseCase(SETTING_FONT_SIZE) }
    )
    private val fontSettingState by stateFromFlow(fontSettingLoading.stateFlow)

    private val statistic1Loading = OrdinaryLoading(
        viewModelScope,
        load = { getSettingValueUseCase(SETTING_STATISTIC_ONE) }
    )
    private val statistic1State by stateFromFlow(statistic1Loading.stateFlow)

    private val statistic2Loading = OrdinaryLoading(
        viewModelScope,
        load = { getSettingValueUseCase(SETTING_STATISTIC_TWO) }
    )
    private val statistic2State by stateFromFlow(statistic2Loading.stateFlow)


    init {
        fontSettingLoading.refresh()
        statistic1Loading.refresh()
        statistic2Loading.refresh()

        autorun(::fontSettingState) {
            when (it) {
                is Loading.State.Data ->
                    isExtendedFont = isFontLarge(it.data.toString().toFloat())
            }
        }

        autorun(::statistic1State) {
            when (it) {
                is Loading.State.Data ->
                    statistics1Id = getStatisticId(it.data.toString())
            }
        }

        autorun(::statistic2State) {
            when (it) {
                is Loading.State.Data ->
                    statistics2Id = getStatisticId(it.data.toString())
            }
        }
    }

    private fun isFontLarge(fontSize: Float): Boolean {
        return when (fontSize) {
            FONT_SIZE_NORMAL -> false
            else -> true
        }
    }

    fun changeFontSize() {
        when (isExtendedFont) {
            true -> setSettingUseCase(SETTING_FONT_SIZE, FONT_SIZE_NORMAL)
            false -> setSettingUseCase(SETTING_FONT_SIZE, FONT_SIZE_LARGE)
        }
        isExtendedFont = !isExtendedFont
        reload()
    }

    private fun getStatisticId(statistic: String): Int {
        return when (statistic) {
            STATISTIC_AVG_FUEL -> 0
            STATISTIC_MOMENT_FUEL -> 1
            STATISTIC_ALL_FUEL -> 2
            STATISTIC_FUEL_PRICE -> 3
            STATISTIC_MILEAGE_PRICE -> 4
            STATISTIC_ALL_PRICE -> 5
            else -> 6
        }
    }

    fun statisticOne() {
        showItemListDialog(
            AppItemDialogContainer(R.array.statistic_values) {
                changeStatistic1(it)
            }
        )
    }

    fun statisticTwo() {
        showItemListDialog(
            AppItemDialogContainer(R.array.statistic_values) {
                changeStatistic2(it)
            }
        )
    }

    private fun changeStatistic1(position: Int) {
        val newField = when (position) {
            0 -> STATISTIC_AVG_FUEL
            1 -> STATISTIC_MOMENT_FUEL
            2 -> STATISTIC_ALL_FUEL
            3 -> STATISTIC_FUEL_PRICE
            4 -> STATISTIC_MILEAGE_PRICE
            5 -> STATISTIC_ALL_PRICE
            else -> STATISTIC_ALL_MILEAGE
        }
        setSettingUseCase(SETTING_STATISTIC_ONE, newField)
        statistic1Loading.refresh()
    }

    private fun changeStatistic2(position: Int) {
        val newField = when (position) {
            0 -> STATISTIC_AVG_FUEL
            1 -> STATISTIC_MOMENT_FUEL
            2 -> STATISTIC_ALL_FUEL
            3 -> STATISTIC_FUEL_PRICE
            4 -> STATISTIC_MILEAGE_PRICE
            5 -> STATISTIC_ALL_PRICE
            else -> STATISTIC_ALL_MILEAGE
        }
        setSettingUseCase(SETTING_STATISTIC_TWO, newField)
        statistic2Loading.refresh()
    }

    fun showInfoStatistics() {
        showAlert(
            AppDialogContainer(
                getString(R.string.dialog_info_title),
                message = getString(R.string.dialog_info_statistics),
                positiveBtnCallback = {  }
            )
        )
    }

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
