package com.demo.carspends.presentation.fragments.settings

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.SettingsFragmentBinding
import com.demo.carspends.utils.ui.baseFragment.BaseFragment


class SettingsFragment: BaseFragment(R.layout.settings_fragment) {
    override val binding: SettingsFragmentBinding by viewBinding()
    override val viewModel: SettingsViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        switchListener()
        statisticSpinnersListener()

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
                ::isExtendedFont bind { sfFontSizeSwitch.isChecked = it }
                ::statistics1Id bind { sfStatistics1Spinner.setSelection(it) }
                ::statistics2Id bind { sfStatistics2Spinner.setSelection(it) }
            }
        }
    }


    /**
     * Listeners
     */

    private fun switchListener() {
        binding.sfFontSizeSwitch.setOnClickListener { viewModel.changeFontSize() }
    }

    private fun acceptClickListener() {
        binding.sfButtonApply.setOnClickListener {
            viewModel.exit()
        }
    }

    private fun statisticSpinnersListener() {
        binding.sfStatistics1Spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    viewModel.changeStatistic1(pos)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
        }

        binding.sfStatistics2Spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    viewModel.changeStatistic2(pos)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
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
    }
}
