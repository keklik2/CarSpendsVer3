package com.demo.carspends.utils.files.adapter

import android.view.View
import com.demo.carspends.databinding.PictureItemBinding
import com.demo.carspends.domain.picture.InternalPicture
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

class PictureViewHolder(view: View): RecyclerViewHolder<InternalPicture>(view) {
    val binding = PictureItemBinding.bind(view)
}
