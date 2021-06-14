package com.example.working.loginorsignup


import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.LoginFragmentBinding
import com.example.working.repos.SUCCESS
import com.example.working.utils.CustomProgressBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginScreen : Fragment(R.layout.login_fragment) {
    private lateinit var binding: LoginFragmentBinding

    @Inject
    lateinit var customProgressBar: CustomProgressBar
    private val myViewModel: MyViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        myViewModel.event.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { string ->
                if (string == SUCCESS) {
                    customProgressBar.dismiss()
                    dir()
                } else {
                    customProgressBar.dismiss()
                    Snackbar.make(requireView(), string, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        binding.nextBtn.setOnClickListener {
            if (binding.emailText.text.toString().isEmpty() || binding.passwordText.text.toString()
                    .isBlank() || binding.passwordText.text.toString()
                    .isBlank() || binding.passwordText.text.toString().isEmpty()
            ) {
                Snackbar.make(requireView(), "Enter the Correct Value", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            customProgressBar.show(requireActivity(),"Details is been Processing..",flag = false)
            myViewModel.signInAccount(
                binding.emailText.text.toString(),
                binding.passwordText.text.toString()
            )
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

    override fun onStart() {
        super.onStart()
        /*if (args.flags) {
            activity?.finish()
        }*/
        FirebaseAuth.getInstance().currentUser?.let {
            //   dir()
        }
    }

    private fun dir() {
        val action = LoginScreenDirections.actionLoginScreenToMainActivity23()
        findNavController().navigate(action)
    }
}