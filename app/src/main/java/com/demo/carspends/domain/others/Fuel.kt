package com.demo.carspends.domain.others

import android.content.Context
import android.os.Parcelable
import com.demo.carspends.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Fuel(val layoutRes: Int) : Parcelable {
    GAS(R.string.GAS),
    ELECTRICITY(R.string.Electricity),
    DIESEL(R.string.DT),
    F100(R.string.F100),
    F98(R.string.F98),
    F95(R.string.F95),
    F92(R.string.F92);

    fun toString(context: Context): String = context.getString(layoutRes)
}
