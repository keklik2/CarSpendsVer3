package com.demo.carspends.utils.ui.baseFragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker
import com.demo.carspends.R
import com.demo.carspends.domain.picture.InternalPicture
import com.demo.carspends.utils.ui.baseViewModel.NoteAddOrEditViewModel
import com.demo.carspends.utils.files.adapter.PictureAdapter
import com.demo.carspends.utils.files.filePicker.FileSelectionEntryPoint
import com.demo.carspends.utils.files.filePicker.SelectFileResultContract
import com.demo.carspends.utils.getOriginalFileName

abstract class NoteAddOrEditFragment(layout: Int) :
    BaseFragment(layout),
    FileSelectionEntryPoint {

    abstract override val vm: NoteAddOrEditViewModel
    abstract fun setupPicturesRecyclerViewBind()

    val pictureAdapter by lazy {
        PictureAdapter.get(
            {
                if (isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    activity?.startActivity(intentView(it.uri))
                } else readStoragePermission.launch(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            },
            {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(
                        String.format(
                            getString(R.string.dialog_delete_picture),
                            it.name
                        )
                    )
                    .setPositiveButton(R.string.button_apply) { _, _ ->
                        vm.deletePicture(it)
                    }
                    .setNegativeButton(R.string.button_deny) { _, _ -> }
                    .show()
            }
        )
    }

    fun openPicturesPicker() {
        if (vm.pictures.size < 4) {
            if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                picturesPicker.launch(null)
            } else readStoragePermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.toast_pictures_max_size),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val picturesPicker =
        registerForActivityResult(SelectFileResultContract()) {
            onFileSelected(it)
        }

    private val readStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            val anyNotGranted = granted.values.contains(false)
            if (!anyNotGranted) picturesPicker.launch(null)
            else {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.dialog_permission_title)
                    .setMessage(getString(R.string.dialog_permission_denied))
                    .setNeutralButton(R.string.button_apply) { _, _ -> }
                    .show()
            }
        }


    override fun onFileSelected(uriList: List<Uri>?) {
        if (!uriList.isNullOrEmpty()) {
            val newPictures = uriList.map { itUri ->
                InternalPicture(
                    name = itUri.getOriginalFileName(requireContext())
                        ?: getString(UNNAMED_FILE),
                    uri = itUri
                )
            }
            vm.addPictures(newPictures)
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        context?.let {
            return PermissionChecker.checkSelfPermission(
                it,
                permission
            ) == PermissionChecker.PERMISSION_GRANTED
        }
        return false
    }

    private fun intentView(data: Uri): Intent =
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(data, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    companion object {
        private const val UNNAMED_FILE = R.string.text_unnamed
    }
}
