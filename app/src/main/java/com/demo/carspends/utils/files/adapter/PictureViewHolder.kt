package com.demo.carspends.utils.files.adapter

import android.view.View
import com.demo.carspends.databinding.ItemPictureBinding
import com.demo.carspends.domain.picture.InternalPicture
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class PictureViewHolder(view: View): RecyclerViewHolder<InternalPicture>(view) {
    val binding = ItemPictureBinding.bind(view)
}
