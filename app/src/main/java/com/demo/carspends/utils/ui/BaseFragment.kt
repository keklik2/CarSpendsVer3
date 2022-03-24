package com.demo.carspends.utils.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.demo.carspends.CarSpendsApp
import com.demo.carspends.ViewModelFactory
import javax.inject.Inject

abstract class BaseFragment(layout: Int): Fragment(layout) {

    /**
     * Methods & variables that must be override
     */
    abstract val binding: ViewBinding
    abstract val viewModel: ViewModel
    abstract var setupListeners: (() -> Unit)?
    abstract var setupObservers: (() -> Unit)?


    /**
     * Methods & variables that are independent of input data
     */
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val component by lazy {
        (requireActivity().application as CarSpendsApp).component
    }

    override fun onResume() {
        super.onResume()

        setupListeners?.invoke()
        setupObservers?.invoke()
    }
}