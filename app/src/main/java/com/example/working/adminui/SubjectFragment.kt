package com.example.working.adminui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.SubjectFragmentBinding

class SubjectFragment:Fragment(R.layout.subject_fragment) {
    private lateinit var binding:SubjectFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= SubjectFragmentBinding.bind(view)
    }
}