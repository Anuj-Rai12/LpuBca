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
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.working.databinding.ActivityMain2Binding
import com.example.working.utils.Convertor
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import com.example.working.utils.userchannel.FireBaseUser
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {
    private var binding: ActivityMain2Binding? = null
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
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        img = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.userProfileImage)!!
        userName = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.userNametext)!!
        userEmail =
            binding?.nagView?.getHeaderView(0)?.findViewById(R.id.userEmailtext)!!
        logout =
            binding?.nagView?.getHeaderView(0)?.findViewById(R.id.mylogoutText)!!
        share = binding?.nagView?.getHeaderView(0)?.findViewById(R.id.myshareText)!!
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }
        share.setOnClickListener {
            Toast.makeText(this, "Doing", Toast.LENGTH_SHORT).show()
        }
        setUpProfile()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.noteFragment,
                R.id.booksFragment,
                R.id.profileFragment,
                R.id.downloadFragment,
            ), binding?.drawLayout
        )

        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding?.mybtnNag?.setupWithNavController(navController)
        binding?.nagView?.setupWithNavController(navController)
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
                    val user = it.data as FireBaseUser
                    val theByteArray = Convertor.covertByteArray2image(user.icon?.toBytes()!!)
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
}