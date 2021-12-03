package com.demo.carspends.data.mapper

import androidx.room.TypeConverter
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.domain.note.NoteType

class DbConverters {

    @TypeConverter
    fun toNoteType(value: String) = enumValueOf<NoteType>(value)

    @TypeConverter
    fun fromNoteType(value: NoteType) = value.name

    @TypeConverter
    fun toFuelType(value: String) = enumValueOf<Fuel>(value)

    @TypeConverter
    fun fromFuelType(value: Fuel) = value.name
}