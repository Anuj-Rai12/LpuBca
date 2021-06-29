package com.example.working.adminui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.PorfileFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.loginorsignup.createacc.FEMALE
import com.example.working.loginorsignup.createacc.MALE
import com.example.working.utils.Convertor
import com.example.working.utils.MySealed
import com.example.working.utils.userchannel.FireBaseUser
import dagger.hilt.android.AndroidEntryPoint

private const val ROTATION = "Rotation"

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.porfile_fragment) {
    private lateinit var binding: PorfileFragmentBinding
    private val myViewModel: MyViewModel by activityViewModels()
    private var loading: Boolean? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PorfileFragmentBinding.bind(view)
        savedInstanceState?.let {
            loading = it.getBoolean(ROTATION)
        }
        loading?.let {
            if (it) {
                binding.sipeit.isRefreshing = true
                getUserInfo()
            }
        }
        getUserInfo()
        binding.sipeit.setOnRefreshListener {
            Log.i(TAG, "onViewCreated: Refreshing is Started")
            loading = true
            getUserInfo()
        }
    }

    private fun showLoading() {
        binding.sipeit.isRefreshing = true
        loading = true
    }

    private fun hideLoading() {
        binding.sipeit.isRefreshing = false
        loading = false
    }

    private fun getUserInfo() {
        myViewModel.userData.observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    hideLoading()
                    dialog(message = "${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    showLoading()
                }
                is MySealed.Success -> {
                    hideLoading()
                    val fireBaseUser=it.data as FireBaseUser
                    setUI(fireBaseUser)
                }
            }
        }
    }

    private fun dialog(title:String="Error!",message: String) {
        val action=ProfileFragmentDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    private fun setUI(fireBaseUser: FireBaseUser) {
        binding.apply {
            fireBaseUser.gender?.let {gender->
                when (gender) {
                    MALE -> userGender.setBackgroundResource(R.drawable.boyicon)
                    FEMALE -> userGender.setBackgroundResource(R.drawable.girlcon)
                    else -> userGender.setBackgroundResource(R.drawable.tansicon)
                }
            }
            userNameText.text="${fireBaseUser.firstname} ${fireBaseUser.lastname}"
            userName.text="${fireBaseUser.firstname} ${fireBaseUser.lastname}"
            userEmail.text=fireBaseUser.email?:""
            userEmailText.text=fireBaseUser.email?:""
            userPhoneNo.text=fireBaseUser.phone?:""
            userDob.text=fireBaseUser.dob?:""
            userSemester.text=fireBaseUser.semester?:""
            userProfileImage.setImageBitmap(Convertor.covertByteArray2image(fireBaseUser.icon?.toBytes()!!))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        loading?.let {
            outState.putBoolean(ROTATION, it)
        }
    }
}