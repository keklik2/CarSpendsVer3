package com.demo.carspends.presentation.fragments.componentsList.recycleView

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.demo.carspends.R
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.getFormattedPercentAsStr
import me.ibrahimyilmaz.kiel.adapterOf

object ComponentItemAdapter {
    fun get(onClickFunc: ((ExtendedComponentItem) -> Unit)? = null) =
        adapterOf<ExtendedComponentItem> {
            diff(
                areContentsTheSame = { old, new -> old == new },
                areItemsTheSame = { old, new ->
                    old.componentItem.id == new.componentItem.id
                            && old.currMileage == new.currMileage
                },
            )
            register(
                layoutResource = R.layout.component_item,
                viewHolder = ::ComponentItemViewHolder,
                onBindViewHolder = { viewHolder, _, item ->
                    viewHolder.itemView.setOnClickListener {
                        onClickFunc?.invoke(item)
                    }

                    viewHolder.binding.apply {
                        with(item.componentItem) {
                            with(item) {
                                pbLeftMileage.progress = leftResourcePercent
                                tvLeftMileage.text =
                                    getFormattedPercentAsStr(leftResourcePercent)
                                tvTitle.text = title

                                // Setting resource statement value & color
                                tvResourceStatement.text = String.format(
                                    root.context.getString(R.string.text_measure_mileage_unit_for_formatting),
                                    getFormattedIntAsStrForDisplay(leftResourceValue)
                                )
                                tvResourceStatement.setTextColor(
                                    getColor(
                                        root.context,
                                        resourceColorId
                                    )
                                )
                                tvDate.text = getFormattedDate(date)
                            }
                        }
                    }
                }
            )
        }

    private fun getColor(context: Context, layoutId: Int): ColorStateList {
        return ColorStateList.valueOf(ContextCompat.getColor(context, layoutId))
    }
}
