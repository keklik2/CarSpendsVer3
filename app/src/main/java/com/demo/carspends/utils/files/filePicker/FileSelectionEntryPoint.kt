package com.demo.carspends.utils.files.filePicker

import android.net.Uri

interface FileSelectionEntryPoint {
    fun onFileSelected(uriList: List<Uri>?)
}
