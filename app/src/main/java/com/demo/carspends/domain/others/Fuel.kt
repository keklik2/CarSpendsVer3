package com.demo.carspends.domain.others

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Fuel(val strName: String) : Parcelable {
    GAS("Газ"),
    ELECTRICITY("Электричество"),
    DIESEL("Дизель"),
    F100("АИ-100"),
    F98("АИ-98"),
    F95("АИ-95"),
    F92("АИ-92");

    override fun toString(): String {
        return strName
    }}