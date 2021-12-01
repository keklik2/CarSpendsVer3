package com.demo.carspends.data.car

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarItemDbModel (
        @PrimaryKey(autoGenerate = true)
        val id: Int = UNDEFINED_ID,
        val title: String,
        val mileage: Int,
        val engineVolume: Int,
        val power: Int,
        val avgFuel: Double = DEFAULT_DOUBLE,
        val momentFuel: Double = DEFAULT_DOUBLE,
        val milPrice: Double = DEFAULT_DOUBLE
    ) {
    companion object {
        private const val DEFAULT_DOUBLE = 0.0
        private const val UNDEFINED_ID = 0
    }
}