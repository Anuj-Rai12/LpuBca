package com.example.working.loginorsignup.forgertpass

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.ForgetFragementBinding
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern


class ForgetPassWord : Fragment(R.layout.forget_fragement) {
    private lateinit var binding: ForgetFragementBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ForgetFragementBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val codePhone = "${binding.codePicker.selectedCountryCodeWithPlus}${binding.phoneText.text}"
            if (binding.phoneText.text.toString().isBlank()
                || binding.phoneText.text.toString().isEmpty() ||
                binding.emailText.text.toString().isEmpty() ||
                binding.emailText.text.toString().isBlank()
            ) {
                Snackbar.make(requireView(), "Please fill the Information", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (!isValidPhone(codePhone)){
                Snackbar.make(requireView(), "Please enter Correct Registered PhoneNo", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (!isValidEmail(binding.emailText.text.toString()))
            {
                Snackbar.make(requireView(), "Please enter the Correct Email Address", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            dir(codePhone,email = binding.emailText.text.toString())
        }
        binding.createacc.setOnClickListener {
            val action = ForgetPassWordDirections.actionForgetPassWordToSignUpScreen()
            findNavController().navigate(action)
        }
    }

    private fun dir(phone: String,email:String) {
        val action = ForgetPassWordDirections.actionForgetPassWordToMakeSelection(email, phone)
        findNavController().navigate(action)
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonetic = "^[+]?[0-9]{10,13}\$"
        val pattern = Pattern.compile(phonetic)
        return pattern.matcher(phone).matches()
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}