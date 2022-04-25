package com.demo.carspends.presentation.fragments.notesList

import android.content.Context
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
import com.demo.carspends.presentation.fragments.notesList.recyclerView.NoteItemAdapter
import com.demo.carspends.utils.ui.BaseFragment
import com.faltenreich.skeletonlayout.applySkeleton
import me.aartikov.sesame.loading.simple.Loading
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.thread

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
        setupSettingsListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupNotesBind()
        bindCarFields()
    }

    private val mainAdapter by lazy {
        NoteItemAdapter.get {
            viewModel.goToNoteAddOrEditFragment(it.type, it.id)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    /**
     * Binds
     */
    private fun setupNotesBind() {
        binding.nlfRvNotes.adapter = mainAdapter
        val skeleton = binding.nlfRvNotes.applySkeleton(R.layout.note_item_skeleton)
        skeleton.showSkeleton()

        viewModel::notesListState bind {
            when (it) {
                is Loading.State.Data -> {
                    thread {
                        sleep(500)
                    }
                    skeleton.showOriginal()

                    mainAdapter.submitList(it.data)
                    binding.nlfTvEmptyNotes.visibility =
                        if (it.data.isNotEmpty()) View.INVISIBLE
                        else View.VISIBLE
                }
                is Loading.State.Loading -> skeleton.showSkeleton()
                else -> {
                    skeleton.showOriginal()

                    binding.nlfTvEmptyNotes.visibility = View.VISIBLE
                    mainAdapter.submitList(emptyList())
                }
            }
        }
    }

    private fun bindCarFields() {
        viewModel::carTitle bind { binding.nlfTvCarTitle.text = it }
        viewModel::statisticsField1 bind { binding.nlfTvStatistics1.text = it }
        viewModel::statisticsField2 bind { binding.nlfTvStatistics2.text = it }
        viewModel::statisticsField1Img bind { binding.nlfIvStatistics1.setImageResource(it) }
        viewModel::statisticsField2Img bind { binding.nlfIvStatistics2.setImageResource(it) }
    }


    /**
     * Listeners
     */
    private fun setupSettingsListener() {
        binding.nlfIvSettings.setOnClickListener {
            viewModel.goToSettingsFragment()
        }
    }

    private fun setupCarInfoListener() {
        binding.nlfCarInfoLayout.setOnClickListener {
            viewModel.goToCarEditFragment()
        }
    }

    private fun setupDateSpinnerListener() {
        binding.nlfSpinnerDate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    when (pos) {
                        0 -> viewModel.setData()
                        1 -> viewModel.setData(getYearDate())
                        2 -> viewModel.setData(getMonthDate())
                        else -> viewModel.setData(getWeekDate())
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }

    private fun setupTypeSpinnerListener() {
        binding.nlfSpinnerNoteType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    when (pos) {
                        0 -> viewModel.setType()
                        1 -> viewModel.setType(NoteType.FUEL)
                        2 -> viewModel.setType(NoteType.REPAIR)
                        else -> viewModel.setType(NoteType.EXTRA)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
    }


    /**
     * Additional functions
     */
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


    /**
     * Method for showing/hiding floating buttons for adding notes
     */
    private fun setupAddNoteListeners() {
        with(binding) {
            nlfFbAddFilling.setOnClickListener {
                floatingButtonsChangeStatement()
                viewModel.goToNoteAddOrEditFragment(NoteType.FUEL)
            }

            nlfFbAddRepair.setOnClickListener {
                floatingButtonsChangeStatement()
                viewModel.goToNoteAddOrEditFragment(NoteType.REPAIR)
            }

            nlfFbAddExtra.setOnClickListener {
                floatingButtonsChangeStatement()
                viewModel.goToNoteAddOrEditFragment(NoteType.EXTRA)
            }
        }
    }

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
                binding.nlfRvNotes.adapter?.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(
                        String.format(
                            getString(R.string.dialog_delete_note),
                            currItem.title
                        )
                    )
                    .setPositiveButton(R.string.button_apply) { _, _ ->
                        viewModel.deleteNote(currItem)
                    }
                    .setNegativeButton(R.string.button_deny) { _, _ -> }
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

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val MINUS_WEEK = -7
        private const val MINUS_ONE = -1
    }
}
