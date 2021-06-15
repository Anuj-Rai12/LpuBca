package com.example.working.loginorsignup


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
        binding.forpass.setOnClickListener {
            val action = LoginScreenDirections.actionLoginScreenToForgetPassWord()
            findNavController().navigate(action)
        }
        binding.backSign.setOnClickListener {
            val action = LoginScreenDirections.actionLoginScreenToSignUpScreen()
            findNavController().navigate(action)
        }
    }

    private fun hideLoading() = customProgressBar.dismiss()
    private fun showLoading(string: String?, boolean: Boolean = false) =
        customProgressBar.show(requireActivity(), string, boolean)

    private fun validUser() {
        myViewModel.signInAccount(
            binding.emailText.text.toString(),
            binding.passwordText.text.toString()
        ).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Loading -> {
                    showLoading(it.data)
                }
                is MySealed.Success -> {
                    hideLoading()
                    dir()
                }
                is MySealed.Error -> {
                    hideLoading()
                    val action=LoginScreenDirections.actionGlobalPasswordDialog(
                        "${it.exception?.localizedMessage}",
                        "Error"
                    )
                    findNavController().navigate(action)
                    Log.i(TAG, "validUser: ${it.exception?.localizedMessage}")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //activity?.finish()
        FirebaseAuth.getInstance().currentUser?.let {
             //  dir()
        }
    }

    private fun dir() {
        val action = LoginScreenDirections.actionLoginScreenToMainActivity23()
        findNavController().navigate(action)
    }
}