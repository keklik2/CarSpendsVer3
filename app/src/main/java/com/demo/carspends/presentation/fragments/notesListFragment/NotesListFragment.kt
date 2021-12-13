package com.demo.carspends.presentation.fragments.notesListFragment

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
import com.demo.carspends.R
import com.demo.carspends.databinding.NotesListFragmentBinding
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.fragments.extra.ApplyActionDialog
import com.demo.carspends.presentation.fragments.notesListFragment.recyclerView.NoteItemAdapter
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr

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

        checkForCarExisting()

        setNotesObservers()
        setupListeners()
    }

    private fun setupListeners() {
        setupAdapterOnClickListener()
        setupAdapterOnLongClickListener()
        setupRecyclerOnSwipeListener()
        setupAddNoteClickListener()
        setupAddNoteListeners()
        setupCarInfoListener()
    }

    private fun setNotesObservers() {
        viewModel.notesList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.nlfTvEmptyNotes.visibility = View.VISIBLE
            else {
                binding.nlfTvEmptyNotes.visibility = View.INVISIBLE
                mainAdapter.submitList(it)
                binding.nlfRvNotes.adapter = mainAdapter
            }
        }
    }

    private fun checkForCarExisting() {
        viewModel.carsList.observe(viewLifecycleOwner) {
            if(it.isEmpty()) startCarAddOrEdit()
            else {
                val carItem = it[0]
                with(binding) {
                    nlfTvCarTitle.text = carItem.title
                    "${getFormattedDoubleAsStr(carItem.momentFuel)} ${getString(R.string.text_measure_gas_charge)}"
                        .also { it1 -> nlfTvAvgFuel.text = it1 }
                    "${getFormattedDoubleAsStr(carItem.milPrice)}${getString(R.string.text_measure_currency)}"
                        .also { it1 -> nlfTvAvgCost.text = it1 }
                }
                viewModel.setCarItem(carItem.id)
            }
        }
    }

    private fun setupCarInfoListener() {
        binding.nlfCarInfoLayout.setOnClickListener {
            startCarAddOrEdit(getCarId())
        }
    }

    private fun getCarId(): Int {
        var id = 0
        viewModel.carsList.observe(viewLifecycleOwner) {
              id = it[0].id
        }
        return id
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

                val question = String.format(getString(R.string.text_delete_note_confirmation), currItem.title)
                val testDialog = ApplyActionDialog(requireActivity(), question)
                testDialog.onApplyClickListener = {
                    viewModel.deleteNote(currItem)
                }
                testDialog.onDenyClickListener = {
                    setNotesObservers()
                }
                testDialog.show()
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
            NoteType.FUEL -> startFillingNoteAddOrEdit(id)
            NoteType.REPAIR -> startRepairNoteAddOrEdit(id)
            NoteType.EXTRA -> startExtraNoteAddOrEdit(id)
        }
    }

    private fun goToAddNoteItemFragment(type: NoteType) {
        when(type) {
            NoteType.FUEL -> startFillingNoteAddOrEdit()
            NoteType.REPAIR -> startRepairNoteAddOrEdit()
            NoteType.EXTRA -> startExtraNoteAddOrEdit()
        }
    }

    private fun startFillingNoteAddOrEdit() {
        startActivity(DetailElementsActivity.newAddOrEditNoteFillingIntent(requireActivity(), getCarId()))
    }

    private fun startFillingNoteAddOrEdit(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditNoteFillingIntent(requireActivity(), getCarId(), id))
    }

    private fun startRepairNoteAddOrEdit() {
        startActivity(DetailElementsActivity.newAddOrEditNoteRepairIntent(requireActivity(), getCarId()))
    }

    private fun startRepairNoteAddOrEdit(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditNoteRepairIntent(requireActivity(), getCarId(), id))
    }

    private fun startExtraNoteAddOrEdit() {
        startActivity(DetailElementsActivity.newAddOrEditNoteExtraIntent(requireActivity()))
    }

    private fun startExtraNoteAddOrEdit(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditNoteExtraIntent(requireActivity(), id))
    }

    private fun startCarAddOrEdit() {
        startActivity(DetailElementsActivity.newAddOrEditCarIntent(requireActivity()))
    }

    private fun startCarAddOrEdit(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditCarIntent(requireActivity(), id))
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