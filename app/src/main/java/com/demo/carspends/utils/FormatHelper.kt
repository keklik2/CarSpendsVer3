package com.demo.carspends.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import com.demo.carspends.R
import com.google.gson.reflect.TypeToken
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

fun getFormattedDayOfWeekRes(receivedDate: Long): Int {
    val calendar = GregorianCalendar.getInstance().apply {
        time = Date(receivedDate)
    }

    return when(calendar.get(GregorianCalendar.DAY_OF_WEEK)) {
        GregorianCalendar.MONDAY -> R.string.monday
        GregorianCalendar.TUESDAY -> R.string.tuesday
        GregorianCalendar.WEDNESDAY -> R.string.wednesday
        GregorianCalendar.THURSDAY -> R.string.thursday
        GregorianCalendar.FRIDAY -> R.string.friday
        GregorianCalendar.SATURDAY -> R.string.saturday
        else -> R.string.sunday
    }
}

fun getFormattedMonthOfYear(receivedDate: Long): String {
    val date = Date(receivedDate)
    val form = SimpleDateFormat("MM.yy", Locale.getDefault())
    return form.format(date)
}

fun getFormattedYear(receivedDate: Long): String {
    val date = Date(receivedDate)
    val form = SimpleDateFormat("yy", Locale.getDefault())
    return form.format(date)
}

fun getFormattedPercentsAsStr(value: Int): String = "$value%"

fun getFormattedPercentsAsStr(value: Double): String = "${value.toInt()}%"

fun Uri.getOriginalFileName(context: Context): String? {
    return try {
        context.contentResolver.query(this, null, null, null, null)?.use {
            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameColumnIndex)
        }
    } catch (t: Throwable) {
        path!!.substringAfterLast('/', "")
    }
}

fun View.setVisibility(isVisible: Boolean) {
    this.visibility =
        if (isVisible) View.VISIBLE
        else View.INVISIBLE
}

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
