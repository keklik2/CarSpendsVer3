package com.demo.carspends.presentation.fragments.componentsListFragment.recycleView

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.demo.carspends.R
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.getFormattedPercentsAsStr

class ComponentItemAdapter: ListAdapter<ComponentItem, ComponentItemViewHolder>(ComponentItemDiffCallback()) {

    var currMileage = 0
    set(value) {
        if(value > 0) field = value
    }
    var onClickListener: ((ComponentItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentItemViewHolder {
        return ComponentItemViewHolder(
            LayoutInflater.from(
                parent.context
            )
                .inflate(
                    R.layout.component_item,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ComponentItemViewHolder, position: Int) {
        val currComponent = getItem(position)

        with(holder) {
            val leftResourceValue = getLeftResourceValue(currComponent.resourceMileage, currComponent)
            val leftResourcePercent = getLeftResourcePercent(currComponent.resourceMileage, currComponent)
            val progressColor = getColor(view, getResourceColorId(leftResourcePercent))

            // Setting progressbar value & color
            with(binding) {
                ciPbLeftMileage.progress = leftResourcePercent

                ciTvLeftMileage.text = getFormattedPercentsAsStr(leftResourcePercent)
                ciTvTitle.text = currComponent.title

                // Setting resource statement value & color
                val milState = view.context.getString(R.string.text_measure_mileage_unit)
                "${getFormattedIntAsStrForDisplay(leftResourceValue)} $milState".also {
                    ciTvResourceStatement.text = it
                }
                ciTvResourceStatement.setTextColor(progressColor)

                ciTvDate.text = getFormattedDate(currComponent.date)
            }

            view.setOnClickListener {
                onClickListener?.invoke(currComponent)
            }
        }
    }

    private fun getColor(view: View, id: Int): ColorStateList {
        return ColorStateList.valueOf(ContextCompat.getColor(view.context, id))
    }

    private fun getResourceColorId(resource: Int): Int {
        return when(resource) {
            in 70..1000000 -> R.color.green
            in 35..70 -> R.color.yellow
            else -> R.color.vine
        }
    }

    private fun getLeftResourceValue(startProgress: Int, component: ComponentItem): Int {
        val res = component.resourceMileage - (currMileage - component.startMileage)
        return when {
            res > startProgress -> startProgress
            res > 0 -> res
            else -> 0
        }
    }

    private fun getLeftResourcePercent(startProgress: Int, component: ComponentItem): Int {
        val res = ((getLeftResourceValue(startProgress, component).toDouble() * 100) / component.resourceMileage).toInt()
        return when {
            res in 0..100 -> res
            res > 100 -> 100
            else -> 0
        }
    }
}