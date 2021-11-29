package com.demo.carspends.data

import androidx.room.TypeConverter
import com.demo.carspends.domain.note.NoteType

class DbConverters {

    @TypeConverter
    fun toNoteType(value: String) = enumValueOf<NoteType>(value)

    @TypeConverter
    fun fromNoteType(value: NoteType) = value.name
}