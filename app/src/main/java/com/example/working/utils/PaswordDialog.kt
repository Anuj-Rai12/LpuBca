package com.example.working.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.working.MyViewModel
import com.example.working.R
import com.example.working.adminui.EMAIL
import com.example.working.adminui.NAME
import com.example.working.adminui.PHONE_NO
import com.example.working.adminui.SEMESTER
import com.example.working.databinding.AlertDialogBinding
import com.example.working.loginorsignup.TAG
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class PasswordDialog(val title: String? = null, val Msg: String? = null) :
    androidx.fragment.app.DialogFragment() {
    private val args: PasswordDialogArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alterDialog = AlertDialog.Builder(requireActivity()).setTitle(title ?: args.title)
        alterDialog.setMessage(Msg ?: args.message)
            .setPositiveButton("ok") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        return alterDialog.create()
    }
    //8.At End of Password you may use $ symbol or Any Special Symbol
}

class UserInfoUpdateDialog @Inject constructor(
    private val title: String,
    private val work: String
) : DialogFragment() {
    private lateinit var binding: AlertDialogBinding
    private val myViewModel: MyViewModel by activityViewModels()
    var updateMyInfo: UpdateMyInfo? = null
    private val arrayAdapter: ArrayAdapter<String> by lazy {
        val weeks = resources.getStringArray(R.array.timers)
        ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
    }
    private var semesterNo: String? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = AlertDialogBinding.inflate(layoutInflater)
        val alertDialog =
            AlertDialog.Builder(requireActivity()).setView(binding.root)
                .setTitle("$title $work,")
        getUI()
        setSemester()
        binding.chooseSemester.setOnItemClickListener { _, _, position, _ ->
            semesterNo = arrayAdapter.getItem(position)
        }
        alertDialog.setPositiveButton("Update") { _, _ ->
            myViewModel.profileDialogFlag=false
            setValue()
        }
        alertDialog.setNegativeButton("Cancel") { dialogInterface, _ ->
            myViewModel.profileDialogFlag=false
            dialogInterface.dismiss()
        }
        return alertDialog.create()
    }

    private fun setValue() {
        when (work) {
            NAME -> {
                val firstName = binding.newUserFirstName.text.toString()
                val lastName = binding.newLastName.text.toString()
                if (checkUI(firstName) && checkUI(lastName))
                    updateMyInfo?.updateName(firstName, lastName)
                else
                    msg()
            }
            EMAIL -> {
                val email = binding.currentEmail.text.toString()
                val password = binding.currentPassword.text.toString()
                val newEmail = binding.newEmail.text.toString()
                if (checkUI(email) && checkUI(password) && checkUI(newEmail) && myViewModel.isValidEmail(
                        newEmail
                    )
                )
                    updateMyInfo?.updateEmail(email, password, newEmail)
                else
                    msg()
            }
            PHONE_NO -> {
                val email = binding.currentEmail.text.toString()
                val password = binding.currentPassword.text.toString()
                val phoneWithCode = binding.newCodePicker.selectedCountryCodeWithPlus +
                        binding.newPhoneText.text.toString()
                if (myViewModel.isValidPhone(phoneWithCode) && checkUI(email) && checkUI(password))
                    updateMyInfo?.updatePhoneNo(phoneWithCode, email, password)
                else
                    msg()

            }
            SEMESTER -> {
                if (!semesterNo.isNullOrEmpty())
                    updateMyInfo?.updateSemester(semesterNo!!)
                else
                    msg()
            }
        }
    }

    private fun msg(string: String = "Please Enter the Correct value.") {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }

    private fun checkUI(string: String) = !(string.isBlank() || string.isEmpty())


    override fun onResume() {
        super.onResume()
        setSemester()
    }

    private fun setSemester() {
        val weeks = resources.getStringArray(R.array.timers)
        val weekArray = ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
        binding.chooseSemester.setAdapter(weekArray)
    }

    private fun getUI() {
        binding.root.isVisible = true
        when (work) {
            NAME -> {
                binding.newFireFirstLayout.isVisible = true
                binding.newUserLastLayout.isVisible = true
            }
            EMAIL -> {
                binding.passwordEmail.isVisible = true
                binding.newEmailLayout.isVisible = true
                setEmailPassword()
            }
            PHONE_NO -> {
                binding.passwordEmail.isVisible = true
                binding.newCodePicker.isVisible = true
                binding.newPhoneTextLayout.isVisible = true
                setEmailPassword()
            }
            SEMESTER -> {
                binding.semesterLayout.isVisible = true
            }
        }
    }

    private fun setEmailPassword() {
        myViewModel.read.observe(this) { userStore ->
            binding.currentEmail.setText(userStore.email)
            binding.currentPassword.setText(userStore.password)
            Log.i(TAG, "setValue: $userStore")
        }
    }
}

class UpdateDialog constructor(
    private val message: String,
    private val link: String?,
    private val title: String,
) : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alterDialog = AlertDialog.Builder(activity)
        if (title != "LogOut!") {
            alterDialog
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Update") { _, _ ->
                    link?.let {
                        loadUrl(it)
                    }
                }.setNegativeButton("Exit") { _, _ ->
                    activity?.finish()
                }
        } else {
            alterDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    activity?.finish()
                }.setNegativeButton("No") { _, _ ->
                    dismiss()
                }
        }
        return alterDialog.create()
    }

    private fun loadUrl(s: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(s))
        startActivity(browserIntent)
    }
}