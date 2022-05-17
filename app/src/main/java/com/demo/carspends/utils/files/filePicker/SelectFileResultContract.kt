package com.demo.carspends.utils.files.filePicker

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

class SelectFileResultContract : ActivityResultContract<Unit?, List<Uri>?>() {

    override fun createIntent(context: Context, data: Unit?): Intent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            addCategory(Intent.CATEGORY_OPENABLE)
//            setTypeAndNormalize("image/*")
        }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri>? = when (resultCode) {
        Activity.RESULT_OK -> mutableListOf<Uri>().apply {
//            intent?.data?.let { add(it) }
            intent?.clipData?.let {
                for(i in 0 until it.itemCount) {
                    add(it.getItemAt(i).uri)
                }
            }
        }
        else -> null
    }
}
