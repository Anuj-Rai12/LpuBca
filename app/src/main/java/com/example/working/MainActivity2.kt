package com.example.working

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.working.databinding.ActivityMain2Binding
import com.example.working.utils.*
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
    private val shareLink by lazy {
        intent.getStringExtra(SHARE_Key)
    }

    @Inject
    lateinit var customProgress: CustomProgress
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
            val updateDialog = UpdateDialog("Do You Really Want to LogOut?", null, "LogOut!")
            updateDialog.isCancelable = false
            updateDialog.show(supportFragmentManager, "LogOUt")
        }
        share.setOnClickListener {
            shareLink?.let { url ->
                shareText(
                    SHARED = SHARED_APP,
                    context = applicationContext,
                    downloadUri = url,
                    sharedBy = userName.text.toString(),
                    title = "Share App!"
                )
            }
        }
        setUpProfile()
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, binding?.drawLayout)
        binding?.nagView!!.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
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
}