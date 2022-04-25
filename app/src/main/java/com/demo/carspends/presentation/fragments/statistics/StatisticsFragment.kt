package com.demo.carspends.presentation.fragments.statistics

import android.content.Context
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.StatisticsFragmentBinding
import com.demo.carspends.utils.ui.baseFragment.BaseFragment

class StatisticsFragment: BaseFragment(R.layout.statistics_fragment) {
    override val binding: StatisticsFragmentBinding by viewBinding()
    override val viewModel: StatisticsViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {}
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
    }


    /**
     * Binds
     */
    private fun setupFieldsBind() {
        with(viewModel) {
            with(binding) {
                ::sAvgFuel bind { tvAvgFuel.text = it }
                ::sMomentFuel bind { tvMomentFuel.text = it }
                ::sAllFuel bind { tvAllFuel.text = it }
                ::sFuelPrice bind { tvAllFuelPrice.text = it }
                ::sMileagePrice bind { tvMileagePrice.text = it }
                ::sAllPrice bind { tvAllPrice.text = it }
                ::sAllMileage bind { tvAllMileage.text = it }
            }
        }
    }

    /**
     * Base functions to make class work as fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
