package com.example.working.notfiy

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.SendNotficationFramgmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.AllMyConstant
import com.example.working.utils.CustomProgress
import com.example.working.utils.MySealed
import com.example.working.utils.getPathFile
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SendNotificationFragment : Fragment(R.layout.send_notfication_framgment) {
    private lateinit var binding: SendNotficationFramgmentBinding
    private val adminViewModel: AdminViewModel by activityViewModels()

    @Inject
    lateinit var customProgress: CustomProgress

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SendNotficationFramgmentBinding.bind(view)
        adminViewModel.notifyFlag?.let {
            if (it) {
                sendNotification(
                    title = adminViewModel.notifytitle!!, desc = adminViewModel.notifyDesc!!,
                    image = adminViewModel.notifyImage
                )
            }
        }
        binding.sendNotification.setOnClickListener {
            val title = binding.notificationTitle.text.toString()
            val desc = binding.notificationDesc.text.toString()
            val image = getValue(binding.notificationLink.text.toString())
            if (getValue(title) == null || getValue(desc) == null) {
                Snackbar.make(requireView(), "Enter the Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendNotification(title, desc, image)
        }
    }

    private fun showLoading(string: String) =
        customProgress.showLoading(requireActivity(), string = string)

    private fun hideLoading() = customProgress.hideLoading(requireActivity())

    private fun dialog(title: String, desc: String) {
        val action = SendNotificationFragmentDirections.actionGlobalPasswordDialog2(
            title = title,
            message = desc
        )
        findNavController().navigate(action)
    }

    private fun sendNotification(title: String, desc: String, image: String?) {
        adminViewModel.notifyDesc = desc
        adminViewModel.notifyImage = image
        adminViewModel.notifytitle = title
        Message(title = title, body = desc, image = image).also { message ->
            MainObject(
                notification = message,
                data = MessageData(Key1 = getValue(binding.notificationDataLink.text.toString())),
                to = AllMyConstant.TOPIC
            ).also { mainObject ->
                adminViewModel.sendNotification(mainObject).observe(viewLifecycleOwner) {
                    when (it) {
                        is MySealed.Error -> {
                            hideLoading()
                            adminViewModel.notifyFlag = false
                            dialog("Error!", it.exception?.localizedMessage!!)
                            Log.i(TAG, "sendNotification: ${it.exception.localizedMessage}")
                        }
                        is MySealed.Loading -> {
                            adminViewModel.notifyFlag = true
                            showLoading(it.data!!)
                        }
                        is MySealed.Success -> {
                            hideLoading()
                            adminViewModel.notifyFlag = false
                            val getArray = getPathFile(it.data!!)
                            dialog(title = getArray.first(), desc = getArray.last())
                        }
                    }
                }
            }
        }
    }

    private fun getValue(notificationLink: String): String? {
        return if (notificationLink.isEmpty() || notificationLink.isBlank())
            null
        else
            notificationLink
    }

    override fun onPause() {
        super.onPause()
        hideLoading()
    }

}