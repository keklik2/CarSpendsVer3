package com.demo.carspends.data.database.note

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.domain.note.NoteType

@Entity(tableName = "notes_table")
data class NoteItemDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = UNDEFINED_ID,
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
        const val UNDEFINED_ID = 0
        const val UNDEFINED_INT = -1
        const val UNDEFINED_DOUBLE = -1.0
        val UNDEFINED_FUEL_TYPE = Fuel.DIESEL
    }
}
