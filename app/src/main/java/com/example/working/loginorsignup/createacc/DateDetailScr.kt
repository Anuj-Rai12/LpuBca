package com.example.working.loginorsignup.createacc

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.DateDetailFramgentBinding

class DateDetailScr:Fragment(R.layout.date_detail_framgent) {
    private lateinit var binding: DateDetailFramgentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= DateDetailFramgentBinding.bind(view)
        binding.backTo.setOnClickListener {
            val action= DateDetailScrDirections.actionDateDetailScrToPhoneScreen()
            findNavController().navigate(action)
        }
    }
}