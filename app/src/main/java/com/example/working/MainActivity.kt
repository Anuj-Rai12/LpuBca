package com.example.working

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.working.databinding.ActivityMainBinding
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint

const val REQUEST_CAM = 101
const val REQUEST_GAL = 102
const val REQUEST_WRIT = 103

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        grantPermission()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
    }

    private fun checkCameraPermission() =
        EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)

    private fun checkGalleryPermission() =
        EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun checkWritePermission() =
        EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private fun grantPermission() {
        if (!checkCameraPermission()) {
            request(Manifest.permission.CAMERA, REQUEST_CAM, "Camera")
        }
        if (!checkGalleryPermission()) {
            request(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_GAL, "Gallery")
        }
        if (!checkWritePermission()) {
            request(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRIT, "Access to Storage")
        }
    }

    private fun request(camera: String, code: Int, s: String) = EasyPermissions.requestPermissions(
        this,
        "Kindly Give us $s permission,otherwise application may not work Properly.",
        code,
        camera
    )

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        perms.forEach {
            if (EasyPermissions.permissionPermanentlyDenied(this, it)) {
                SettingsDialog.Builder(this).build().show()
            } else {
                grantPermission()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("MYTAG", "onPermissionsGranted: Permission Is Granted")
    }

}