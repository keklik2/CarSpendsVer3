package com.demo.carspends.presentation.fragments.statistics.graphics.adapter

import android.util.Log
import android.view.View
import com.anychart.AnyChart
import com.anychart.AnyChart.column
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.demo.carspends.R
import com.demo.carspends.databinding.GraphicItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask


object GraphAdapter {
    fun get() = adapterOf<GraphItem> {
        diff(
            areContentsTheSame = { old, new -> old == new },
            areItemsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.graphic_item,
            viewHolder = ::GraphicItemViewHolder,
            onBindViewHolder = { vh, _, item ->
                with(vh.binding) {
                    tvGraphTitle.text = item.title
                    tvGraphMeasure.text = item.measure

                    tvGraphMeasure.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        item.drawableRes,
                        0,
                        0,
                        0
                    )

                    // CHART
                    with(chart) {
                        setBottomTextList(ArrayList(item.bottomTextList))
                        setDataList(ArrayList(item.dataList), item.maxHeight)
                    }
                }
            }
        )
    }

    private fun getString(id: Int, viewHolder: GraphicItemViewHolder): String =
        viewHolder.itemView.context.getString(id)
}

class GraphicItemViewHolder(view: View) : RecyclerViewHolder<GraphItem>(view) {
    val binding = GraphicItemBinding.bind(view)
}
