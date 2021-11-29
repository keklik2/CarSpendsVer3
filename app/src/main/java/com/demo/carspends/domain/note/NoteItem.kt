package com.demo.carspends.domain.note

import com.demo.carspends.domain.others.Fuel

data class NoteItem (
    val id: Int,
    val title: String,
    val totalPrice: Double,
    val price: Double = UNDEFINED_DOUBLE,
    val liters: Double = UNDEFINED_DOUBLE,
    val mileage: Int = UNDEFINED_INT,
    val date: Long,
    val type: NoteType,
    val fuelType: Fuel = UNDEFINED_FUEL_TYPE
) {

    companion object {
        const val UNDEFINED_STRING = "none"
        const val UNDEFINED_INT = -1
        const val UNDEFINED_DOUBLE = -1.0
        val UNDEFINED_FUEL_TYPE = Fuel.DIESEL
    }
}