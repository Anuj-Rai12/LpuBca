package com.example.working.loginorsignup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.SignFramgnetBinding

class SignUpScreen:Fragment(R.layout.sign_framgnet) {
    private lateinit var binding: SignFramgnetBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= SignFramgnetBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val action=SignUpScreenDirections.actionSignUpScreenToDateDetailScr()
            findNavController().navigate(action)
        }
        binding.backTo.setOnClickListener {
            val action=SignUpScreenDirections.actionSignUpScreenToLoginScreen()
            findNavController().navigate(action)
        }
    }
}