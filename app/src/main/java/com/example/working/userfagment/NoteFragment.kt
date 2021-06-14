package com.example.working.userfagment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.working.R
import com.example.working.databinding.NoteFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.Convertor
import com.example.working.utils.userchannel.UserInfo1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteFragment : Fragment(R.layout.note_framgnet) {
    private lateinit var binding: NoteFramgnetBinding
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val udi by lazy {
        FirebaseAuth.getInstance().currentUser?.uid!!
    }
    private val reference by lazy {
        fireStore.collection("Users").document(udi)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NoteFramgnetBinding.bind(view)
        reference.get().addOnSuccessListener {
            if (it.exists()) {
                val info1 = it.toObject(UserInfo1()::class.java)
                info1?.let { userInfo1 ->
                    binding.op.text = ""
                    binding.op.append("User Id $udi\n")
                    binding.op.append("Email Id ${userInfo1.email}\n")
                    binding.op.append("Icon Id ${userInfo1.icon?.toByteArray()}\n")
                    val theByteArray = userInfo1.icon?.toByteArray()
                    Log.i(TAG, "onViewCreated: ByteArray $theByteArray")
                }
            } else
                Log.i(TAG, "onViewCreated: Empty Snapshot")
        }
    }
}