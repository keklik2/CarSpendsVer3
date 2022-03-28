package com.demo.carspends.presentation.fragments.notesList

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NotesListFragmentBinding
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.presentation.activities.DetailElementsActivity
import com.demo.carspends.presentation.fragments.notesList.recyclerView.NoteItemAdapter
import com.demo.carspends.utils.ui.BaseFragment
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import java.util.*

class NotesListFragment : BaseFragment(R.layout.notes_list_fragment) {

    override val binding: NotesListFragmentBinding by viewBinding()
    override val viewModel: NotesListViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupTypeSpinnerListener()
        setupDateSpinnerListener()

        setupRecyclerOnSwipeListener()
        setupRecyclerScrollListener()

        setupAddNoteClickListener()
        setupAddNoteListeners()
        setupCarInfoListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setNotesObserver()
    }


    private var date: Long? = null
    private var type: NoteType? = null
    private val mainAdapter by lazy {
        NoteItemAdapter.get {
            goToEditNoteItemFragment(it.type, it.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForCarExisting()
    }

    override fun onResume() {
        super.onResume()
        refreshSpinners()
    }

    private fun setupDateSpinnerListener() {
        binding.nlfSpinnerDate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    refreshDateSpinner(pos)
                    setNotesObserver()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun setupTypeSpinnerListener() {
        binding.nlfSpinnerNoteType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    refreshTypeSpinner(pos)
                    setNotesObserver()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun refreshDateSpinner(pos: Int) {
        date = when (pos) {
            0 -> null
            1 -> getYearDate()
            2 -> getMonthDate()
            else -> getWeekDate()
        }
    }

    private fun refreshTypeSpinner(pos: Int) {
        type = when (pos) {
            0 -> null
            1 -> NoteType.FUEL
            2 -> NoteType.REPAIR
            else -> NoteType.EXTRA
        }
    }

    private fun refreshSpinners() {
        with(binding) {
            refreshDateSpinner(nlfSpinnerDate.selectedItemPosition)
            refreshTypeSpinner(nlfSpinnerNoteType.selectedItemPosition)
        }
    }

    private fun setNewNotesList() {
        if (date != null) {
            if (type != null) viewModel.setTypedNotes(type!!, date!!)
            else viewModel.setAllNotes(date!!)
        } else {
            if (type != null) viewModel.setTypedNotes(type!!)
            else viewModel.setAllNotes()
        }
    }

    private fun getYearDate(): Long {
        val date = GregorianCalendar.getInstance().apply {
            add(GregorianCalendar.YEAR, MINUS_ONE)
        }.time.time
        return date
    }

    private fun getMonthDate(): Long {
        val date = GregorianCalendar.getInstance().apply {
            add(GregorianCalendar.MONTH, MINUS_ONE)
        }.time.time
        return date
    }

    private fun getWeekDate(): Long {
        val date = GregorianCalendar.getInstance().apply {
            add(GregorianCalendar.DATE, MINUS_WEEK)
        }.time.time
        return date
    }

    private fun setNotesObserver() {
        setNewNotesList()

        viewModel.notesList.observe(viewLifecycleOwner) {
            with(binding) {
                mainAdapter.submitList(it)
                nlfRvNotes.adapter = mainAdapter
                nlfTvEmptyNotes.visibility = if (it.isEmpty()) View.VISIBLE
                else View.INVISIBLE
            }
        }
    }

    private fun checkForCarExisting() {
        viewModel.carsList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) startCarAddOrEdit()
            else {
                val carItem = it[0]
                with(binding) {
                    nlfTvCarTitle.text = carItem.title
                    nlfTvAvgFuel.text = String.format(
                        getString(R.string.text_measure_gas_charge_for_formatting),
                        getFormattedDoubleAsStrForDisplay(carItem.momentFuel)
                    )
                    nlfTvAvgCost.text = String.format(
                        getString(R.string.text_measure_currency_for_formatting),
                        getFormattedDoubleAsStrForDisplay(carItem.milPrice)
                    )
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
                floatingButtonsChangeStatement()
                goToAddNoteItemFragment(NoteType.FUEL)
            }

            nlfFbAddRepair.setOnClickListener {
                floatingButtonsChangeStatement()
                goToAddNoteItemFragment(NoteType.REPAIR)
            }

            nlfFbAddExtra.setOnClickListener {
                floatingButtonsChangeStatement()
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
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currItem = mainAdapter.currentList[viewHolder.absoluteAdapterPosition]
                AlertDialog.Builder(requireActivity())
                    .setMessage(
                        String.format(
                            getString(R.string.dialog_delete_note),
                            currItem.title
                        )
                    )
                    .setPositiveButton(R.string.button_apply) { _, _ ->
                        viewModel.deleteNote(currItem)
                    }
                    .setNegativeButton(R.string.button_deny) { _, _ ->
                        setNotesObserver()
                    }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.nlfRvNotes)
    }

    private fun setupRecyclerScrollListener() {
        binding.nlfRvNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (isFLBAddNoteShown()) setFLBAddNoteVisibility(false)
                    if (areFloatingButtonsShown()) setFloatingButtonsInvisible()
                }
                if (dy < 0) if (!isFLBAddNoteShown()) setFLBAddNoteVisibility(true)
            }
        })
    }

    private fun isFLBAddNoteShown(): Boolean = binding.nlfFbAddNote.isVisible

    private fun setFLBAddNoteVisibility(visible: Boolean) {
        if (visible) binding.nlfFbAddNote.show()
        else binding.nlfFbAddNote.hide()
    }

    private fun goToEditNoteItemFragment(type: NoteType, id: Int) {
        when (type) {
            NoteType.FUEL -> startFillingNoteAddOrEdit(id)
            NoteType.REPAIR -> startRepairNoteAddOrEdit(id)
            NoteType.EXTRA -> startExtraNoteAddOrEdit(id)
        }
    }

    private fun goToAddNoteItemFragment(type: NoteType) {
        when (type) {
            NoteType.FUEL -> startFillingNoteAddOrEdit()
            NoteType.REPAIR -> startRepairNoteAddOrEdit()
            NoteType.EXTRA -> startExtraNoteAddOrEdit()
        }
    }

    private fun startFillingNoteAddOrEdit() {
        startActivity(
            DetailElementsActivity.newAddOrEditNoteFillingIntent(
                requireActivity(),
                getCarId()
            )
        )
    }

    private fun startFillingNoteAddOrEdit(id: Int) {
        startActivity(
            DetailElementsActivity.newAddOrEditNoteFillingIntent(
                requireActivity(),
                getCarId(),
                id
            )
        )
    }

    private fun startRepairNoteAddOrEdit() {
        startActivity(
            DetailElementsActivity.newAddOrEditNoteRepairIntent(
                requireActivity(),
                getCarId()
            )
        )
    }

    private fun startRepairNoteAddOrEdit(id: Int) {
        startActivity(
            DetailElementsActivity.newAddOrEditNoteRepairIntent(
                requireActivity(),
                getCarId(),
                id
            )
        )
    }

    private fun startExtraNoteAddOrEdit() {
        startActivity(
            DetailElementsActivity.newAddOrEditNoteExtraIntent(
                requireActivity(),
                getCarId()
            )
        )
    }

    private fun startExtraNoteAddOrEdit(id: Int) {
        startActivity(
            DetailElementsActivity.newAddOrEditNoteExtraIntent(
                requireActivity(),
                getCarId(),
                id
            )
        )
    }

    private fun startCarAddOrEdit() {
        startActivity(DetailElementsActivity.newAddOrEditCarIntent(requireActivity()))
    }

    private fun startCarAddOrEdit(id: Int) {
        startActivity(DetailElementsActivity.newAddOrEditCarIntent(requireActivity(), id))
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val MINUS_WEEK = -7
        private const val MINUS_ONE = -1
    }
}
