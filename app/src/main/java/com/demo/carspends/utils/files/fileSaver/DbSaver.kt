package com.demo.carspends.utils.files.fileSaver

import android.Manifest
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.demo.carspends.R
import com.demo.carspends.utils.DOWNLOAD_CHANNEL_ID
import com.demo.carspends.utils.DOWNLOAD_NOTIFICATION_ID
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.aartikov.sesame.property.PropertyHost
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import javax.inject.Inject

class DbSaver<T> @Inject constructor(
    private val fragment: Fragment,
    private val type: Type,
    private val afterLoad: (T) -> Unit
) : FileReadEntryPoint, PropertyHost {
    private var json: String? = null
    private var toConvert: T? = null

    fun save(toConvert: T) {
        this.toConvert = toConvert
        if (isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) innerSave()
        else writeStoragePermission.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
    }

    private fun innerSave() {
        toConvert?.let {
            makeAlert(START_DOWNLOAD)
            json = Gson().toJson(this.toConvert!!, type)
            saveToDownloads(NOTES)
        }
    }

    fun load() {
        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            filePicker.launch(null)
        } else readStoragePermission.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    private fun saveToDownloads(fileName: String) {
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            val stream = FileOutputStream(file)
            stream.write(json?.toByteArray())
            stream.close()
            makeAlert(ENDED_DOWNLOAD)
        } catch (e: Exception) {
            makeAlert(ERROR)
            makeToast("${fragment.getString(TOAST_ERROR_SAVE)}: $e")
        }
    }

    private fun readTextFile(uri: Uri): String? {
        var reader: BufferedReader? = null
        val builder = StringBuilder()
        try {
            reader = BufferedReader(
                InputStreamReader(
                    fragment.requireActivity().contentResolver.openInputStream(uri)
                )
            )
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (e: Exception) {
            makeToast(TOAST_ERROR_LOAD)
        } finally {
            reader?.let {
                try { it.close() }
                catch (e: Exception) { }
            }
        }
        return if (builder.toString().isBlank() || builder.toString().isEmpty()) null
        else builder.toString()
    }

    private val filePicker =
        fragment.registerForActivityResult(ReadFileResultContract()) {
            onResult(it)
        }

    private val readStoragePermission =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            val anyNotGranted = granted.values.contains(false)
            if (!anyNotGranted) filePicker.launch(null)
            else {
                AlertDialog.Builder(fragment.requireActivity())
                    .setTitle(R.string.dialog_permission_title)
                    .setMessage(fragment.getString(R.string.dialog_permission_denied))
                    .setNeutralButton(R.string.button_apply) { _, _ -> }
                    .show()
            }
        }

    private val writeStoragePermission =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            val anyNotGranted = granted.values.contains(false)
            if (!anyNotGranted) innerSave()
            else
                AlertDialog.Builder(fragment.requireActivity())
                    .setTitle(R.string.dialog_permission_title)
                    .setMessage(fragment.getString(R.string.dialog_permission_denied))
                    .setNeutralButton(R.string.button_apply) { _, _ -> }
                    .show()
        }

    override fun onResult(uri: Uri?) {
        uri?.let { itUri ->
            json = readTextFile(itUri)
            json?.let { itJson ->
                makeToast(TOAST_COMPLETED_LOAD)
                afterLoad(Gson().fromJson(itJson, type))
            }
        }
    }

    private fun makeAlert(condition: Int) {
        val builder = when (condition) {
            START_DOWNLOAD -> {
                NotificationCompat.Builder(fragment.requireContext(), DOWNLOAD_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(fragment.getString(NOTIFICATION_DOWNLOAD_TITLE))
                    .setContentText(fragment.getString(NOTIFICATION_DOWNLOAD_PROCESSING))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build()
            }
            ENDED_DOWNLOAD -> {
                NotificationCompat.Builder(fragment.requireContext(), DOWNLOAD_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(fragment.getString(NOTIFICATION_DOWNLOAD_TITLE))
                    .setContentText(fragment.getString(NOTIFICATION_DOWNLOAD_SUCCESS))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            fragment.requireContext(),
                            0,
                            Intent(DownloadManager.ACTION_VIEW_DOWNLOADS),
                            0
                        )
                    )
                    .build()
            }
            else -> {
                NotificationCompat.Builder(fragment.requireContext(), DOWNLOAD_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(fragment.getString(NOTIFICATION_DOWNLOAD_TITLE))
                    .setContentText(fragment.getString(NOTIFICATION_DOWNLOAD_ERROR))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build()
            }
        }


        NotificationManagerCompat.from(fragment.requireContext())
            .notify(DOWNLOAD_NOTIFICATION_ID, builder)
    }

    private fun makeToast(alert: Int) {
        Toast.makeText(fragment.requireActivity(), alert, Toast.LENGTH_LONG).show()
    }

    private fun makeToast(alert: String) {
        Toast.makeText(fragment.requireActivity(), alert, Toast.LENGTH_LONG).show()
    }

    private fun isPermissionGranted(permission: String): Boolean {
        fragment.context?.let {
            return PermissionChecker.checkSelfPermission(
                it,
                permission
            ) == PermissionChecker.PERMISSION_GRANTED
        }
        return false
    }

    companion object {
        private const val START_DOWNLOAD = 0
        private const val ENDED_DOWNLOAD = 1
        private const val ERROR = 2

        private const val TOAST_ERROR_LOAD = R.string.toast_db_saver_load_error
        private const val TOAST_ERROR_SAVE = R.string.toast_db_saver_save_error
        private const val TOAST_COMPLETED_LOAD = R.string.toast_db_saver_load_completed

        private const val NOTIFICATION_DOWNLOAD_TITLE = R.string.notification_download_title
        private const val NOTIFICATION_DOWNLOAD_SUCCESS = R.string.notification_download_success
        private const val NOTIFICATION_DOWNLOAD_PROCESSING =
            R.string.notification_download_in_process
        private const val NOTIFICATION_DOWNLOAD_ERROR = R.string.notification_download_error

        const val NOTES = "car_spends.txt"
    }

    override val propertyHostScope = CoroutineScope(Dispatchers.IO)
}
