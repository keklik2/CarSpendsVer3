package com.demo.carspends.utils.ui.baseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.property.PropertyHost

abstract class BaseViewModel(
    private val app: Application
): AndroidViewModel(app), PropertyHost {

    fun withScope(func: suspend () -> Unit) {
        viewModelScope.launch { func.invoke() }
    }

    fun getString(resource: Int): String = app.getString(resource)
}
