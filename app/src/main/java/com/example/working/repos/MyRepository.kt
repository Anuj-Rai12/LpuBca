package com.example.working.repos

import android.util.Log
import com.example.working.loginorsignup.TAG
import com.example.working.utils.userchannel.UserInfo1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MyRepository @Inject constructor() {
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val udi by lazy {
        FirebaseAuth.getInstance().currentUser?.uid!!
    }
    private val reference by lazy {
        fireStore.collection("Users").document(udi)
    }

    fun createAcc(info1: UserInfo1): String {
        var string= ""
        reference.set(info1).addOnSuccessListener {
            string = "Account is Created Successfully"
        }.addOnFailureListener {
            string = it.localizedMessage?:""
            Log.i(TAG, "createAcc: ${it.localizedMessage}")
        }
        return string
    }

    fun signInAccount(email: String, password: String): String {
        var string=""
         FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener {
            string = "Account is Created Successfully"
        }.addOnFailureListener {
            string = it.localizedMessage?:""
            Log.i(TAG, "createAcc: ${it.localizedMessage}")
        }
        return string
    }
}