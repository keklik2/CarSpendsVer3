package com.demo.carspends.presentation.fragments.notesList.recyclerView

import android.view.View
import com.demo.carspends.databinding.ItemNoteBinding
import com.demo.carspends.domain.note.NoteItem
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class NoteItemViewHolder(view: View) : RecyclerViewHolder<NoteItem>(view) {
    val binding = ItemNoteBinding.bind(view)
}