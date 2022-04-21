package com.demo.carspends.domain.picture

import android.net.Uri

data class InternalPicture(
    val id: Int = UNDEFINED_ID,
    val name: String,
    val uri: Uri
) {

    companion object {
        private const val UNDEFINED_ID = 0
    }
}
