package com.demo.carspends.utils.files.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.demo.carspends.R
import com.demo.carspends.domain.picture.InternalPicture
import me.ibrahimyilmaz.kiel.adapterOf
import java.lang.Exception

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
                val bmp: Bitmap = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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
                } catch (e: Exception) {
                    getBitmapFromVectorDrawable(
                        vh.itemView.context,
                        R.drawable.ic_baseline_image_not_supported_24
                    )
                }
                vh.binding.ivPreview.setImageBitmap(bmp)
            }
        )
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
