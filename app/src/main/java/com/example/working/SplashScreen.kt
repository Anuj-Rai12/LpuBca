package com.example.working

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

const val VERSION = 1

@AndroidEntryPoint
class SplashScreen : Fragment(R.layout.splash_screen) {
    private lateinit var binding: SplashScreenBinding

    @Inject
    lateinit var customProgressBar: CustomProgressBar

    @Inject
    lateinit var updateDialog: UpdateDialog
    private val myViewModel: MyViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashScreenBinding.bind(view)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        lifecycleScope.launch {
            delay(3000)
            checkUpdate()
        }
    }

    private fun hideLoading() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        customProgressBar.dismiss()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun showLoading(string: String?, boolean: Boolean = false) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        customProgressBar.show(requireActivity(), string, boolean)
    }

    private fun checkUpdate() {
        myViewModel.getUpdate().observe(viewLifecycleOwner) {
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
        myViewModel.downloadLink = link
        myViewModel.msg = message
        updateDialog.show(parentFragmentManager, "UpdateTag")
        updateDialog.isCancelable = false
    }

    private fun dir(choice: Int = 0) {
        when (choice) {
            1 -> {
                val action = SplashScreenDirections.actionGlobalLoginScreen2()
                findNavController().navigate(action)
            }
            else -> {
                val action = SplashScreenDirections.actionSplashScreenToNavigation2()
                findNavController().navigate(action)
            }
        }
    }
}