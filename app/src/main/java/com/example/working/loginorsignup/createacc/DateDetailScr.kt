package com.example.working.loginorsignup.createacc

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.R
import com.example.working.databinding.DateDetailFramgentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.userchannel.UserInfo1
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class DateDetailScr : Fragment(R.layout.date_detail_framgent) {
    private lateinit var binding: DateDetailFramgentBinding
    private val args: DateDetailScrArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DateDetailFramgentBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val month = if ((binding.datePicker.month + 1) <= 9)
                "0${binding.datePicker.month + 1}"
            else
                "${binding.datePicker.month + 1}"

            val date =
                "${binding.datePicker.dayOfMonth}/$month/${binding.datePicker.year}"
            Log.i(TAG, "onViewCreated: $date")
            Log.i(TAG, "onViewCreated: check ${checkDate()}")

            if (getGender() == null) {
                Snackbar.make(requireView(), "Select Gender", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (date == checkDate()) {
                Snackbar.make(requireView(), "Select Date of Birth", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val info1 = UserInfo1(
                firstname = args.userone.firstname,
                lastname = args.userone.lastname,
                gender = getGender(),
                dob = date,
                semester = args.userone.semester,
                email = args.userone.email,
                password = args.userone.password,
                icon = args.userone.icon
            )
            Log.i(TAG, "onViewCreated: $info1")
            dir(info1)
        }

        binding.backTo.setOnClickListener {
            val action = DateDetailScrDirections.actionGlobalLoginScreen2()
            findNavController().navigate(action)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkDate(): String {
        val current = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(current)
    }

    private fun getGender(): String? {
        return when {
            binding.radioButton1.isChecked -> "Male"
            binding.radioButton2.isChecked -> "Female"
            binding.radioButton3.isChecked -> "Others"
            else -> null
        }
    }

    private fun dir(userInfo1: UserInfo1) {
        val action = DateDetailScrDirections.actionDateDetailScrToPhoneScreen(userInfo1)
        findNavController().navigate(action)
    }
}