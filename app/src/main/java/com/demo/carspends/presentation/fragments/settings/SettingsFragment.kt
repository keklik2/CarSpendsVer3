package com.demo.carspends.presentation.fragments.settings

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.SettingsFragmentBinding
import com.demo.carspends.utils.dialogs.AppItemDialogContainer
import com.demo.carspends.utils.dialogs.AppItemPickDialog
import com.demo.carspends.utils.ui.baseFragment.BaseFragment


class SettingsFragment: BaseFragment(R.layout.settings_fragment) {
    override val binding: SettingsFragmentBinding by viewBinding()
    override val vm: SettingsViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        switchListener()
        statisticSpinnersListener()
        showInfoListener()

        acceptClickListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupBackPresser()
    }


    /**
     * Binds
     */
    private fun setupFieldsBind() {
        with(vm) {
            with(binding) {
                val statisticValues = resources.getStringArray(R.array.statistic_values)
                ::isExtendedFont bind { fontSizeSwitch.isChecked = it }
                ::statistics1Id bind { tvStatisticsOneDescription.text = statisticValues[it] }
                ::statistics2Id bind { tvStatisticsTwoDescription.text = statisticValues[it] }
            }
        }
    }


    /**
     * Listeners
     */

    private fun switchListener() {
        binding.llFontSize.setOnClickListener { vm.changeFontSize() }
        binding.fontSizeSwitch.setOnClickListener { vm.changeFontSize() }
    }

    private fun acceptClickListener() {
        binding.buttonApply.setOnClickListener {
            vm.exit()
        }
    }

    private fun statisticSpinnersListener() {
        binding.llStatisticsOne.setOnClickListener { vm.statisticOne() }
        binding.llStatisticsTwo.setOnClickListener { vm.statisticTwo() }
    }

    private fun showInfoListener() {
        with(binding) {
            btnStatisticsOneInfo.setOnClickListener { vm.showInfoStatistics() }
            btnStatisticsTwoInfo.setOnClickListener { vm.showInfoStatistics() }
        }
    }


    /**
     * Base functions to make class work as fragment
     */
    private fun setupBackPresser() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vm.exit()
                }
            })
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
