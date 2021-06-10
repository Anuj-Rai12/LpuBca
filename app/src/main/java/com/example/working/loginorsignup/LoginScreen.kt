package com.example.working.loginorsignup

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.R
import com.example.working.databinding.LoginFragmentBinding

class LoginScreen : Fragment(R.layout.login_fragment) {
    private lateinit var binding: LoginFragmentBinding
    private val args: LoginScreenArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            /*binding.emailTextView.boxStrokeColor = (resources.getColor(R.color.red_color, null))
            binding.emailTextView.hint = "hello is Is Wrong"*/
            val action=LoginScreenDirections.actionLoginScreenToMainActivity23()
            findNavController().navigate(action)
        }
        binding.forpass.setOnClickListener {
            val action=LoginScreenDirections.actionLoginScreenToForgetPassWord()
            findNavController().navigate(action)
        }
        binding.backSign.setOnClickListener {
            val action = LoginScreenDirections.actionLoginScreenToSignUpScreen()
            findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        if (args.flags) {
            activity?.finish()
        }
    }

}