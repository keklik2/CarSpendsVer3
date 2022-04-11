package com.demo.carspends.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

val doubleDf = DecimalFormat().apply {
    maximumFractionDigits = 1
    isGroupingUsed = true
    groupingSize = 3

    decimalFormatSymbols = decimalFormatSymbols.apply {
        decimalSeparator = '.'
        groupingSeparator = ' '
    }
}

val intDf = DecimalFormat().apply {
    isGroupingUsed = true
    groupingSize = 3

    decimalFormatSymbols = decimalFormatSymbols.apply {
        groupingSeparator = ' '
    }
}

fun getFormattedDoubleAsStrForDisplay(value: Double): String = doubleDf.format(value)

fun getFormattedIntAsStrForDisplay(value: Int): String = intDf.format(value)

fun getFormattedDoubleAsStr(value: Double): String = "%.1f".format(Locale.US, value)

fun getFormattedDate(receivedDate: Long): String {
    val date = Date(receivedDate)
    val form = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return form.format(date)
}

fun getFormattedPercentsAsStr(value: Int): String = "$value%"

fun getFormattedPercentsAsStr(value: Double): String = "${value.toInt()}%"
