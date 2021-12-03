package com.demo.carspends.presentation.fragments.componentAddOrEditFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demo.carspends.databinding.ComponentAddEditFragmentBinding
import com.demo.carspends.domain.component.ComponentItem

class ComponentAddOrEditFragment: Fragment() {

    private var _binding: ComponentAddEditFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComponentAddEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val EDIT_MODE_COMPONENT = "edit_mode_component"

        fun newAddInstance(): ComponentAddOrEditFragment {
            return ComponentAddOrEditFragment()
        }

        fun newEditInstance(component: ComponentItem): ComponentAddOrEditFragment {
            return ComponentAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EDIT_MODE_COMPONENT, component)
                }
            }
        }
    }
}