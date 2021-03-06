package com.demo.carspends.presentation.fragments.statistics.graphics

import android.content.Context
import android.util.Log
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.GraphicsFragmentBinding
import com.demo.carspends.presentation.fragments.statistics.graphics.GraphicsViewModel.Companion.DATE_ALL_TIME
import com.demo.carspends.presentation.fragments.statistics.graphics.GraphicsViewModel.Companion.DATE_MONTH
import com.demo.carspends.presentation.fragments.statistics.graphics.GraphicsViewModel.Companion.DATE_WEEK
import com.demo.carspends.presentation.fragments.statistics.graphics.GraphicsViewModel.Companion.DATE_YEAR
import com.demo.carspends.presentation.fragments.statistics.graphics.adapter.GraphAdapter
import com.demo.carspends.utils.setVisibility
import com.demo.carspends.utils.ui.baseFragment.BaseFragment

class GraphicsFragment : BaseFragment(R.layout.graphics_fragment) {
    override val binding: GraphicsFragmentBinding by viewBinding()
    override val viewModel: GraphicsViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDateChangeListener()
        setupRecyclerScrollListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupRecyclerBind()
    }

    private val mainAdapter by lazy { GraphAdapter.get() }


    /**
     * Bind functions
     */
    private fun setupRecyclerBind() {
        binding.rvGraphics.adapter = mainAdapter

        viewModel::testGraphItem bind {
            mainAdapter.submitList(it)
            if (it.isEmpty()) binding.tvEmptyGraphs.setVisibility(true)
            else binding.tvEmptyGraphs.setVisibility(false)
        }
    }


    /**
     * Listener functions
     */
    private fun setupDateChangeListener() {
        binding.fbDate.setOnClickListener {
            setupDateType()
        }
    }

    private fun setupRecyclerScrollListener() {
        binding.rvGraphics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (isFBDateShown()) setFBDateVisibility(false)
                }
                if (dy < 0) if (!isFBDateShown()) setFBDateVisibility(true)
            }
        })
    }


    /**
     * Additional functions
     */
    private fun isFBDateShown(): Boolean = binding.fbDate.isVisible

    private fun setupDateType() {
        val popupmenu = PopupMenu(requireActivity(), binding.fbDate).apply {
            inflate(R.menu.popup_menu)
        }

        popupmenu.setOnMenuItemClickListener {
            viewModel.dateType = when(it.itemId) {
                R.id.menu_week -> DATE_WEEK
                R.id.menu_month -> DATE_MONTH
                R.id.menu_year -> DATE_YEAR
                else -> DATE_ALL_TIME
            }
            true
        }
        popupmenu.show()
    }

    private fun setFBDateVisibility(visible: Boolean) {
        if (visible) binding.fbDate.show()
        else binding.fbDate.hide()
    }


    /**
     * Base functions to make class work as Fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
