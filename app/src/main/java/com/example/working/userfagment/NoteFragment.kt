package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.NoteFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.Convertor
import com.example.working.utils.userchannel.FireBaseUser
import com.example.working.utils.userchannel.UserInfo1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore

class NoteFragment : Fragment(R.layout.note_framgnet) {
    private lateinit var binding: NoteFramgnetBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NoteFramgnetBinding.bind(view)
        binding.op.text="Note Fragment"
    }
}