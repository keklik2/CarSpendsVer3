package com.demo.carspends.presentation.fragments.statistics.graphics.adapter

import android.view.View
import com.demo.carspends.R
import com.demo.carspends.databinding.ItemGraphicBinding
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import lecho.lib.hellocharts.model.*
import me.ibrahimyilmaz.kiel.adapterOf
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder


object GraphAdapter {
    fun get() = adapterOf<GraphItem> {
        diff(
            areContentsTheSame = { old, new -> old == new },
            areItemsTheSame = { old, new -> old == new }
        )
        register(
            layoutResource = R.layout.item_graphic,
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

                    chart.columnChartData = ColumnChartData().apply {
                        columns = ArrayList<Column>().apply {
                            for (i in 0 until item.data.size) {
                                add(
                                    Column(
                                        ArrayList<SubcolumnValue>().apply {
                                            add(
                                                SubcolumnValue(item.data[i].toFloat()).apply {
                                                    setLabel("${getFormattedIntAsStrForDisplay(item.data[i])} ${item.measureUnit}")
                                                    color = getColor(R.color.vine, vh)
                                                }
                                            )
                                        }
                                    ).apply {
                                        setHasLabels(true)
                                        setHasLabelsOnlyForSelected(true)
                                    }
                                )
                            }
                        }
                        axisXBottom = Axis().apply {
                            values = ArrayList<AxisValue?>().apply {
                                for (i in 0 until item.labels.size) {
                                    add(AxisValue(i.toFloat()).setLabel(item.labels[i]))
                                }
                            }
                            setHasLines(true)
                        }
                        axisYLeft = Axis().apply {
                            maxLabelChars = item.data.maxOf { it }.toString().length
                        }
                    }
                }
            }
        )
    }

    private fun getString(id: Int, viewHolder: GraphicItemViewHolder): String =
        viewHolder.itemView.context.getString(id)

    private fun getColor(id: Int, viewHolder: GraphicItemViewHolder): Int =
        viewHolder.itemView.context.getColor(id)
}

class GraphicItemViewHolder(view: View) : RecyclerViewHolder<GraphItem>(view) {
    val binding = ItemGraphicBinding.bind(view)
}
