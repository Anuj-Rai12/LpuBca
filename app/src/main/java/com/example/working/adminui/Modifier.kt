package com.example.working.adminui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.ModiferFragmentBinding

class Modifier : Fragment(R.layout.modifer_fragment) {
    private lateinit var binding: ModiferFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ModiferFragmentBinding.bind(view)
    }
}