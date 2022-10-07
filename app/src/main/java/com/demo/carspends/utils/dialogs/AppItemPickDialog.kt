package com.demo.carspends.utils.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.demo.carspends.R

class AppItemPickDialog(
    private val itemDialogContainer: AppItemDialogContainer
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.dialog_select_item_title)
            setItems(itemDialogContainer.itemsResId) { _, item ->
                itemDialogContainer.onItemSelected(item)
            }
            setNegativeButton(R.string.dialog_negative) { _, _ ->  }
        }.create()
    }
}
