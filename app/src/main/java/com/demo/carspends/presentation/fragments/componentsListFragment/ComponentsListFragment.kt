package com.demo.carspends.presentation.fragments.componentsListFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.databinding.ComponentsListFragmentBinding
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.fragments.componentsListFragment.recycleView.ComponentItemAdapter

class ComponentsListFragment: Fragment() {

    private var _binding: ComponentsListFragmentBinding? = null
    private val binding get() = _binding!!

    private val mainAdapter by lazy {
        ComponentItemAdapter()
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[ComponentsListViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLiveDateObservers()
        setupListeners()
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

    private fun setupRecyclerOnSwipeListener() {
        val callback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currItem = mainAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteComponent(currItem)
            }

        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.clfRvComponents)
    }

    private fun goToAddOrEditComponentItemFragment(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditComponentIntent(requireActivity(), id))
    }

    private fun goToAddOrEditComponentItemFragment() {
        startActivity(DetailElementsActivity.newAddOrEditComponentIntent(requireActivity()))
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