package com.demo.carspends.presentation.fragments.noteRepairAddOrEditFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demo.carspends.databinding.NoteRepairAddEditFragmentBinding
import com.demo.carspends.domain.note.NoteItem

class NoteRepairAddOrEditFragment: Fragment() {

    private var _binding: NoteRepairAddEditFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteRepairAddEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val EDIT_MODE_NOTE = "edit_mode_note"

        fun newAddInstance(): NoteRepairAddOrEditFragment {
            return NoteRepairAddOrEditFragment()
        }

        fun newEditInstance(note: NoteItem): NoteRepairAddOrEditFragment {
            return NoteRepairAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EDIT_MODE_NOTE, note)
                }
            }
        }
    }
}