package com.example.working

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.working.databinding.ActivityMainBinding
import com.example.working.utils.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val REQUEST_CAM = 101
const val REQUEST_GAL = 102
const val REQUEST_WRIT = 103

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var checkInternet: CheckInternet
    @Inject
    lateinit var customProgressBar: CustomProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkInternet= CheckInternet(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        grantPermission()
        checkInternet()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
    }
    private fun checkInternet() {
        checkInternet.observe(this){
            if (!it){
                dialog()
                Toast.makeText(this, No_Internet_MSG, Toast.LENGTH_LONG).show()
            }
            else{
                dialog(flag = true)
                Toast.makeText(this, "Connection Established", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dialog(flag:Boolean=false) {
        val msg = PasswordDialog(title = No_Internet, Msg = No_Internet_MSG)
        if (!flag) {
            msg.isCancelable = flag
            msg.show(supportFragmentManager, "No_Internet")
        } else if (msg.isVisible) {
            msg.dismiss()
        }
    }
    private fun grantPermission() {
        if (!checkCameraPermission(this)) {
            request(Manifest.permission.CAMERA, REQUEST_CAM, "Camera")
        }
        if (!checkGalleryPermission(this)) {
            request(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_GAL, "Gallery")
        }
        if (!checkWritePermission(this)) {
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
    override fun onPause() {
        super.onPause()
        customProgressBar.show(this,null)
        customProgressBar.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        customProgressBar.show(this,null)
        customProgressBar.dismiss()
    }
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i("MYTAG", "onPermissionsGranted: Permission Is Granted")
    }

}