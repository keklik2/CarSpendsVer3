package com.demo.carspends.data.database.car

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarItemDbModel (
    @PrimaryKey(autoGenerate = true)
    val id: Int = UNDEFINED_ID,
    val title: String,
    val startMileage: Int,
    val mileage: Int,
    val engineVolume: Double,
    val power: Int,
    val avgFuel: Double = DEFAULT_DOUBLE,
    val momentFuel: Double = DEFAULT_DOUBLE,
    val allFuel: Double = DEFAULT_DOUBLE,
    val fuelPrice: Double = DEFAULT_DOUBLE,
    val milPrice: Double = DEFAULT_DOUBLE,
    val allPrice: Double = DEFAULT_DOUBLE,
    val allMileage: Int = DEFAULT_INT
    ) {
    companion object {
        private const val DEFAULT_DOUBLE = 0.0
        private const val DEFAULT_INT = 0
        private const val UNDEFINED_ID = 0
    }
}
