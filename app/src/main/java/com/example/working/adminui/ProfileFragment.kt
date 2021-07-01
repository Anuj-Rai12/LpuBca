package com.example.working.adminui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.PorfileFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.loginorsignup.createacc.FEMALE
import com.example.working.loginorsignup.createacc.MALE
import com.example.working.utils.*
import com.example.working.utils.userchannel.FireBaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val ROTATION = "Rotation"

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.porfile_fragment), UpdateMyInfo {
    private lateinit var binding: PorfileFragmentBinding
    private val myViewModel: MyViewModel by activityViewModels()
    private var loading: Boolean? = null
    private lateinit var userInfoUpdateDialog: UserInfoUpdateDialog

    @Inject
    lateinit var customProgress: CustomProgress
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PorfileFragmentBinding.bind(view)
        Log.i(TAG, "onViewCreated: Welcome")
        savedInstanceState?.let {
            loading = it.getBoolean(ROTATION)
        }
        getUserInfo()
        binding.nameLayout.setOnClickListener {
            if (checkUI(binding.userNameText)) {
                dialog(UPDATE, NAME)
            }
        }
        binding.emailLayout.setOnClickListener {
            if (checkUI(binding.userEmailText))
                dialog(UPDATE, EMAIL)
        }
        /*binding.mobileLayout.setOnClickListener {
            if (checkUI(binding.userPhoneNo) && binding.userPhoneNo.text.toString() != ADMIN_PHONE)
                dialog(UPDATE, PHONE_NO)
        }*/
        binding.semesterLayout.setOnClickListener {
            if (checkUI(binding.userSemester))
                dialog(UPDATE, SEMESTER)
        }
    }


    private fun showLoading(string: String) {
        customProgress.showLoading(requireActivity(), string)
    }

    private fun hideLoading() {
        customProgress.hideLoading(requireActivity())
    }

    private fun getUserInfo() {
        myViewModel.userData.observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    hideLoading()
                    dialog(message = "${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    showLoading("Account Detail is Loading,")
                }
                is MySealed.Success -> {
                    hideLoading()
                    val fireBaseUser = it.data as FireBaseUser
                    setUI(fireBaseUser)
                }
            }
        }
    }

    private fun dialog(title: String = "Error!", message: String) {
        when (title) {
            UPDATE -> {
                userInfoUpdateDialog = UserInfoUpdateDialog(title, message)
                userInfoUpdateDialog.updateMyInfo = this
                userInfoUpdateDialog.show(childFragmentManager, "MyFragment")

            }
            else -> {
                val action =
                    ProfileFragmentDirections.actionGlobalPasswordDialog2(title, message)//Error
                findNavController().navigate(action)
            }
        }
    }

    private fun checkUI(textView: TextView) =
        !(textView.text.toString().isEmpty() || textView.text.toString().isBlank())

    @SuppressLint("SetTextI18n")
    private fun setUI(fireBaseUser: FireBaseUser) {
        binding.apply {
            fireBaseUser.gender?.let { gender ->
                when (gender) {
                    MALE -> userGender.setBackgroundResource(R.drawable.boyicon)
                    FEMALE -> userGender.setBackgroundResource(R.drawable.girlcon)
                    else -> userGender.setBackgroundResource(R.drawable.tansicon)
                }
            }
            userNameText.text = "${fireBaseUser.firstname} ${fireBaseUser.lastname}"
            userName.text = "${fireBaseUser.firstname} ${fireBaseUser.lastname}"
            userEmail.text = fireBaseUser.email ?: ""
            userEmailText.text = fireBaseUser.email ?: ""
            userPhoneNo.text = fireBaseUser.phone ?: ""
            userDob.text = fireBaseUser.dob ?: ""
            userSemester.text = fireBaseUser.semester ?: ""
            userProfileImage.setImageBitmap(Convertor.covertByteArray2image(fireBaseUser.icon?.toBytes()!!))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        loading?.let {
            outState.putBoolean(ROTATION, it)
        }
    }

    override fun updateName(firstName: String, lastName: String) {
        myViewModel.updateValue("$firstName,$lastName", NAME).observe(viewLifecycleOwner) {
            showResult(it)
        }
    }

    override fun updateEmail(email: String, password: String, newEmail: String) {
        myViewModel.signInAccount(email, password).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    hideLoading()
                    dialog("Error", "${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    showLoading(it.data.toString())
                }
                is MySealed.Success -> {
                    hideLoading()
                    updateNewEmail(newEmail)
                }
            }
        }
    }

    private fun updateNewEmail(newEmail: String) {
        myViewModel.updateNewEmail(newEmail).observe(viewLifecycleOwner) {
            showResult(it)
        }
    }

    private fun showResult(it: MySealed<out String>) {
        when (it) {
            is MySealed.Error -> {
                hideLoading()
                dialog("Error", "${it.exception?.localizedMessage}")
            }
            is MySealed.Loading -> {
                showLoading(it.data.toString())
            }
            is MySealed.Success -> {
                hideLoading()
                dialog("Success", it.data.toString())
            }
        }
    }

    override fun updateSemester(semesterNo: String) {
        myViewModel.updateValue(semesterNo, SEMESTER).observe(viewLifecycleOwner) {
            showResult(it)
        }
    }

    override fun updatePhoneNo(phoneWithCode: String, email: String, password: String) {
        Log.i(TAG, "updatePhone: Phone -> $phoneWithCode email -> $email password -> $password")
    }
}

private const val UPDATE = "Update"
const val NAME = "name"
const val EMAIL = "email"
const val PHONE_NO = "phone"
const val SEMESTER = "semester"