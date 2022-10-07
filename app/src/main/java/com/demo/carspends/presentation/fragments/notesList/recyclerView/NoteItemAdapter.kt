package com.demo.carspends.presentation.fragments.notesList.recyclerView

import com.demo.carspends.R
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.note.NoteType
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.setVisibility
import me.ibrahimyilmaz.kiel.adapterOf

object NoteItemAdapter {
    fun get(onClickFunc: ((NoteItem) -> Unit)? = null) = adapterOf<NoteItem> {
        diff(
            areContentsTheSame = { old, new -> old == new },
            areItemsTheSame = { old, new -> old.id == new.id },
        )
        register(
            layoutResource = R.layout.item_note,
            viewHolder = ::NoteItemViewHolder,
            onBindViewHolder = { viewHolder, _, item ->
                viewHolder.itemView.setOnClickListener {
                    onClickFunc?.invoke(item)
                }

                viewHolder.binding.apply {
                    ivTool.setImageResource(getImageID(item.type))
                    tvTitle.text = item.title
                    tvAmount.text = String.format(
                        root.context.getString(R.string.text_measure_currency_for_formatting),
                        getFormattedDoubleAsStrForDisplay(item.totalPrice)
                    )
                    if (item.type == NoteType.FUEL) {
                        tvExtraInfo.text = String.format(
                            "- ${item.fuelType.toString(root.context)}",
                            item.fuelType.toString(root.context)
                        )
                    }
                    tvDate.text = getFormattedDate(item.date)
                    hasPicturesImage.setVisibility(item.hasPictures)
                }
            }
        )
    }

    private fun getImageID(noteType: NoteType): Int {
        return when (noteType) {
            NoteType.FUEL -> R.drawable.ic_gas_station
            NoteType.REPAIR -> R.drawable.ic_repair
            NoteType.EXTRA -> R.drawable.ic_more_horiz
        }
    }


}
