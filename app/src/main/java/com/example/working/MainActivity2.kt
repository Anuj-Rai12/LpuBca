package com.example.working

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.ActivityMain2Binding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private var binding: ActivityMain2Binding? = null
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var checkInternet: CheckInternet
    private lateinit var img: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var logout: TextView
    private lateinit var share: TextView
    private lateinit var bugs: TextView
    private var adminEmail: String? = null
    private var updateDialog: UpdateDialog? = null
    private var passwordDialog: PasswordDialog? = null
    private val message: String = "Do You Really Want to LogOut?"
    private var dialogTitle: String = "LogOut!"
    private val myViewModel: MyViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()
    private val shareLink by lazy {
        MainActivity3.mySharedUrl
    }

    @Inject
    lateinit var customProgress: CustomProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        savedInstanceState?.let { bundle ->
            adminEmail = bundle.getString(MAIL)
        }
        if (myViewModel.dialogFlag) {
            openDialog()
            myViewModel.dialogFlag = false
            passwordDialog=null
        }
        grantPermission()
        getAdminEmail()
        checkInternet = CheckInternet(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        img = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.userProfileImage)!!
        bugs = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.myBugReport)!!
        userName = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.userNametext)!!
        userEmail =
            binding?.nagView?.getHeaderView(0)?.findViewById(R.id.userEmailtext)!!
        logout =
            binding?.nagView?.getHeaderView(0)?.findViewById(R.id.mylogoutText)!!
        share = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.myshareText)!!
        logout.setOnClickListener {
            openDialog()
        }
        share.setOnClickListener {
            shareLink?.let { url ->
                shareText(
                    SHARED = SHARED_APP,
                    context = this,
                    downloadUri = url,
                    sharedBy = userName.text.toString(),
                    title = "Share App!"
                )
            }
        }
        bugs.setOnClickListener {
            adminEmail?.let {
                val intend = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$it"))
                startActivity(intend)
                return@setOnClickListener
            }
            Toast.makeText(this, "Can't Send Mail", Toast.LENGTH_SHORT).show()
        }
        setUpProfile()
        checkInternet()
        subscribe()
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding?.drawLayout)
        binding?.nagView!!.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun subscribe() = myViewModel.subscriberToNotification.observe(this){
            checkSubscriptionStatus(it)
        }

    override fun onPause() {
        super.onPause()
        hideLoading()
        updateDialog?.dismiss()
        passwordDialog?.dismiss()
    }

    private fun openDialog() {
        updateDialog = UpdateDialog(message, null, dialogTitle)
        myViewModel.dialogFlag = true
        updateDialog?.isCancelable = false
        updateDialog?.show(supportFragmentManager, AllMyConstant.MSG_DIALOG)
    }

    private fun checkInternet() {
        checkInternet.observe(this) {
            if (it) {
                passwordDialog?.dismiss()
            } else {
                dialog()
            }
        }
    }

    private fun dialog() {
        passwordDialog = PasswordDialog(title = No_Internet, Msg = No_Internet_MSG)
        passwordDialog?.show(supportFragmentManager, "No_Internet")
    }

    private fun getAdminEmail() {
        adminViewModel.getAdminEmailId.observe(this) {
            when (it) {
                is MySealed.Error -> Log.i(TAG, "getAdminEmail: ${it.exception?.localizedMessage}")
                is MySealed.Loading -> Log.i(TAG, "getAdminEmail: Getting Admin Email")
                is MySealed.Success -> adminEmail = it.data
            }
        }
    }

    private fun hideLoading() {
        customProgress.hideLoading(this)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun showLoading(string: String?, boolean: Boolean = false) {
        customProgress.showLoading(this, string, boolean)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpProfile() {
        Log.i(TAG, "setUpProfile: Profile Is Updated ")
        myViewModel.userData.observe(this) {
            when (it) {
                is MySealed.Loading -> {
                    showLoading("User Profile is Loading")
                }
                is MySealed.Success -> {
                    hideLoading()
                    val user = it.data
                    val theByteArray = Convertor.covertByteArray2image(user?.icon?.toBytes()!!)
                    img.setImageBitmap(theByteArray)
                    userEmail.text = user.email
                    userName.text = "${user.firstname} ${user.lastname}"
                }
                is MySealed.Error -> {
                    hideLoading()
                    val action = MainActivity2Directions.actionGlobalPasswordDialog(
                        "${it.exception?.localizedMessage}",
                        "Error"
                    )
                    navController.navigate(action)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
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

    private fun request(camera: String, code: Int, s: String) = EasyPermissions.requestPermissions(
        this,
        "Kindly Give us $s permission,otherwise application may not work Properly.",
        code,
        camera
    )

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adminEmail?.let {
            outState.getString(MAIL, it)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i(TAG, "onPermissionsGranted: Permstion granted ->$perms")
    }
}

private const val MAIL = "MY_MAIL"