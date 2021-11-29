package com.demo.carspends.utils

import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDoubleAsStr(sum: Double): String {
    return "%.2f".format(sum)
}

fun getFormattedDate(receivedDate: Long): String {
    val date = Date(receivedDate)
    val form = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
    return form.format(date)
}

fun getFormattedPercentsAsStr(value: Int): String {
    return "$value%"
}

fun getFormattedPercentsAsStr(value: Double): String {
    return "${value.toInt()}%"
}