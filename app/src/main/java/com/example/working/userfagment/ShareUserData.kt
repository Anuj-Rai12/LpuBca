package com.example.working.userfagment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.ShareFramgentBinding

class ShareUserData :Fragment(R.layout.share_framgent){
    private lateinit var binding: ShareFramgentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= ShareFramgentBinding.bind(view)
    }
}