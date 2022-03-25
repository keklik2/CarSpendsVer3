package com.demo.carspends.presentation.fragments.componentsListFragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.ComponentsListFragmentBinding
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.extra.ApplyActionDialog
import com.demo.carspends.presentation.fragments.componentsListFragment.recycleView.ComponentItemAdapter
import com.demo.carspends.utils.ui.BaseFragment

class ComponentsListFragment : BaseFragment(R.layout.components_list_fragment) {
    override val binding: ComponentsListFragmentBinding by viewBinding()
    override val viewModel: ComponentsListViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupRecyclerItemOnClickListener()
        setupRecyclerOnSwipeListener()
        setupAddComponentButtonListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setComponentsObserver()
    }

    private var test = false
    private val mainAdapter by lazy {
        ComponentItemAdapter().apply {
            viewModel.carsList.observe(viewLifecycleOwner) {
                currMileage = it[0].mileage
            }
        }
    }

    private fun getCarId(): Int {
        var id = 0
        viewModel.carsList.observe(viewLifecycleOwner) {
            id = it[0].id
        }
        return id
    }

    private fun setComponentsObserver() {
        viewModel.componentsList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.clfTvEmptyNotes.visibility = View.VISIBLE
            else {
                binding.clfTvEmptyNotes.visibility = View.INVISIBLE
                mainAdapter.submitList(it)
                binding.clfRvComponents.adapter = mainAdapter
            }
        }
    }

    private fun setupRecyclerItemOnClickListener() {
        mainAdapter.onClickListener = {
            goToAddOrEditComponentItemFragment(it.id)
        }
    }

    private fun setupAddComponentButtonListener() {
        binding.clfFbAddComponent.setOnClickListener {
            goToAddOrEditComponentItemFragment()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecyclerOnSwipeListener() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                return if (test) {
                    test = false
                    0
                } else super.convertToAbsoluteDirection(flags, layoutDirection)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currItem = mainAdapter.currentList[viewHolder.adapterPosition]

                val question = String.format(
                    getString(R.string.text_delete_component_confirmation),
                    currItem.title
                )
                val testDialog = ApplyActionDialog(requireActivity(), question)
                testDialog.onApplyClickListener = {
                    viewModel.deleteComponent(currItem)
                }
                testDialog.onDenyClickListener = {
                    setComponentsObserver()
                }
                testDialog.show()
            }
        }


        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.clfRvComponents)
    }

    private fun goToAddOrEditComponentItemFragment() {
        startActivity(
            DetailElementsActivity.newAddOrEditComponentIntent(
                requireActivity(),
                getCarId()
            )
        )
    }

    private fun goToAddOrEditComponentItemFragment(id: Int) {
        startActivity(
            DetailElementsActivity.newAddOrEditComponentIntent(
                requireActivity(),
                getCarId(),
                id
            )
        )
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}