package com.demo.carspends.data.database.component

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "components")
data class ComponentItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int = UNDEFINED_ID,
    val title: String,
    val startMileage: Int,
    val resourceMileage: Int,
    val date: Long
) {
    companion object {
        private const val UNDEFINED_ID = 0
    }
}
