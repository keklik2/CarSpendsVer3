package com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demo.carspends.databinding.NoteExtraAddEditFragmentBinding
import com.demo.carspends.databinding.NoteFillingAddEditFragmentBinding
import com.demo.carspends.domain.note.NoteItem

class NoteFillingAddOrEditFragment: Fragment() {

    private var _binding: NoteFillingAddEditFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteFillingAddEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val EDIT_MODE_NOTE = "edit_mode_note"

        fun newAddInstance(): NoteFillingAddOrEditFragment {
            return NoteFillingAddOrEditFragment()
        }

        fun newEditInstance(note: NoteItem): NoteFillingAddOrEditFragment {
            return NoteFillingAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EDIT_MODE_NOTE, note)
                }
            }
        }
    }
}