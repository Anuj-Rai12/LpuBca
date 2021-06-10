package com.example.working.loginorsignup.createacc

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.PhoneNumberBinding


class PhoneScreen:Fragment(R.layout.phone_number) {
    private lateinit var binding: PhoneNumberBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= PhoneNumberBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val action= PhoneScreenDirections.actionPhoneScreenToOTPScreen()
            findNavController().navigate(action)
        }
        binding.backTo.setOnClickListener {
            val action=PhoneScreenDirections.actionPhoneScreenToLoginScreen()
            findNavController().navigate(action)
        }
    }
}