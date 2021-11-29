package com.demo.carspends.data.component

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "components")
data class ComponentItemDbModel (
    @PrimaryKey
    val id: Int,
    val title: String,
    val startMileage: Int,
    val resourceMileage: Int,
    val date: Long
)