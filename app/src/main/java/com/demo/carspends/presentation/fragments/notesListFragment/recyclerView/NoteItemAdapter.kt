package com.demo.carspends.presentation.fragments.notesListFragment.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.demo.carspends.R
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.utils.getFormattedCurrency
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr

class NoteItemAdapter: ListAdapter<NoteItem, NoteItemViewHolder>(NoteItemDiffCallback()) {

    var onClickListener: ((NoteItem) -> Unit)? = null
    var onLongClickListener: ((NoteItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        return NoteItemViewHolder(
            LayoutInflater.from(
                parent.context
            )
                .inflate(
                    R.layout.note_item,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        val currNote = getItem(position)

        with(holder) {
            ivTool.setImageResource(getImageID(currNote.type))
            tvTitle.text = currNote.title
            tvAmount.text = getFormattedCurrency(currNote.totalPrice)
            if (currNote.type == NoteType.FUEL) tvExtraInfo.text =
                String.format("- %s", currNote.fuelType.strName)
            tvDate.text = getFormattedDate(currNote.date)

            view.setOnClickListener {
                onClickListener?.invoke(currNote)
            }

            view.setOnLongClickListener {
                onLongClickListener?.invoke(currNote)
                true
            }
        }
    }

    private fun getImageID(noteType: NoteType): Int {
        return when(noteType) {
            NoteType.FUEL -> R.drawable.ic_baseline_local_gas_station_24
            NoteType.REPAIR -> R.drawable.ic_baseline_build_24
            NoteType.EXTRA -> R.drawable.ic_baseline_more_horiz_24
        }
    }
}