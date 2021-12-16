package com.demo.carspends.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun getFormattedDoubleAsStrForDisplay(value: Double): String {
    val df = DecimalFormat()
    with(df) {
        isGroupingUsed = true
        groupingSize = 3

        val dfs = decimalFormatSymbols
        dfs.decimalSeparator = '.'
        dfs.groupingSeparator = ' '
        decimalFormatSymbols = dfs

        return format(value)
    }
}

fun getFormattedIntAsStrForDisplay(value: Int): String {
    val df = DecimalFormat()
    with(df) {
        isGroupingUsed = true
        groupingSize = 3

        val dfs = decimalFormatSymbols
        dfs.groupingSeparator = ' '
        decimalFormatSymbols = dfs

        return format(value)
    }
}

fun getFormattedDoubleAsStr(value: Double): String {
    return "%.2f".format(Locale.US, value)
}

fun getFormattedDate(receivedDate: Long): String {
    val date = Date(receivedDate)
    val form = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return form.format(date)
}

fun getFormattedPercentsAsStr(value: Int): String {
    return "$value%"
}

fun getFormattedPercentsAsStr(value: Double): String {
    return "${value.toInt()}%"
}