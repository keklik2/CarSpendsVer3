package com.demo.carspends.presentation.fragments.statistics

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.property.state
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(
    private val app: Application
): BaseViewModel(app) {

    var menu by state(NUM_FRAGMENT)

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope

    companion object {
        private const val NUM_FRAGMENT = 1
        private const val GRAPH_FRAGMENT = 2
    }
}
