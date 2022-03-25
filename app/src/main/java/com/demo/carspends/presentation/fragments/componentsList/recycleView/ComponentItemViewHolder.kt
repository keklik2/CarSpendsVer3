package com.demo.carspends.presentation.fragments.componentsList.recycleView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.demo.carspends.databinding.ComponentItemBinding

class ComponentItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val binding = ComponentItemBinding.bind(view)
}