package com.example.working.adminui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.UplodFragmentBinding

class Uploader : Fragment(R.layout.uplod_fragment) {
    private lateinit var binding: UplodFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UplodFragmentBinding.bind(view)
    }
}