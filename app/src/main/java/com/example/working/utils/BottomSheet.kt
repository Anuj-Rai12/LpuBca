package com.example.working.utils

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.working.REQUEST_CAM
import com.example.working.REQUEST_GAL
import com.example.working.databinding.MybottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog


class BottomSheet: BottomSheetDialogFragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: MybottomsheetBinding
    var sendData: SendData? = null
    private val requestGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it!=null)
            getGalleryImage(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MybottomsheetBinding.inflate(inflater)
        binding.opencamera.setOnClickListener {
            if (checkCameraPermission()) {
                sendData?.sendData()
                dismiss()
            } else {
                grantPermission(1)
                Toast.makeText(activity, "Please Grant The Permission", Toast.LENGTH_SHORT).show()
            }
        }
        binding.opengallery.setOnClickListener {
            if (checkGalleryPermission()) {
                requestGallery.launch("image/*")
            } else {
                grantPermission(2)
                Toast.makeText(activity, "Please Grant The Permission", Toast.LENGTH_SHORT).show()
            }
        }
        binding.randomImage.setOnClickListener {
            sendData?.urlImage()
            dismiss()
        }
        return binding.root
    }

    private fun grantPermission(int: Int) {
        if (!checkCameraPermission() && int == 1) {
            request(Manifest.permission.CAMERA, REQUEST_CAM,"Camera")
        }
        if (!checkGalleryPermission() && int == 2) {
            request(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_GAL,"Gallery")
        }
    }

    private fun request(camera: String, code: Int,s:String) = EasyPermissions.requestPermissions(
        this,
        "Kindly Give us $s permission,otherwise application may not work Properly.",
        code,
        camera
    )

    private fun checkCameraPermission() =
        EasyPermissions.hasPermissions(activity, Manifest.permission.CAMERA)

    private fun checkGalleryPermission() =
        EasyPermissions.hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun getGalleryImage(res: Uri) {
        res.let {
            sendData?.sendGalImage(res)
            dismiss()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        perms.forEach {
            if (EasyPermissions.permissionPermanentlyDenied(this, it)) {
                activity?.let { it1 -> SettingsDialog.Builder(it1).build().show() }
            }/* else {
                grantPermission()
            }*/
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("MYTAG", "onPermissionsGranted: From Bottom Permission Is Granted")
    }
}