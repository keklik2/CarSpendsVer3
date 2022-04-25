package com.demo.carspends.presentation.fragments.componentsList

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.ComponentsListFragmentBinding
import com.demo.carspends.presentation.fragments.componentsList.recycleView.ComponentItemAdapter
import com.demo.carspends.presentation.fragments.componentsList.recycleView.ExtendedComponentItem
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import com.faltenreich.skeletonlayout.applySkeleton
import me.aartikov.sesame.loading.simple.Loading

class ComponentsListFragment : BaseFragment(R.layout.components_list_fragment) {
    override val binding: ComponentsListFragmentBinding by viewBinding()
    override val viewModel: ComponentsListViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupRecyclerOnSwipeListener()
        setupRecyclerScrollListener()

        setupAddComponentButtonListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupComponentsBind()
    }

    private val mainAdapter = ComponentItemAdapter.get {
        viewModel.goToComponentAddOrEdit(it.componentItem.id)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    private fun setupComponentsBind() {
        binding.rvComponents.adapter = mainAdapter
        val skeleton = binding.rvComponents.applySkeleton(R.layout.note_item_skeleton)
        skeleton.showSkeleton()

        viewModel::componentsListState bind {
            when (it) {
                is Loading.State.Data -> {
                    skeleton.showOriginal()

                    mainAdapter.submitList(it.data.map
                        { it1 -> ExtendedComponentItem(it1, viewModel.mileage) }
                    )
                    binding.tvEmptyNotes.visibility =
                        if (it.data.isNotEmpty()) View.INVISIBLE
                        else View.VISIBLE
                }
                is Loading.State.Loading -> skeleton.showSkeleton()
                else -> {
                    skeleton.showOriginal()

                    binding.tvEmptyNotes.visibility = View.VISIBLE
                    mainAdapter.submitList(emptyList())
                }
            }
        }
    }

    private fun setupAddComponentButtonListener() {
        binding.fbAddComponent.setOnClickListener {
            viewModel.goToComponentAddOrEdit()
        }
    }

    private fun setupRecyclerOnSwipeListener() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currItem =
                    mainAdapter.currentList[viewHolder.absoluteAdapterPosition].componentItem
                binding.rvComponents.adapter?.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(
                        String.format(
                            getString(R.string.dialog_delete_component),
                            currItem.title
                        )
                    )
                    .setPositiveButton(R.string.button_apply) { _, _ ->
                        viewModel.deleteComponent(currItem)
                    }
                    .setNegativeButton(R.string.button_deny) { _, _ -> }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvComponents)
    }

    private fun setupRecyclerScrollListener() {
        binding.rvComponents.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) if (isFLBAddComponentShown()) setFLBAddComponentVisibility(false)
                if (dy < 0) if (!isFLBAddComponentShown()) setFLBAddComponentVisibility(true)
            }
        })
    }

    private fun isFLBAddComponentShown(): Boolean = binding.fbAddComponent.isVisible

    private fun setFLBAddComponentVisibility(visible: Boolean) {
        if (visible) binding.fbAddComponent.show()
        else binding.fbAddComponent.hide()
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
