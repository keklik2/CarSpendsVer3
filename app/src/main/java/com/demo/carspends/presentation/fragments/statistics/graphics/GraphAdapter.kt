package com.demo.carspends.presentation.fragments.statistics.graphics

import android.view.View
import com.demo.carspends.R
import com.demo.carspends.databinding.GraphicItemBinding
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

object GraphAdapter {
    fun get() = adapterOf<GraphItem> {
        diff(
            areContentsTheSame = { old, new -> old == new },
            areItemsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.graphic_item,
            viewHolder = ::GraphicItemViewHolder,
            onBindViewHolder = {vh, _, item ->
                with(vh.binding) {
                    tvGraphTitle.text = item.title
                    tvGraphMeasure.text = item.measure

                    tvGraphMeasure.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        item.drawableRes,
                        0,
                        0,
                        0
                    )

                    chart.setBottomTextList(ArrayList(item.bottomTextList))
                    chart.setDataList(ArrayList(item.dataList), item.maxHeight)
                }
            }
        )
    }
}

class GraphicItemViewHolder(view: View) : RecyclerViewHolder<GraphItem>(view) {
    val binding = GraphicItemBinding.bind(view)
}
