package com.example.working

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.working.repos.ClassPersistence
import com.example.working.repos.MyRepository
import com.example.working.utils.Event
import com.example.working.utils.userchannel.UserInfo1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val myRepository: MyRepository,
    private val classPersistence: ClassPersistence
) : ViewModel() {
    var msg: String? = null
    var downloadLink: String? = null
    var image: Bitmap? = null
    private var _event = MutableLiveData<Event<String>>()
    val event: LiveData<Event<String>>
        get() = _event

    fun createUser(email: String, password: String) =
        myRepository.createUser(email, password).asLiveData()

    fun createAccount(icon: Bitmap, userInfo1: UserInfo1) =
        myRepository.createAcc(icon, userInfo1).asLiveData()

    fun signInAccount(email: String, password: String) =
        myRepository.signInAccount(email, password).asLiveData()

    val read = classPersistence.read.asLiveData()
    fun storeInfo(email: String, password: String, flag: Boolean) {
        updateUserInfo(email, password, flag)
    }

    private fun updateUserInfo(email: String, password: String, flag: Boolean) =
        viewModelScope.launch {
            classPersistence.updateInfo(email, password, flag)
            _event.value = Event("Information Saved")
        }

    fun getUpdate() = myRepository.getUpdate().asLiveData()

    fun getUserProfile() = myRepository.getProfileInfo().asLiveData()

    fun passwordRestEmail(email: String) = myRepository.passwordRestEmail(email).asLiveData()
}