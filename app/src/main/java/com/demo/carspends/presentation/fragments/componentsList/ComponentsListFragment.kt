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
import com.demo.carspends.utils.ui.BaseFragment

class ComponentsListFragment : BaseFragment(R.layout.components_list_fragment) {
    override val binding: ComponentsListFragmentBinding by viewBinding()
    override val viewModel: ComponentsListViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupRecyclerOnSwipeListener()
        setupRecyclerScrollListener()

        setupAddComponentButtonListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setComponentsObserver()
    }

    private val mainAdapter = ComponentItemAdapter.get {
        viewModel.goToComponentAddOrEdit(it.componentItem.id)
    }

    private fun setComponentsObserver() {
        binding.clfRvComponents.adapter = mainAdapter
        viewModel.componentsList.observe(viewLifecycleOwner) {
            viewModel.carsList.observe(viewLifecycleOwner) { it1 ->
                mainAdapter.submitList(it.map { it2 -> ExtendedComponentItem(it2, it1.first().mileage) })
                binding.clfTvEmptyNotes.visibility = if (it.isEmpty()) View.VISIBLE
                else View.INVISIBLE
            }

        }
    }

    private fun setupAddComponentButtonListener() {
        binding.clfFbAddComponent.setOnClickListener {
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
                val currItem = mainAdapter.currentList[viewHolder.absoluteAdapterPosition].componentItem

                AlertDialog.Builder(requireActivity())
                    .setMessage(
                        String.format(
                            getString(R.string.dialog_delete_component),
                            currItem.title
                        )
                    )
                    .setPositiveButton(R.string.button_apply) { _, _ ->
                        viewModel.deleteComponent(currItem)
                    }
                    .setNegativeButton(R.string.button_deny) { _, _ ->
                        setComponentsObserver()
                    }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.clfRvComponents)
    }

    private fun setupRecyclerScrollListener() {
        binding.clfRvComponents.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0) if (isFLBAddComponentShown()) setFLBAddComponentVisibility(false)
                if(dy < 0) if (!isFLBAddComponentShown()) setFLBAddComponentVisibility(true)
            }
        })
    }

    private fun isFLBAddComponentShown(): Boolean = binding.clfFbAddComponent.isVisible

    private fun setFLBAddComponentVisibility(visible: Boolean) {
        if (visible) binding.clfFbAddComponent.show()
        else binding.clfFbAddComponent.hide()
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
