package com.example.working.adminui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.PorfileFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment:Fragment(R.layout.porfile_fragment) {
    private lateinit var binding: PorfileFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= PorfileFragmentBinding.bind(view)
    }
}