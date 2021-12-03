package com.demo.carspends.presentation.fragments.componentsListFragment.recycleView

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.R

class ComponentItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    val pbLeftMileage = view.findViewById<ProgressBar>(R.id.ci_pb_left_mileage)
    val tvLeftMileage = view.findViewById<TextView>(R.id.ci_tv_left_mileage)
    val tvTitle = view.findViewById<TextView>(R.id.ci_tv_title)
    val tvResourceStatement = view.findViewById<TextView>(R.id.ci_tv_resource_statement)
    val tvDate = view.findViewById<TextView>(R.id.ci_tv_date)
}