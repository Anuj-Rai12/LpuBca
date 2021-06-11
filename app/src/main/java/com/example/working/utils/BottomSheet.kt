package com.example.working.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.working.databinding.MybottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: MybottomsheetBinding
     var sendData: SendData? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MybottomsheetBinding.inflate(inflater)
        binding.opencamera.setOnClickListener {

            sendData?.sendData("Hello From Station",true)
            dismiss()
        }
        binding.opengallery.setOnClickListener {

            Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show()
        }
        binding.randomImage.setOnClickListener {
            Toast.makeText(activity, "Working", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
}