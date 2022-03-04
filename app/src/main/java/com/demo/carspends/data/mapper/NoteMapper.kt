package com.demo.carspends.data.mapper

import com.demo.carspends.data.note.NoteItemDbModel
import com.demo.carspends.domain.note.NoteItem
import javax.inject.Inject

class NoteMapper @Inject constructor(){

    fun mapNoteDbModelToEntity(noteItemDbModel: NoteItemDbModel): NoteItem = NoteItem(
        noteItemDbModel.id,
        noteItemDbModel.title,
        noteItemDbModel.totalPrice,
        noteItemDbModel.price,
        noteItemDbModel.liters,
        noteItemDbModel.mileage,
        noteItemDbModel.date,
        noteItemDbModel.type,
        noteItemDbModel.fuelType
    )

    fun mapEntityToNoteDbModel(entity: NoteItem): NoteItemDbModel {
        return NoteItemDbModel(
            entity.id,
            entity.title,
            entity.totalPrice,
            entity.price,
            entity.liters,
            entity.mileage,
            entity.date,
            entity.type,
            entity.fuelType
        )
    }
}