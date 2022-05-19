package com.demo.carspends.presentation.fragments.statistics.graphics

import android.content.Context
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.GraphicsFragmentBinding
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import com.faltenreich.skeletonlayout.applySkeleton
import me.aartikov.sesame.loading.simple.Loading

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
//        val skeleton = binding.rvGraphics.applySkeleton(R.layout.graphic_item)
//        skeleton.showSkeleton()

        viewModel::testGraphItem bind {
            mainAdapter.submitList(it)
        }

//        viewModel::graphListState bind {
//            when (it) {
//                is Loading.State.Data -> {
//                    skeleton.showOriginal()
//                    mainAdapter.submitList(it.data)
//                }
//                is Loading.State.Loading -> skeleton.showSkeleton()
//                else -> {
//                    skeleton.showOriginal()
//                    mainAdapter.submitList(emptyList())
//                }
//            }
//        }
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
        viewModel.dateType
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
