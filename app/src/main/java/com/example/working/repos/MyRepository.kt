package com.example.working.repos

import android.util.Log
import com.example.working.loginorsignup.TAG
import com.example.working.utils.userchannel.FireBaseUser
import com.example.working.utils.userchannel.UserInfo1
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
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
        val ico = Blob.fromBytes(info1.icon!!)
        val userdata = FireBaseUser(
            firstname = info1.firstname,
            lastname = info1.lastname,
            gender = info1.gender,
            dob = info1.dob,
            semester = info1.semester,
            phone = info1.phone,
            email = info1.email,
            password = info1.password,
            icon = ico
        )

        val user = reference.set(userdata)
        return if (user.isSuccessful)
            SUCCESS
        else {
            Log.i(TAG, "createAcc: User ${user.exception?.localizedMessage}")
            user.exception?.localizedMessage ?: ERROR
        }
    }

    fun signInAccount(email: String, password: String): String {
        val user = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        val result = Tasks.await(user)

        return if (result.user?.uid!=null)
            "Account Created Successfully"
        else {
            Log.i(TAG, "signInAccount: ${user.exception?.localizedMessage}")
            user.exception?.localizedMessage ?: ERROR
        }
    }
}
const val SUCCESS="Account is Created Successfully"
const val ERROR="UnFortunate Error"