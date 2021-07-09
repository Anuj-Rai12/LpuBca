package com.example.working.utils

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.working.REQUEST_CAM
import com.example.working.REQUEST_GAL
import com.example.working.databinding.MybottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import javax.inject.Inject


class BottomSheet @Inject constructor() : BottomSheetDialogFragment(),
    EasyPermissions.PermissionCallbacks {
    private lateinit var binding: MybottomsheetBinding
    var sendData: SendData? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MybottomsheetBinding.inflate(inflater)
        binding.opencamera.setOnClickListener {
            if (checkCameraPermission(requireActivity())) {
                sendData?.sendData()
                dismiss()
            } else {
                grantPermission(1)
                Toast.makeText(activity, "Please Grant The Permission", Toast.LENGTH_SHORT).show()
            }
        }
        binding.opengallery.setOnClickListener {
            if (checkGalleryPermission(requireActivity())) {
                sendData?.sendGalImage()
                dismiss()
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
        if (!checkCameraPermission(requireActivity()) && int == 1) {
            request(Manifest.permission.CAMERA, REQUEST_CAM, "Camera")
        }
        if (!checkGalleryPermission(requireActivity()) && int == 2) {
            request(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_GAL, "Gallery")
        }
    }

    private fun request(camera: String, code: Int, s: String) = EasyPermissions.requestPermissions(
        this,
        "Kindly Give us $s permission,otherwise application may not work Properly.",
        code,
        camera
    )

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        perms.forEach {
            if (EasyPermissions.permissionPermanentlyDenied(this, it)) {
                SettingsDialog.Builder(requireContext()).build().show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("MYTAG", "onPermissionsGranted: From Bottom Permission Is Granted")
    }
}