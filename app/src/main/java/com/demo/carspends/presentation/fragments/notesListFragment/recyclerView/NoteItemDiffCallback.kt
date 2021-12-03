package com.demo.carspends.presentation.fragments.notesListFragment.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.demo.carspends.domain.note.NoteItem

class NoteItemDiffCallback: DiffUtil.ItemCallback<NoteItem>() {
    override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean =
        oldItem == newItem
}