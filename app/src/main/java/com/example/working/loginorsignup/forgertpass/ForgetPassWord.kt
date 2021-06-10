package com.example.working.loginorsignup.forgertpass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.ForgetFragementBinding


class ForgetPassWord:Fragment(R.layout.forget_fragement) {
    private lateinit var binding:ForgetFragementBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= ForgetFragementBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val action= ForgetPassWordDirections.actionForgetPassWordToMakeSelection()
            findNavController().navigate(action)
        }
    }
}