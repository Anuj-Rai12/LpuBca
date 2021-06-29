package com.example.working.adminui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.PorfileFragmentBinding
import com.example.working.loginorsignup.TAG
import dagger.hilt.android.AndroidEntryPoint

private const val ROTATION="Rotation"
@AndroidEntryPoint
class ProfileFragment:Fragment(R.layout.porfile_fragment) {
    private lateinit var binding: PorfileFragmentBinding
    private var loading:Boolean?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= PorfileFragmentBinding.bind(view)
        savedInstanceState?.let {
            loading=it.getBoolean(ROTATION)
        }
        loading?.let {
            if (it)
            binding.sipeit.isRefreshing=true
        }
        binding.sipeit.setOnRefreshListener {
            Log.i(TAG, "onViewCreated: Refreshing is Started")
            loading=true
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        loading?.let {
            outState.putBoolean(ROTATION,it)
        }
    }
}