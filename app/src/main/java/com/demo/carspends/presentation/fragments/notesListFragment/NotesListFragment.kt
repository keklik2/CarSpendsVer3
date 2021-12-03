package com.demo.carspends.presentation.fragments.notesListFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.databinding.NotesListFragmentBinding
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.fragments.notesListFragment.recyclerView.NoteItemAdapter
import com.demo.carspends.utils.getFormattedDate

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

        setLiveDateObservers()
        setupListeners()
    }

    private fun setupListeners() {
        setupAdapterOnClickListener()
        setupAdapterOnLongClickListener()
        setupRecyclerOnSwipeListener()
        setupAddNoteClickListener()
        setupAddNoteListeners()
    }

    private fun setLiveDateObservers() {
        viewModel.notesList.observe(viewLifecycleOwner) {
            mainAdapter.submitList(it)
            binding.nlfRvNotes.adapter = mainAdapter
        }
    }

    private fun setupAddNoteListeners() {
        with(binding) {
            nlfFbAddFilling.setOnClickListener {
                goToAddNoteItemFragment(NoteType.FUEL)
            }

            nlfFbAddRepair.setOnClickListener {
                goToAddNoteItemFragment(NoteType.REPAIR)
            }

            nlfFbAddExtra.setOnClickListener {
                goToAddNoteItemFragment(NoteType.EXTRA)
            }
        }
    }

    /** Method for showing/hiding floating buttons for adding notes */
    private fun setupAddNoteClickListener() {
        binding.nlfFbAddNote.setOnClickListener {
            floatingButtonsChangeStatement()
        }
    }

    private fun floatingButtonsChangeStatement() {
        if (areFloatingButtonsShown()) setFloatingButtonsInvisible()
        else setFloatingButtonsVisible()
    }

    private fun areFloatingButtonsShown(): Boolean =
        binding.nlfFbAddRepair.isVisible
                && binding.nlfFbAddExtra.isVisible
                && binding.nlfFbAddFilling.isVisible

    private fun setFloatingButtonsVisible() {
        binding.nlfFbAddFilling.show()
        binding.nlfFbAddRepair.show()
        binding.nlfFbAddExtra.show()
    }

    private fun setFloatingButtonsInvisible() {
        binding.nlfFbAddFilling.hide()
        binding.nlfFbAddRepair.hide()
        binding.nlfFbAddExtra.hide()
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
                viewModel.deleteNote(currItem)
            }

        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.nlfRvNotes)
    }

    private fun setupAdapterOnClickListener() {
        mainAdapter.onClickListener = {
            goToEditNoteItemFragment(it.type, it.id)
        }
    }

    private fun setupAdapterOnLongClickListener() {
        mainAdapter.onLongClickListener = {
            Toast.makeText(activity, "Note: (${it.id}, ${getFormattedDate(it.date)})", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToEditNoteItemFragment(type: NoteType, id: Int) {
        when(type) {
            NoteType.FUEL -> TODO()
            NoteType.REPAIR -> TODO()
            NoteType.EXTRA -> startExtraNoteEdit(id)
        }
    }

    private fun goToAddNoteItemFragment(type: NoteType) {
        when(type) {
            NoteType.FUEL -> TODO()
            NoteType.REPAIR -> TODO()
            NoteType.EXTRA -> startExtraNoteAdd()
        }
    }

    private fun startExtraNoteAdd() {
        startActivity(DetailElementsActivity.newAddOrEditNoteExtraIntent(requireActivity()))
    }

    private fun startExtraNoteEdit(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditNoteExtraIntent(requireActivity(), id))
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