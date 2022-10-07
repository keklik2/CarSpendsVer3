package com.demo.carspends.utils.dialogs

import android.app.DatePickerDialog
import java.util.*

data class AppDatePickerContainer(
    private val onDateSetCallback: (date: Long) -> Unit,
    private val startDate: Long? = null
) {
    val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val cal = GregorianCalendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)

        onDateSetCallback(cal.timeInMillis)
    }
    val calendar: Calendar = if (startDate != null) GregorianCalendar.getInstance().apply {
        timeInMillis = startDate
    }
        else GregorianCalendar.getInstance()
}
