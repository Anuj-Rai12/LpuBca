package com.example.working.loginorsignup.forgertpass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.MkeFramgentBinding


class MakeSelection:Fragment(R.layout.mke_framgent) {
    private lateinit var binding: MkeFramgentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= MkeFramgentBinding.bind(view)
        binding.re1.setOnClickListener {
            val action= MakeSelectionDirections.actionMakeSelectionToOTPScreen()
            findNavController().navigate(action)
        }
    }
}