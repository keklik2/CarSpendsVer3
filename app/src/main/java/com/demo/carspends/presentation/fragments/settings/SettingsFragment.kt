package com.demo.carspends.presentation.fragments.settings

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.SettingsFragmentBinding
import com.demo.carspends.utils.dialogs.AppItemPickDialog
import com.demo.carspends.utils.ui.baseFragment.BaseFragment


class SettingsFragment: BaseFragment(R.layout.settings_fragment) {
    override val binding: SettingsFragmentBinding by viewBinding()
    override val viewModel: SettingsViewModel by viewModels { viewModelFactory }
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
        with(viewModel) {
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
        binding.fontSizeSwitch.setOnClickListener { viewModel.changeFontSize() }
    }

    private fun acceptClickListener() {
        binding.buttonApply.setOnClickListener {
            viewModel.exit()
        }
    }

    private fun statisticSpinnersListener() {
        val statisticsListId = R.array.statistic_values

        binding.llStatisticsOne.setOnClickListener {
            AppItemPickDialog(statisticsListId) {
                viewModel.changeStatistic1(it)
            }.show(childFragmentManager, DIALOG_TAG)
        }

        binding.llStatisticsTwo.setOnClickListener {
            AppItemPickDialog(statisticsListId) {
                viewModel.changeStatistic2(it)
            }.show(childFragmentManager, DIALOG_TAG)
        }
    }

    private fun showInfoListener() {
        with(binding) {
            btnStatisticsOneInfo.setOnClickListener {

            }

            btnStatisticsTwoInfo.setOnClickListener {

            }
        }
    }


    /**
     * Base functions to make class work as fragment
     */
    private fun setupBackPresser() {
        requireActivity().onBackPressedDispatcher.addCallback(object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.exit()
                }
            })
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val IMAGE_TEST = 1
        private const val DIALOG_TAG = "dialog_tag"
    }
}
