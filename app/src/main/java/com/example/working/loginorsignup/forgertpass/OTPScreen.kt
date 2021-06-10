package com.example.working.loginorsignup.forgertpass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.OptFramgnetBinding

class OTPScreen:Fragment(R.layout.opt_framgnet) {
    private lateinit var binding: OptFramgnetBinding
    private lateinit var navController:NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= OptFramgnetBinding.bind(view)
        navController = Navigation.findNavController(view)
        binding.verify.setOnClickListener {
            val action= OTPScreenDirections.actionOTPScreenToMainActivity23()
            findNavController().navigate(action)
        }
    }
}