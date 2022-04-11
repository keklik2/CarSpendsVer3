package com.demo.carspends.presentation.fragments.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.refresh
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.stateFromFlow

class SettingsViewModel @Inject constructor(
    private val setSettingUseCase: SetSettingUseCase,
    private val getSettingValueUseCase: GetSettingValueUseCase,
    private val router: Router,
    private val app: Application
): AndroidViewModel(app), PropertyHost {
    fun exit() = router.exit()
    fun reload() = router.replaceScreen(Screens.Settings())

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

    private val statistic2Loading = OrdinaryLoading(
        viewModelScope,
        load = { getSettingValueUseCase(SETTING_STATISTIC_TWO) }
    )


    init {
        fontSettingLoading.refresh()

        autorun(::fontSettingState) {

            when(it) {
                is Loading.State.Data -> {
                    isExtendedFont = isFontLarge(it.data.toString().toFloat())
                }
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
        when(isExtendedFont) {
            true -> setSettingUseCase(SETTING_FONT_SIZE, FONT_SIZE_NORMAL)
            false -> setSettingUseCase(SETTING_FONT_SIZE, FONT_SIZE_LARGE)
        }
        isExtendedFont = !isExtendedFont
        reload()
    }


    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
