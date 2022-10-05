package com.demo.carspends.utils.ui.baseViewModel

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.dialogs.AppItemDialogContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.command
import java.util.*

abstract class BaseViewModel(
    private val app: Application
): AndroidViewModel(app), PropertyHost {

    val showAlert = command<AppDialogContainer>()
    val showItemListDialog = command<AppItemDialogContainer>()
    val showToast = command<String>()
    val showToastLong = command<String>()

    fun withScope(func: suspend () -> Unit) {
        viewModelScope.launch { func.invoke() }
    }

    fun getCurrentDate(): Long = Date().time

    fun getString(resource: Int): String = app.getString(resource)
    fun getDrawable(resource: Int): Drawable? = app.getDrawable(resource)

    override val propertyHostScope: CoroutineScope
        get() = viewModelScope
}
