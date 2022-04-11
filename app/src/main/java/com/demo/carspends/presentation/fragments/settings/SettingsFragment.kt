package com.demo.carspends.presentation.fragments.settings

import android.content.Context
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.SettingsFragmentBinding
import com.demo.carspends.utils.ui.BaseFragment

class SettingsFragment: BaseFragment(R.layout.settings_fragment) {
    override val binding: SettingsFragmentBinding by viewBinding()
    override val viewModel: SettingsViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        switchListener()
        acceptClickListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
    }

    private fun setupFieldsBind() {
        viewModel::isExtendedFont bind {
            binding.sfFontSizeSwitch.isChecked = it
        }
    }

    private fun switchListener() {
        binding.sfFontSizeSwitch.setOnClickListener { viewModel.changeFontSize() }
    }

    private fun acceptClickListener() {
        binding.sfButtonApply.setOnClickListener {
            viewModel.exit()
        }
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
