package com.example.working

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.working.databinding.SplashScreenBinding
import com.example.working.repos.Update
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import com.example.working.utils.UpdateDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val VERSION = 1

@AndroidEntryPoint
class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding

    @Inject
    lateinit var customProgressBar: CustomProgressBar

    private lateinit var updateDialog: UpdateDialog

    private val myViewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(3000)
            checkUpdate()
        }
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

    private fun checkUpdate() {
        myViewModel.getUpdate.observe(this) {
            when (it) {
                is MySealed.Loading -> {
                    showLoading(it.data.toString())
                }
                is MySealed.Success -> {
                    hideLoading()
                    val update = it.data as Update
                    val flag = update.version?.toInt() == VERSION
                    val user = FirebaseAuth.getInstance().currentUser == null
                    if (!flag) {
                        dialog(
                            message = "New  Version is available,\nVersion code :${update.version}",
                            link = update.download
                        )
                    } else if (flag && user)//User is Not Sign In
                        dir(1)
                    else //User is Sign In
                        dir()
                }
                is MySealed.Error -> {
                    hideLoading()
                    val user = FirebaseAuth.getInstance().currentUser == null
                    if (user)//User is Not Sign In
                        dir(1)
                    else//User is Sign In
                        dir()
                }
            }
        }
    }

    private fun dialog(message: String, link: String?) {
        updateDialog = UpdateDialog(message, link)
        updateDialog.show(supportFragmentManager, "UpdateTag")
        updateDialog.isCancelable = false
    }

    private fun dir(choice: Int = 0) {
        when (choice) {
            1 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}