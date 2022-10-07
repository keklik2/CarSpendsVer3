package com.demo.carspends.presentation.fragments.componentsList.recycleView

import android.view.View
import com.demo.carspends.databinding.ItemComponentBinding
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class ComponentItemViewHolder(view: View) : RecyclerViewHolder<ExtendedComponentItem>(view) {
    val binding = ItemComponentBinding.bind(view)
}
