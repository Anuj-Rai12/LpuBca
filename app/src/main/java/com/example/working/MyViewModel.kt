package com.example.working

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.working.repos.MyRepository
import com.example.working.utils.Event
import com.example.working.utils.userchannel.UserInfo1
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(private val myRepository: MyRepository) : ViewModel() {

    private var _event = MutableLiveData<Event<String>>()
    val event: LiveData<Event<String>>
        get() = _event

    fun createAccount(userInfo1: UserInfo1) {
        val str=myRepository.createAcc(userInfo1)
        if (str.isNotEmpty())
            _event.value=Event(str)
    }

    fun signInAccount(email: String, password: String) {
        _event.value=Event(myRepository.signInAccount(email,password))
    }

}