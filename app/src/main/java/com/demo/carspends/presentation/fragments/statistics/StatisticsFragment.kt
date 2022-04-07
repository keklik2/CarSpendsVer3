package com.demo.carspends.presentation.fragments.statistics

import android.content.Context
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.StatisticsFragmentBinding
import com.demo.carspends.utils.ui.BaseFragment

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
                ::sAvgFuel bind { sfTvAvgFuel.text = it }
                ::sMomentFuel bind { sfTvMomentFuel.text = it }
                ::sAllFuel bind { sfTvAllFuel.text = it }
                ::sFuelPrice bind { sfTvAllFuelPrice.text = it }
                ::sMileagePrice bind { sfTvMileagePrice.text = it }
                ::sAllPrice bind { sfTvAllPrice.text = it }
                ::sAllMileage bind { sfTvAllMileage.text = it }
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
