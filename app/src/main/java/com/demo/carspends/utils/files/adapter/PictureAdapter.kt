package com.demo.carspends.utils.files.adapter

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import com.demo.carspends.R
import com.demo.carspends.domain.picture.InternalPicture
import me.ibrahimyilmaz.kiel.adapterOf

object PictureAdapter {
    fun get(
        onItemClickFunc: ((InternalPicture) -> Unit)? = null,
        onItemDeleteFunc: ((InternalPicture) -> Unit)? = null
    ) = adapterOf<InternalPicture> {
        register(
            layoutResource = R.layout.picture_item,
            viewHolder = ::PictureViewHolder,
            onBindViewHolder = { vh, _, item ->
                vh.binding.cvPreview.setOnClickListener {
                    onItemClickFunc?.invoke(item)
                }
                vh.binding.ibDelete.setOnClickListener {
                    onItemDeleteFunc?.invoke(item)
                }
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            vh.itemView.context.contentResolver,
                            item.uri
                        )
                    )
                }
                else {
                    MediaStore.Images.Media.getBitmap(
                        vh.itemView.context.contentResolver,
                        item.uri
                    )
                }
                vh.binding.ivPreview.setImageBitmap(bmp)
            }
        )
    }
}
