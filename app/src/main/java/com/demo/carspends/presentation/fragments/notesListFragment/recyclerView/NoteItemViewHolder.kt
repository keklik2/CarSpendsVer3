package com.demo.carspends.presentation.fragments.notesListFragment.recyclerView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.R

class NoteItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    val ivTool = view.findViewById<ImageView>(R.id.ni_iv_tool)
    val tvTitle = view.findViewById<TextView>(R.id.ni_tv_title)
    val tvAmount = view.findViewById<TextView>(R.id.ni_tv_amount)
    val tvExtraInfo = view.findViewById<TextView>(R.id.ni_tv_extra_info)
    val tvDate = view.findViewById<TextView>(R.id.ni_tv_date)
}