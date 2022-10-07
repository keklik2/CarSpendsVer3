package com.demo.carspends.utils.dialogs

data class AppDialogContainer(
    val title: String? = null,
    val message: String,
    val onPositiveButtonClicked: (() -> Unit),
    val onNegativeButtonClicked: (() -> Unit)? = null,
    val onNeutralButtonClicked: (() -> Unit)? = null,
)
