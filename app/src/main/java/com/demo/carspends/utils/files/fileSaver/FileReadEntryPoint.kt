package com.demo.carspends.utils.files.fileSaver

import android.net.Uri

interface FileReadEntryPoint {
    fun onResult(uri: Uri?)
}
