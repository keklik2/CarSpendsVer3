package com.demo.carspends.presentation.fragments.componentsList.recycleView

import com.demo.carspends.R
import com.demo.carspends.domain.component.ComponentItem

data class ExtendedComponentItem(
    val componentItem: ComponentItem,
    val currMileage: Int
) {
    val resourceColorId: Int
        get() = when (leftResourcePercent) {
            in 70..Int.MAX_VALUE -> R.color.green
            in 35..70 -> R.color.yellow
            else -> R.color.vine
        }

    val leftResourceValue: Int
        get() {
            val res = componentItem.resourceMileage - (currMileage - componentItem.startMileage)
            return when {
                res > componentItem.resourceMileage -> componentItem.resourceMileage
                res > 0 -> res
                else -> 0
            }
        }

    val leftResourcePercent: Int
        get() {
            val res = ((leftResourceValue.toDouble() * 100) / componentItem.resourceMileage).toInt()
            return when {
                res in 0..100 -> res
                res > 100 -> 100
                else -> 0
            }
        }

    companion object {
        const val MIN_PERCENTAGE = 10
    }
}
