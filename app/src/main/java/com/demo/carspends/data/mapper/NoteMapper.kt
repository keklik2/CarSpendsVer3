package com.demo.carspends.data.mapper

import com.demo.carspends.data.note.NoteItemDbModel
import com.demo.carspends.domain.note.NoteItem

class NoteMapper {

    fun mapNoteDbModelToEntity(noteItemDbModel: NoteItemDbModel): NoteItem = NoteItem(
        noteItemDbModel.id,
        noteItemDbModel.title,
        noteItemDbModel.totalPrice,
        noteItemDbModel.price,
        noteItemDbModel.liters,
        noteItemDbModel.mileage,
        noteItemDbModel.date,
        noteItemDbModel.type
    )

    fun mapEntityToNoteDbModel(entity: NoteItem): NoteItemDbModel = NoteItemDbModel(
        entity.id,
        entity.title,
        entity.totalPrice,
        entity.price,
        entity.liters,
        entity.mileage,
        entity.date,
        entity.type
    )
}