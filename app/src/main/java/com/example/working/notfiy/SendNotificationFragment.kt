package com.example.working.notfiy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.SendNotficationFramgmentBinding

class SendNotificationFragment:Fragment(R.layout.send_notfication_framgment) {
    private lateinit var binding: SendNotficationFramgmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= SendNotficationFramgmentBinding.bind(view)
    }
}