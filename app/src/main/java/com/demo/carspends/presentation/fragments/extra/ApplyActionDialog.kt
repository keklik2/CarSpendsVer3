package com.demo.carspends.presentation.fragments.extra

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.demo.carspends.R
import java.lang.Exception

class ApplyActionDialog(activity: Activity, private val question: String?): Dialog(activity) {

    var onApplyClickListener: (() -> Unit)? = null
    var onDenyClickListener: (() -> Unit)? = null

    private lateinit var mainText: TextView
    private lateinit var buttonApply: Button
    private lateinit var buttonDeny: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.apply_action_dialog)
        setCancelable(false)
        this.window?.setBackgroundDrawableResource(android.R.color.transparent)

        mainText = findViewById(R.id.aad_tv_main_text)
        buttonApply = findViewById(R.id.aad_bn_yes)
        buttonDeny = findViewById(R.id.aad_bn_no)
    }

    override fun onStart() {
        initDialog()
        super.onStart()
    }

    private fun refactorString(title: String?): String {
        return title?.trim() ?: throw Exception("Unable to set null/empty string as question for ApplyActionDialog")
    }

    private fun initDialog() {
        val que = refactorString(question)

        mainText.text = que
        if (onApplyClickListener != null) {
            buttonApply.setOnClickListener {
                onApplyClickListener?.invoke()
                dismiss()
            }
        } else throw error("onApplyClickListener is not assigned for ApplyActionDialog")
        if (onDenyClickListener != null) {
            buttonDeny.setOnClickListener {
                onDenyClickListener?.invoke()
                dismiss()
            }
        } else throw error("onDenyClickListener is not assigned for ApplyActionDialog")
    }
}