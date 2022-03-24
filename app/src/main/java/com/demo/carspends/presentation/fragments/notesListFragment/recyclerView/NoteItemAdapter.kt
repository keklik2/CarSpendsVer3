package com.demo.carspends.presentation.fragments.notesListFragment.recyclerView

import com.demo.carspends.R
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import me.ibrahimyilmaz.kiel.adapterOf

object NoteItemAdapter {
    fun get(onClickFunc: ((NoteItem) -> Unit)? = null) = adapterOf<NoteItem> {
        diff(
            areContentsTheSame = { old, new -> old == new },
            areItemsTheSame = { old, new -> old.id == new.id },
        )
        register(
            layoutResource = R.layout.note_item,
            viewHolder = ::NoteItemViewHolder,
            onBindViewHolder = { viewHolder, _, item ->
                viewHolder.itemView.setOnClickListener {
                    onClickFunc?.invoke(item)
                }

                viewHolder.binding.apply {
                    niIvTool.setImageResource(getImageID(item.type))
                    niTvTitle.text = item.title
                    val currencyState = root.context.getString(R.string.text_measure_currency)
                    "${getFormattedDoubleAsStrForDisplay(item.totalPrice)}$currencyState".also {
                        niTvAmount.text = it
                    }
                    if (item.type == NoteType.FUEL) "- ${item.fuelType.strName}".also {
                        niTvExtraInfo.text = it
                    }
                    niTvDate.text = getFormattedDate(item.date)
                }
            }
        )
    }

    private fun getImageID(noteType: NoteType): Int {
        return when (noteType) {
            NoteType.FUEL -> R.drawable.ic_baseline_local_gas_station_24
            NoteType.REPAIR -> R.drawable.ic_baseline_build_24
            NoteType.EXTRA -> R.drawable.ic_baseline_more_horiz_24
        }
    }
}