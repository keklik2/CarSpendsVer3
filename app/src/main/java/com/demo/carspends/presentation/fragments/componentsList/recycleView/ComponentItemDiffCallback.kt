package com.demo.carspends.presentation.fragments.componentsList.recycleView

import androidx.recyclerview.widget.DiffUtil
import com.demo.carspends.domain.component.ComponentItem

class ComponentItemDiffCallback: DiffUtil.ItemCallback<ComponentItem>() {
    override fun areItemsTheSame(oldItem: ComponentItem, newItem: ComponentItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ComponentItem, newItem: ComponentItem): Boolean =
        oldItem == newItem
}