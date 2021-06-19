package com.example.working.loginorsignup


import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.working.ADMIN_PHONE
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.LoginFragmentBinding
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginScreen : Fragment(R.layout.login_fragment) {
    private lateinit var binding: LoginFragmentBinding

    @Inject
    lateinit var customProgressBar: CustomProgressBar
    private val myViewModel: MyViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        binding.nextBtn.setOnClickListener {
            if (binding.emailText.text.toString().isEmpty() || binding.passwordText.text.toString()
                    .isBlank() || binding.passwordText.text.toString()
                    .isBlank() || binding.passwordText.text.toString().isEmpty()
            ) {
                Snackbar.make(requireView(), "Enter the Correct Value", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            validUser()
        }
        myViewModel.event.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
            }
        }
        myViewModel.read.observe(viewLifecycleOwner) { userStore ->
            binding.apply {
                emailText.setText(userStore.email)
                passwordText.setText(userStore.password)
                remeberme.isChecked = userStore.flag
                Log.i(TAG, "onViewCreated: $userStore")
            }
        }
        binding.forpass.setOnClickListener {
            val action = LoginScreenDirections.actionLoginScreenToForgetPassWord()
            findNavController().navigate(action)
        }
        binding.backSign.setOnClickListener {
            val action = LoginScreenDirections.actionLoginScreenToSignUpScreen()
            findNavController().navigate(action)
        }
    }

    private fun hideLoading() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        customProgressBar.dismiss()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun showLoading(string: String?, boolean: Boolean = false) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        customProgressBar.show(requireActivity(), string, boolean)
    }

    private fun validUser() {
        myViewModel.signInAccount(
            binding.emailText.text.toString(),
            binding.passwordText.text.toString()
        ).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Loading -> {
                    showLoading(it.data.toString())
                }
                is MySealed.Success -> {
                    hideLoading()
                    remember()
                    dir()
                }
                is MySealed.Error -> {
                    hideLoading()
                    val action = LoginScreenDirections.actionGlobalPasswordDialog(
                        "${it.exception?.localizedMessage}",
                        "Error"
                    )
                    findNavController().navigate(action)
                    Log.i(TAG, "validUser: ${it.exception?.localizedMessage}")
                }
            }
        }
    }

    private fun remember() {
        val flag = binding.remeberme.isChecked
        if (flag)
            myViewModel.storeInfo(
                binding.emailText.text.toString(),
                binding.passwordText.text.toString(),
                flag
            )
    }


    private fun dir() {
        val action = if (FirebaseAuth.getInstance().currentUser?.phoneNumber != ADMIN_PHONE)
            LoginScreenDirections.actionLoginScreenToMainActivity23()
        else
            LoginScreenDirections.actionLoginScreenToMainActivity4()

        findNavController().navigate(action)
        activity?.finish()
    }
}