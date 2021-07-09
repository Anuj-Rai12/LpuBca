package com.example.working

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.working.databinding.SplashScreenBinding
import com.example.working.loginorsignup.TAG
import com.example.working.repos.Update
import com.example.working.utils.CustomProgress
import com.example.working.utils.MySealed
import com.example.working.utils.UpdateDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val VERSION = 1
const val ADMIN_PHONE = "+917777755555"
@AndroidEntryPoint
class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding

    @Inject
    lateinit var customProgressBar: CustomProgress

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

    private fun hideLoading() = customProgressBar.hideLoading(this)
    companion object {
        var mySharedUrl: String? = null
    }
    private fun showLoading(string: String?)=customProgressBar.showLoading(this, string)

    private fun checkUpdate() {
        myViewModel.getUpdate.observe(this) {
            when (it) {
                is MySealed.Loading -> {
                    showLoading(it.data.toString())
                }
                is MySealed.Success -> {
                    Log.i(TAG, "checkUpdate: Version Success")
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
                        if (FirebaseAuth.getInstance().currentUser?.phoneNumber == ADMIN_PHONE) {
                            mySharedUrl=update.download
                            dir(4)
                        } else {
                            mySharedUrl=update.download
                            dir()
                        }
                }
                is MySealed.Error -> {
                    hideLoading()
                    Log.i(TAG, "checkUpdate: Version Error")
                    val user = FirebaseAuth.getInstance().currentUser == null
                    when {
                        user -> {
                            //User is Not Sign In
                            dir(1)//User is Sign In
                        }
                        FirebaseAuth.getInstance().currentUser?.phoneNumber == ADMIN_PHONE -> dir(4)
                        else -> dir()
                    }
                }
            }
        }
    }

    private fun dialog(message: String, link: String?) {
        updateDialog = UpdateDialog(message, link, "Update!")
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
            4 -> {
                val intent = Intent(this, MainActivity4::class.java)
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