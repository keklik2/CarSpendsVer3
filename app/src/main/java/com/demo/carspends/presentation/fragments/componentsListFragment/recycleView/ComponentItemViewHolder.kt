package com.demo.carspends.presentation.fragments.componentsListFragment.recycleView

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.R
import com.demo.carspends.databinding.ComponentItemBinding

class ComponentItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val binding = ComponentItemBinding.bind(view)
}