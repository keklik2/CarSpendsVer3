package com.demo.carspends.utils.files.fileSaver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class ReadFileResultContract: ActivityResultContract<Unit?, Uri?>() {

    override fun createIntent(context: Context, data: Unit?): Intent =
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setTypeAndNormalize("text/plain")
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when (resultCode) {
        Activity.RESULT_OK -> intent?.data
        else -> null
    }
}
