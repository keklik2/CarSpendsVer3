package com.demo.carspends.presentation.fragments.statistics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.StatisticsFragmentBinding
import com.demo.carspends.presentation.fragments.statistics.graphics.GraphicsFragment
import com.demo.carspends.presentation.fragments.statistics.numerous.NumerousFragment
import com.demo.carspends.utils.ui.baseFragment.BaseFragment

class StatisticsFragment: BaseFragment(R.layout.statistics_fragment) {
    override val binding: StatisticsFragmentBinding by viewBinding()
    override val viewModel: StatisticsViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupRadioGroupClickListener()
    }
    override var setupBinds: (() -> Unit)? = {

    }


    /**
     * Listener functions
     */
    private fun setupRadioGroupClickListener() {
        binding.rgTopMenu.setOnCheckedChangeListener { _, i ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    when (i) {
                        NUMEROUS -> NumerousFragment()
                        else -> GraphicsFragment()
                    }
                )
                .commit()
        }
    }


    /**
     * Base functions to make class work as fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                NumerousFragment()
            )
            .commit()

        binding.rbNumerous.isChecked = true
        binding.rbGraph.isChecked = false
    }

    companion object {
        private const val NUMEROUS = R.id.rb_numerous
        private const val GRAPH = R.id.rb_graph
    }
}
