package com.example.working.loginorsignup.forgertpass

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.R
import com.example.working.databinding.MkeFramgentBinding


class MakeSelection : Fragment(R.layout.mke_framgent) {
    private lateinit var binding: MkeFramgentBinding
    private val args: MakeSelectionArgs by navArgs()

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
            val action = MakeSelectionDirections.actionMakeSelectionToOTPScreen(null, null)
            findNavController().navigate(action)
        }
        binding.emailOption.setOnClickListener {
            Toast.makeText(activity, "Doing", Toast.LENGTH_SHORT).show()
        }
    }
}