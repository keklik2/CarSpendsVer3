package com.demo.carspends.domain.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComponentItem (
    val id: Int = UNDEFINED_ID,
    val title: String,
    val startMileage: Int,
    val resourceMileage: Int,
    val date: Long
    ) : Parcelable {
        companion object {
            const val UNDEFINED_ID = 0
        }
    }