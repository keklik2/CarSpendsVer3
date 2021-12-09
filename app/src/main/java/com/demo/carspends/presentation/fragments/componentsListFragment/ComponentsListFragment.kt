package com.demo.carspends.presentation.fragments.componentsListFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.R
import com.demo.carspends.databinding.ComponentsListFragmentBinding
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.fragments.componentsListFragment.recycleView.ComponentItemAdapter
import com.demo.carspends.presentation.fragments.extra.ApplyActionDialog

class ComponentsListFragment: Fragment() {

    private var _binding: ComponentsListFragmentBinding? = null
    private val binding get() = _binding!!

    private var test = false

    private val mainAdapter by lazy {
        ComponentItemAdapter().apply {
            viewModel.carsList.observe(viewLifecycleOwner) {
                currMileage = it[0].mileage
            }
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[ComponentsListViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLiveDateObservers()
        setupListeners()
    }

    private fun getCarId(): Int {
        var id = 0
        viewModel.carsList.observe(viewLifecycleOwner) {
            id = it[0].id
        }
        return id
    }

    private fun setLiveDateObservers() {
        viewModel.componentsList.observe(viewLifecycleOwner) {
            mainAdapter.submitList(it)
            binding.clfRvComponents.adapter = mainAdapter
        }
    }

    private fun setupListeners() {
        setupRecyclerItemOnClickListener()
        setupRecyclerOnSwipeListener()
        setupAddComponentButtonListener()
    }

    private fun setupAddComponentButtonListener() {
        binding.clfFbAddComponent.setOnClickListener {
            goToAddOrEditComponentItemFragment()
        }
    }

    private fun setupRecyclerItemOnClickListener() {
        mainAdapter.onClickListener = {
            goToAddOrEditComponentItemFragment(it.id)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecyclerOnSwipeListener() {
        val callback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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

                val question = String.format(getString(R.string.text_delete_component_confirmation), currItem.title)
                val testDialog = ApplyActionDialog(requireActivity(), question)
                testDialog.onApplyClickListener = {
                    viewModel.deleteComponent(currItem)
                }
                testDialog.onDenyClickListener = {
                    setLiveDateObservers()
                }
                testDialog.show()
            }
        }


        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.clfRvComponents)
    }

    private fun goToAddOrEditComponentItemFragment() {
        startActivity(DetailElementsActivity.newAddOrEditComponentIntent(requireActivity(), getCarId()))
    }

    private fun goToAddOrEditComponentItemFragment(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditComponentIntent(requireActivity(), getCarId(), id))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComponentsListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}