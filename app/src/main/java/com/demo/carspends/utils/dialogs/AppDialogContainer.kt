package com.demo.carspends.utils.dialogs

data class AppDialogContainer(
    val title: String? = null,
    val message: String,
    val positiveBtnCallback: (() -> Unit),
    val negativeBtnCallback: (() -> Unit)? = null,
    val neutralBtnCallback: (() -> Unit)? = null,
)
