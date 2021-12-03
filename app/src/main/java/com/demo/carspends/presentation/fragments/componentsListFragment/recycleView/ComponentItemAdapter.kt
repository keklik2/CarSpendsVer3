package com.demo.carspends.presentation.fragments.componentsListFragment.recycleView

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.demo.carspends.R
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedPercentsAsStr

class ComponentItemAdapter: ListAdapter<ComponentItem, ComponentItemViewHolder>(ComponentItemDiffCallback()) {

    /** Изменить currMileage: получать текущий пробег автомобиля из приложения */
    private val currMileage = 183000
    var onClickListener: ((NoteItem) -> Unit)? = null

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
            val leftResourcePercent = getLeftResourcePercent(currComponent)
            val progressColorID = getResourceColor(leftResourcePercent)

            // Setting progressbar value & color
            pbLeftMileage.progress = leftResourcePercent
            pbLeftMileage.progressTintList = ColorStateList.valueOf(progressColorID)

            tvLeftMileage.text = getFormattedPercentsAsStr(leftResourcePercent)
            tvTitle.text = currComponent.title

            // Setting resource statement value & color
            tvResourceStatement.text = getLeftResourceValue(currComponent).toString()
            tvResourceStatement.setTextColor(progressColorID)

            tvDate.text = getFormattedDate(currComponent.date)
        }
    }

    private fun getResourceColor(resource: Int): Int {
        return when(resource) {
            in 70..100 -> R.color.green
            in 35..70 -> R.color.yellow
            else -> R.color.vine
        }
    }

    private fun getLeftResourceValue(component: ComponentItem): Int {
        return component.resourceMileage - (component.startMileage - currMileage)
    }

    private fun getLeftResourcePercent(component: ComponentItem): Int {
        return ((getLeftResourceValue(component).toDouble() * 100) % component.resourceMileage).toInt()
    }
}