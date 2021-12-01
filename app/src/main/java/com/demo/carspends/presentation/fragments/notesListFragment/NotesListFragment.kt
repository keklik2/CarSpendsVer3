package com.demo.carspends.presentation.fragments.notesListFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.carspends.databinding.NotesListFragmentBinding
import com.demo.carspends.presentation.fragments.notesListFragment.recyclerView.NoteItemAdapter

class NotesListFragment: Fragment() {

    private var _binding: NotesListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotesListViewModel by lazy {
        ViewModelProvider(this)[NotesListViewModel::class.java]
    }

    private val mainAdapter by lazy {
        NoteItemAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.notesList.observe(viewLifecycleOwner) {
            mainAdapter.submitList(it)
            binding.nlfRvNotes.adapter = mainAdapter
        }

        binding.nlfFbAddNote.setOnClickListener {
            viewModel.addNote()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotesListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}