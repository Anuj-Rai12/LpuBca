package com.example.working

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.example.working.databinding.ActivityMain4Binding
import com.example.working.utils.Convertor
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import com.example.working.utils.UpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity4 : AppCompatActivity() {
    private lateinit var binding: ActivityMain4Binding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var img: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var logout: TextView
    private lateinit var share: TextView
    private val myViewModel: MyViewModel by viewModels()

    @Inject
    lateinit var customProgressBar: CustomProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        img = binding.nagViewAdmin.getHeaderView(0).findViewById(R.id.userProfileImage)!!
        userName = binding.nagViewAdmin.getHeaderView(0).findViewById(R.id.userNametext)!!
        userEmail =
            binding.nagViewAdmin.getHeaderView(0).findViewById(R.id.userEmailtext)!!
        logout =
            binding.nagViewAdmin.getHeaderView(0).findViewById(R.id.mylogoutText)!!
        share = binding.nagViewAdmin.getHeaderView(0).findViewById(R.id.myshareText)!!
        logout.setOnClickListener {
            val updateDialog = UpdateDialog("Do You Really Want to LogOut?", null, "LogOut!")
            updateDialog.isCancelable=false
            updateDialog.show(supportFragmentManager, "LogOUt")
        }
        setUpProfile()
        share.setOnClickListener {
            Toast.makeText(this, "Doing", Toast.LENGTH_SHORT).show()
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.adminDrawn)
        binding.nagViewAdmin.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun hideLoading() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        customProgressBar.dismiss()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun showLoading(string: String?, boolean: Boolean = false) {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        customProgressBar.show(this, string, boolean)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpProfile() {
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
}