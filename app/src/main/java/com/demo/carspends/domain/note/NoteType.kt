package com.demo.carspends.domain.note

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class NoteType : Parcelable {
    FUEL, REPAIR, EXTRA
}