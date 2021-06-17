package com.example.working.loginorsignup.forgertpass

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.databinding.OptFramgnetBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.Convertor
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val KEY = "OUTAG"

@AndroidEntryPoint
class OTPScreen : Fragment(R.layout.opt_framgnet) {
    private lateinit var binding: OptFramgnetBinding
    private lateinit var navController: NavController
    private val args: OTPScreenArgs by navArgs()
    private val myViewModel: MyViewModel by activityViewModels()
    private var verificationProg = false
    private var auth: FirebaseAuth? = null
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var myCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    @Inject
    lateinit var customProgressBar: CustomProgressBar
    private val timer = object : CountDownTimer(60000, 1000) {
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            binding.otpCountDown.text = "${millisUntilFinished / 1000}s"
        }

        override fun onFinish() {
            binding.resendotp.isVisible = true
            binding.otpCountDown.text = ""
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = OptFramgnetBinding.bind(view)
        navController = Navigation.findNavController(view)
        binding.phoneno.text = args.userthree?.phone ?: args.myphoneno
        savedInstanceState?.let {
            verificationProg = savedInstanceState.getBoolean(KEY)
        }
        myCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: Credential Created Successfully")
                verificationProg = false
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.errorMsg.isVisible = true
                binding.errorMsg.text = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        "Error :(\nInvalid Phone Number"
                    }
                    is FirebaseTooManyRequestsException -> {
                        "Error :(\nUsed Too Many Messages Try After 8 hrs."
                    }
                    else -> "Error :(\n ${e.message.toString()}"
                }
                verificationProg = false
                timer.cancel()
                binding.otpCountDown.text = ""
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Toast.makeText(activity, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
                this@OTPScreen.verificationId = verificationId
                resendToken = token
            }
        }
        if (args.myphoneno.isNullOrEmpty()) {
            args.userthree?.let { info ->
                //myViewModel.userData = info
                Log.i(TAG, "onViewCreated: OTP is ->${binding.pinView.text.toString()}")
                Log.i(TAG, "onViewCreated: User Data is ->${info}")
                Log.i(TAG, "PHONE IS ->${info.phone}")
                //Send Code
                signInWithPhoneNumber(info.phone!!)
            }
        } else {
            args.myphoneno?.let {phone->
                Log.i(TAG, "onViewCreated: MyPhone number ->$phone")
                //SendCode
                signInWithPhoneNumber(phone)
            }
        }
        binding.verify.setOnClickListener {
            binding.errorMsg.isVisible = false
            if (binding.pinView.text.toString().isBlank() || binding.pinView.text.toString()
                    .isEmpty()
            ) {
                Snackbar.make(requireView(), "Enter the Correct Otp", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            checkCode(verificationId, code = binding.pinView.text.toString())
        }
        binding.resendotp.setOnClickListener {
            if (args.myphoneno == null && args.userthree?.phone != null)
                resendCode(args.userthree?.phone!!, resendToken)
            else if (args.myphoneno != null && args.userthree?.phone == null)
                resendCode(args.myphoneno!!, resendToken)
            else
                Log.i(TAG, "onViewCreated: Invalid Resend Phone Number")
        }
    }

    private fun resendCode(phone: String, resendToken: PhoneAuthProvider.ForceResendingToken?) {
        val provide = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phone)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(myCallBack!!)
            .setForceResendingToken(resendToken!!).build()
        PhoneAuthProvider.verifyPhoneNumber(provide)
        timer.start()
        binding.resendotp.isVisible = false
        Toast.makeText(activity, "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun checkCode(verificationId: String?, code: String) {
        verificationId?.let {
            val pro = PhoneAuthProvider.getCredential(it, code)
            signInWithCredential(pro)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth?.signInWithCredential(credential)?.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                args.userthree?.let {
                    updateUser()
                    return@addOnCompleteListener
                }
                args.myphoneno?.let {
                    dir()
                }
            } else {
                binding.errorMsg.isVisible = true
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                binding.errorMsg.text = result.exception?.localizedMessage.toString()
            }
        }
    }

    private fun updateUser() {
        myViewModel.createUser(args.userthree?.email!!, args.userthree?.password!!)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is MySealed.Loading -> {
                        showLoading(it.data.toString())
                    }
                    is MySealed.Success -> {
                        hideLoading()
                        validUser()
                    }
                    is MySealed.Error -> {
                        hideLoading()
                        val action =
                            OTPScreenDirections.actionGlobalPasswordDialog(
                                "${it.exception?.localizedMessage}",
                                "Error"
                            )
                        findNavController().navigate(action)
                        Log.i(TAG, "validUser: Exception is -> ${it.exception?.localizedMessage}")
                    }
                }
            }
    }

    private fun dir() {
        val action = OTPScreenDirections.actionOTPScreenToMainActivity23()
        findNavController().navigate(action)
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
        Log.i(TAG, "validUser: icon is ${Convertor.covertImages2ByteArray(myViewModel.image!!)}")
        myViewModel.createAccount(myViewModel.image!!, args.userthree!!)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is MySealed.Loading -> {
                        showLoading(it.data.toString())
                    }
                    is MySealed.Success -> {
                        hideLoading()
                        dir()
                    }
                    is MySealed.Error -> {
                        hideLoading()
                        val action =
                            OTPScreenDirections.actionGlobalPasswordDialog(
                                "${it.exception?.localizedMessage}",
                                "Error"
                            )
                        Log.i(TAG, "validUser: Exception is -> ${it.exception?.localizedMessage}")
                        findNavController().navigate(action)
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (verificationProg && args.userthree?.phone != null)
            signInWithPhoneNumber(args.userthree?.phone!!)
        else if (verificationProg && !args.myphoneno.isNullOrEmpty())
            signInWithPhoneNumber(phone = args.myphoneno!!)
        else {
            Log.i(TAG, "onStart: Invalid Phone Number found")
            Log.i(TAG, "PHONE IS ->${args.userthree?.phone}")
        }
    }

    private fun signInWithPhoneNumber(phone: String) {
        val action = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phone)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(myCallBack!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(action)
        verificationProg = true
        timer.start()
        binding.resendotp.isVisible = false
        Log.i(TAG, "signInWithPhoneNumber: WELCOME")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY, verificationProg)
    }
}