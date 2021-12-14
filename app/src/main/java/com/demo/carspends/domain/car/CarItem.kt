package com.demo.carspends.domain.car

data class CarItem (
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
        val allMileage: Double = DEFAULT_DOUBLE
    ) {
    companion object {
        const val UNDEFINED_ID = 0
        private const val DEFAULT_DOUBLE = 0.0
    }
}