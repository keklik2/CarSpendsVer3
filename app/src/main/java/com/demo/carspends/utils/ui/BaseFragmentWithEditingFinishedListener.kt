package com.demo.carspends.utils.ui

import android.content.Context
import com.demo.carspends.presentation.fragments.OnEditingFinishedListener
import java.lang.Exception

abstract class BaseFragmentWithEditingFinishedListener(layout: Int): BaseFragment(layout) {
    lateinit var onEditingFinishedListener: OnEditingFinishedListener
    abstract fun setupCanCloseScreenObserver()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) onEditingFinishedListener = context
        else throw Exception("Activity must implement OnEditingFinishedListener")
    }
}