package com.example.working.loginorsignup


import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.LoginFragmentBinding
import com.example.working.utils.CustomProgressBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginScreen : Fragment(R.layout.login_fragment) {
    private lateinit var binding: LoginFragmentBinding
    @Inject
    lateinit var customProgressBar:CustomProgressBar
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            /*binding.emailTextView.boxStrokeColor = (resources.getColor(R.color.red_color, null))
            binding.emailTextView.hint = "hello is Is Wrong"*/
            val action=LoginScreenDirections.actionLoginScreenToMainActivity23()
            findNavController().navigate(action)
            //customProgressBar.show(requireActivity(),"Hello I'm Anuj")
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

    /*override fun onStart() {
        super.onStart()
        *//*if (args.flags) {
            activity?.finish()
        }*//*
    }*/

}