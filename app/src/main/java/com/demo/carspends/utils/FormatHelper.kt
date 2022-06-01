package com.demo.carspends.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import com.demo.carspends.R
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Numbers formatting
 */
val doubleFormat = DecimalFormat().apply {
    maximumFractionDigits = 1
    isGroupingUsed = true
    groupingSize = 3
    decimalFormatSymbols = decimalFormatSymbols.apply {
        decimalSeparator = '.'
        groupingSeparator = ' '
    }
}
val intFormat = DecimalFormat().apply {
    isGroupingUsed = true
    groupingSize = 3
    decimalFormatSymbols = decimalFormatSymbols.apply { groupingSeparator = ' ' }
}

fun getFormattedDoubleAsStrForDisplay(value: Double): String = doubleFormat.format(value)

fun getFormattedIntAsStrForDisplay(value: Int): String = intFormat.format(value)

fun getFormattedDoubleAsStr(value: Double): String = "%.1f".format(Locale.US, value)


/**
 * Date formatting
 */
fun getFormattedDate(date: Long): String {
    val form = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return form.format(Date(date))
}

fun getFormattedDayOfWeekRes(date: Long): Int {
    val calendar = GregorianCalendar.getInstance().apply { timeInMillis = date }

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

fun getWeekFromDate(date: Long): Int {
    return GregorianCalendar.getInstance().apply {
        timeInMillis = date
    }.get(GregorianCalendar.WEEK_OF_YEAR)
}

fun getFormattedWeekFromDate(date: Long): String {
    val calendar = GregorianCalendar.getInstance().apply { timeInMillis = date }

    val startDay = calendar.apply {
        set(GregorianCalendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    }.timeInMillis

    val endDay = calendar.apply {
        add(GregorianCalendar.DAY_OF_WEEK, 6)
    }.timeInMillis

//    val(startDay, endDay) = if (calendar.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY) {
//        listOf(
//            calendar.apply {
//                set(GregorianCalendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
//            }.timeInMillis,
//            calendar.apply {
//                add(GregorianCalendar.DAY_OF_WEEK, 6)
//            }.timeInMillis
//        )
//    } else {
//        listOf(
//            calendar.apply {
//                add(GregorianCalendar.DAY_OF_WEEK, -7)
//                set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY)
//            }.timeInMillis,
//            calendar.apply {
//                add(GregorianCalendar.DAY_OF_WEEK, 7)
//                set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SUNDAY)
//            }.timeInMillis
//        )
//    }

    val form = SimpleDateFormat("dd.MM", Locale.getDefault())

    return "${form.format(Date(startDay))} - ${form.format(Date(endDay))}"
}

fun getFormattedMonthOfYear(date: Long): String {
    val form = SimpleDateFormat("MM.yy", Locale.getDefault())
    return form.format(Date(date))
}

fun getFormattedYear(date: Long): String {
    val form = SimpleDateFormat("yy", Locale.getDefault())
    return form.format(Date(date))
}


/**
 * Percents formatting
 */
fun getFormattedPercentAsStr(value: Int): String = "$value%"

fun getFormattedPercentAsStr(value: Double): String = "${value.toInt()}%"


/**
 * Classes external functions
 */
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
