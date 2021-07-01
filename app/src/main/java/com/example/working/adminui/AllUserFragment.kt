package com.example.working.adminui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.AlluserFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllUserFragment : Fragment(R.layout.alluser_fragment) {
    private lateinit var binding: AlluserFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AlluserFragmentBinding.bind(view)
        binding.myShimmer.isVisible=false
    }
}