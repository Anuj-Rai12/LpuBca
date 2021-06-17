package com.example.working.loginorsignup.forgertpass

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.MkeFramgentBinding
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MakeSelection : Fragment(R.layout.mke_framgent) {
    private lateinit var binding: MkeFramgentBinding
    private val args: MakeSelectionArgs by navArgs()
    private val myViewModel: MyViewModel by activityViewModels()

    @Inject
    lateinit var customProgressBar: CustomProgressBar

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MkeFramgentBinding.bind(view)
        binding.apply {
            emailDetail.text =
                "The Password Reset Link will send to your Registered Email address ${args.email}"
            phoneDetail.text = "The OTP will send to your Registered phone number ${args.phone}"
        }
        binding.phoneOption.setOnClickListener {
            val action = MakeSelectionDirections.actionMakeSelectionToOTPScreen(null, args.phone)
            findNavController().navigate(action)
        }
        binding.emailOption.setOnClickListener {
            sendResetEmail()
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

    private fun sendResetEmail() {
        myViewModel.passwordRestEmail(args.email).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Loading -> {
                    showLoading(it.data)
                }
                is MySealed.Success -> {
                    hideLoading()
                    dialog("Link Sent", "${it.data}")
                }
                is MySealed.Error -> {
                    hideLoading()
                    dialog("Error", "${it.exception?.localizedMessage}")
                }
            }
        }
    }

    private fun dialog(title: String, message: String) {
        val action = MakeSelectionDirections.actionGlobalPasswordDialog(message, title)
        findNavController().navigate(action)
    }
}