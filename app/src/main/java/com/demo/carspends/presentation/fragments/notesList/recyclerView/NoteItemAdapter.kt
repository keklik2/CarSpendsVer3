package com.demo.carspends.presentation.fragments.notesList.recyclerView

import android.view.View
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
            layoutResource = R.layout.note_item,
            viewHolder = ::NoteItemViewHolder,
            onBindViewHolder = { viewHolder, _, item ->
                viewHolder.itemView.setOnClickListener {
                    onClickFunc?.invoke(item)
                }

                viewHolder.binding.apply {
                    niIvTool.setImageResource(getImageID(item.type))
                    niTvTitle.text = item.title
                    niTvAmount.text = String.format(
                        root.context.getString(R.string.text_measure_currency_for_formatting),
                        getFormattedDoubleAsStrForDisplay(item.totalPrice)
                    )
                    if (item.type == NoteType.FUEL) {
                        niTvExtraInfo.text = String.format(
                            "- ${item.fuelType.toString(root.context)}",
                            item.fuelType.toString(root.context)
                        )
                    }
                    niTvDate.text = getFormattedDate(item.date)
                    hasPicturesImage.setVisibility(item.hasPictures)
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
