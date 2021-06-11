package com.example.working.loginorsignup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.SignFramgnetBinding
import com.example.working.utils.BottomSheet
import com.example.working.utils.SendData

class SignUpScreen : Fragment(R.layout.sign_framgnet), SendData {
    private lateinit var binding: SignFramgnetBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignFramgnetBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            val action = SignUpScreenDirections.actionSignUpScreenToDateDetailScr()
            findNavController().navigate(action)
        }
        binding.backTo.setOnClickListener {
            val action = SignUpScreenDirections.actionGlobalLoginScreen2()
            findNavController().navigate(action)
        }
        getImage()
    }

    private fun getImage() {
        binding.setimagedude.setOnClickListener {
            val myBottomSheet=BottomSheet()
            myBottomSheet.sendData=this
            myBottomSheet.show(childFragmentManager,"Bottom Sheet")
        }
    }

    override fun sendData(string: String, flags: Boolean) {
        Log.i("MYTAG", "onViewCreated: $string and status $flags")
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }
}