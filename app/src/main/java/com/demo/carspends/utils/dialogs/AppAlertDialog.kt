package com.demo.carspends.utils.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.demo.carspends.R

class AppAlertDialog(
    private val container: AppDialogContainer
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setMessage(container.message)
            setPositiveButton(R.string.dialog_positive) { _, _ -> container.positiveBtnCallback() }
            container.title?.let { setTitle(it) }
            container.negativeBtnCallback?.let { setNegativeButton(R.string.dialog_negative) { _, _ -> it() } }
            container.neutralBtnCallback?.let { setNeutralButton(R.string.dialog_neutral) { _, _ -> it() } }
        }.create()
    }
}
