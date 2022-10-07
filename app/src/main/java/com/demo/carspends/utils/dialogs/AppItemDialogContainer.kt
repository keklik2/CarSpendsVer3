package com.demo.carspends.utils.dialogs

data class AppItemDialogContainer(
    val itemsResId: Int,
    val onItemSelected: (Int) -> Unit
)
