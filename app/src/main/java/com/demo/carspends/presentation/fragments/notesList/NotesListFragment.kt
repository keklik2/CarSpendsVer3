package com.demo.carspends.presentation.fragments.notesList

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.FragmentNotesListBinding
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.presentation.fragments.notesList.recyclerView.NoteItemAdapter
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import com.demo.carspends.utils.ui.tipShower.TipShower
import com.faltenreich.skeletonlayout.applySkeleton
import me.aartikov.sesame.loading.simple.Loading


class NotesListFragment : BaseFragment(R.layout.fragment_notes_list) {

    override val binding: FragmentNotesListBinding by viewBinding()
    override val vm: NotesListViewModel by viewModels { viewModelFactory }
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
        setupCarFieldsBind()
        setupShowTipBind()
        setupDateSpinnerBind()
        setupTypeSpinnerBind()
    }

    private val mainAdapter by lazy {
        NoteItemAdapter.get {
            vm.goToNoteAddOrEditFragment(it.type, it.id)
        }
    }

    private val tipShower by lazy {
        TipShower(requireActivity())
    }


    /**
     * Binds
     */
    private fun setupNotesBind() {
        binding.rvNotes.adapter = mainAdapter
        val skeleton = binding.rvNotes.applySkeleton(R.layout.item_skeleton_note)
        skeleton.showSkeleton()

        vm::notesListState bind {
            when (it) {
                is Loading.State.Data -> {
                    skeleton.showOriginal()

                    mainAdapter.submitList(it.data)
                    binding.tvEmptyNotes.visibility =
                        if (it.data.isNotEmpty()) View.INVISIBLE
                        else View.VISIBLE

                    vm.calculateComponentsResources()
                }
                is Loading.State.Loading -> skeleton.showSkeleton()
                else -> {
                    skeleton.showOriginal()

                    binding.tvEmptyNotes.visibility = View.VISIBLE
                    mainAdapter.submitList(emptyList())
                }
            }
        }
    }

    private fun setupCarFieldsBind() {
        vm::carTitle bind { binding.tvCarTitle.text = it }
        vm::statisticsField1 bind { binding.tvStatistics1.text = it }
        vm::statisticsField2 bind { binding.tvStatistics2.text = it }
        vm::statisticsField1Img bind {
            binding.tvStatistics1.setCompoundDrawablesRelativeWithIntrinsicBounds(
                it,
                0,
                0,
                0
            )
        }
        vm::statisticsField2Img bind {
            binding.tvStatistics2.setCompoundDrawablesRelativeWithIntrinsicBounds(
                it,
                0,
                0,
                0
            )
        }
    }

    private fun setupShowTipBind() {
        vm::tipsCount bind {
            if (it < vm.tips.size && vm.isFirstLaunch)
                tipShower.showTip(vm.tips[it]) { vm.nextTip() }
        }
    }

    private fun setupDateSpinnerBind() = vm::noteDate bind { binding.btnNoteDate.text = it }
    private fun setupTypeSpinnerBind() = vm::noteType bind { binding.btnNoteType.text = it }


    /**
     * Listeners
     */
    private fun setupSettingsListener() = binding.ivSettings.setOnClickListener { vm.goToSettingsFragment() }
    private fun setupCarInfoListener() = binding.carInfoLayout.setOnClickListener { vm.goToCarEditFragment() }
    private fun setupDateSpinnerListener() = binding.btnNoteDate.setOnClickListener { vm.setData() }
    private fun setupTypeSpinnerListener() = binding.btnNoteType.setOnClickListener { vm.setType() }


    /**
     * Method for showing/hiding floating buttons for adding notes
     */
    private fun setupAddNoteListeners() {
        with(binding) {
            fbAddFilling.setOnClickListener {
                floatingButtonsChangeStatement()
                vm.goToNoteAddOrEditFragment(NoteType.FUEL)
            }

            fbAddRepair.setOnClickListener {
                floatingButtonsChangeStatement()
                vm.goToNoteAddOrEditFragment(NoteType.REPAIR)
            }

            fbAddExtra.setOnClickListener {
                floatingButtonsChangeStatement()
                vm.goToNoteAddOrEditFragment(NoteType.EXTRA)
            }
        }
    }

    private fun setupAddNoteClickListener() {
        binding.fbAddNote.setOnClickListener {
            floatingButtonsChangeStatement()
        }
    }

    private fun floatingButtonsChangeStatement() {
        if (areFloatingButtonsShown()) setFloatingButtonsInvisible()
        else setFloatingButtonsVisible()
    }

    private fun areFloatingButtonsShown(): Boolean =
        binding.fbAddRepair.isVisible
                && binding.fbAddExtra.isVisible
                && binding.fbAddFilling.isVisible

    private fun setFloatingButtonsVisible() {
        binding.fbAddFilling.show()
        binding.fbAddRepair.show()
        binding.fbAddExtra.show()
    }

    private fun setFloatingButtonsInvisible() {
        binding.fbAddFilling.hide()
        binding.fbAddRepair.hide()
        binding.fbAddExtra.hide()
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
                binding.rvNotes.adapter?.notifyItemChanged(viewHolder.absoluteAdapterPosition)

                makeAlert(
                    AppDialogContainer(
                        title = getString(R.string.dialog_delete_title),
                        message = String.format(
                            getString(R.string.dialog_delete_note),
                            currItem.title
                        ),
                        onPositiveButtonClicked = { vm.deleteNote(currItem) }
                    )
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvNotes)
    }

    private fun setupRecyclerScrollListener() {
        binding.rvNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun isFLBAddNoteShown(): Boolean = binding.fbAddNote.isVisible

    private fun setFLBAddNoteVisibility(visible: Boolean) {
        if (visible) binding.fbAddNote.show()
        else binding.fbAddNote.hide()
    }


    /**
     * Basic functions to make class work as fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        vm.refreshData()
    }
}
