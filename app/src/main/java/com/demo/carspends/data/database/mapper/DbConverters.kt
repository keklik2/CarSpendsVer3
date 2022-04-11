package com.demo.carspends.data.database.mapper

import androidx.room.TypeConverter
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.domain.note.NoteType
import javax.inject.Inject

class DbConverters @Inject constructor(){

    @TypeConverter
    fun toNoteType(value: String) = enumValueOf<NoteType>(value)

    @TypeConverter
    fun fromNoteType(value: NoteType) = value.name

    @TypeConverter
    fun toFuelType(value: String) = enumValueOf<Fuel>(value)

    @TypeConverter
    fun fromFuelType(value: Fuel) = value.name
}
