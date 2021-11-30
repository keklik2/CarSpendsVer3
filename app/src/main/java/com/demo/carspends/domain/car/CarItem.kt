package com.demo.carspends.domain.car

data class CarItem (
        val id: Int,
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
    }
}