package com.example.working

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.working.repos.MyRepository
import com.example.working.utils.userchannel.UserInfo1
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(private val myRepository: MyRepository) : ViewModel() {
    var imgage:Bitmap?=null
    fun createUser(email: String, password: String) =
        myRepository.createUser(email, password).asLiveData()

    fun createAccount(icon: Bitmap, userInfo1: UserInfo1) = myRepository.createAcc(icon,userInfo1).asLiveData()

    fun signInAccount(email: String, password: String) =
        myRepository.signInAccount(email, password).asLiveData()
}