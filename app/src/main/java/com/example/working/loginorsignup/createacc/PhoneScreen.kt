package com.example.working.loginorsignup.createacc

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.R
import com.example.working.databinding.PhoneNumberBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.userchannel.UserInfo1
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern


class PhoneScreen : Fragment(R.layout.phone_number) {
    private lateinit var binding: PhoneNumberBinding
    private val args: PhoneScreenArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PhoneNumberBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val phone =
                "${binding.countryCode.selectedCountryCodeWithPlus}${binding.phone.text.toString()}"
            Log.i(TAG, "onViewCreated: $phone is ${isValidPhone(phone)}")
            if (!isValidPhone(phone)) {
                Snackbar.make(requireView(), "Enter The Valid Phone Number", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val info1 = UserInfo1(
                firstname = args.usertwo.firstname,
                lastname = args.usertwo.lastname,
                gender = args.usertwo.gender,
                dob = args.usertwo.dob,
                password = args.usertwo.password,
                phone = phone,
                semester = args.usertwo.semester,
                email = args.usertwo.email
            )
            Log.i(TAG, "onViewCreated: $info1")
            dir(info1)
        }
        binding.backTo.setOnClickListener {
            val action = PhoneScreenDirections.actionGlobalLoginScreen2()
            findNavController().navigate(action)
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonetic = "^[+]?[0-9]{10,13}\$"
        val pattern = Pattern.compile(phonetic)
        return pattern.matcher(phone).matches()
    }

    private fun dir(userInfo1: UserInfo1) {
        val action = PhoneScreenDirections.actionPhoneScreenToOTPScreen(userInfo1,null)
        findNavController().navigate(action)
    }
}