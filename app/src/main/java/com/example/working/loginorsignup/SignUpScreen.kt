package com.example.working.loginorsignup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.working.R
import com.example.working.databinding.SignFramgnetBinding
import com.example.working.utils.BottomSheet
import com.example.working.utils.Convertor
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.SendData
import com.example.working.utils.userchannel.UserInfo1
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


const val TAG = "MYTAG"

@AndroidEntryPoint
class SignUpScreen : Fragment(R.layout.sign_framgnet), SendData {
    private lateinit var binding: SignFramgnetBinding
    private var myBitmap: Bitmap? = null
    private var semesterNo: String? = null


    @Inject
    lateinit var myBottomSheet: BottomSheet

    @Inject
    lateinit var customProgressBar: CustomProgressBar
    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it != null)
                getCameraImage(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignFramgnetBinding.bind(view)
        setSemester()
        savedInstanceState?.let {
            myBitmap = it.getByteArray("IMAGE")?.let { byte ->
                Convertor.covertByteArray2image(byte)
            }
            semesterNo=it.getString("Sem")
        }
        myBitmap?.let {
            binding.profileImage.setImageBitmap(it)
            binding.profileImage.setBackgroundColor(Color.WHITE)
        }

        binding.nextBtn.setOnClickListener {
            val lastname = binding.lastName.text.toString()
            val firstname = binding.firstName.text.toString()
            val email = binding.emailAddress.text.toString()
            val password = binding.password.text.toString()
            Log.i(TAG, "onViewCreated: $lastname,$firstname,$email,$password $semesterNo")
            if ((lastname.isEmpty() || lastname.isBlank()
                        || firstname.isEmpty() || firstname.isBlank()) || !isValidEmail(email) || password.isEmpty() || password.isBlank() || semesterNo.isNullOrEmpty()
            ) {
                Snackbar.make(requireView(), "Please Enter The Correct value", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val icon = myBitmap ?: convertImage()
                val info1 = UserInfo1(
                    firstname = firstname,
                    lastname = lastname,
                    semester = semesterNo!!,
                    email = email,
                    password = password,
                    icon = Convertor.covertImages2ByteArray(icon)!!
                )
                Log.i(TAG, "onViewCreated: $info1")
                dir(info1)
            }
        }
        binding.backTo.setOnClickListener {
            val action = SignUpScreenDirections.actionGlobalLoginScreen2()
            findNavController().navigate(action)
        }
        binding.Autocom.setOnItemClickListener { _, _, position, _ ->
            getPosition(position)
        }
        getImage()
    }
    private fun isValidEmail(target: CharSequence?): Boolean {
        return if (target==null ) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
    private fun dir(userInfo1: UserInfo1) {
        val action = SignUpScreenDirections.actionSignUpScreenToDateDetailScr(userInfo1)
        findNavController().navigate(action)
    }

    private fun getPosition(position: Int) {
        semesterNo = when (position) {
            0 -> "1th Semester"
            1 -> "2th Semester"
            2 -> "3th Semester"
            3 -> "4th Semester"
            4 -> "5th Semester"
            5 -> "6th Semester"
            else -> null
        }
    }

    private fun getImage() {
        binding.setimagedude.setOnClickListener {
            myBottomSheet.sendData = this
            myBottomSheet.show(childFragmentManager, "Bottom Sheet")
        }
    }

    private fun getCameraImage(it: ActivityResult) {
        if (it.resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap? = it.data?.getParcelableExtra("data")
            if (bitmap != null) {
                myBitmap = bitmap
                binding.profileImage.setImageBitmap(myBitmap)
            }
        }
    }

    override fun sendData() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        requestCamera.launch(intent)
    }

    override fun sendGalImage(string: Uri) {
        binding.profileImage.setImageURI(string)
        myBitmap = getBitmapFromView(binding.profileImage)
        binding.profileImage.setBackgroundColor(Color.WHITE)
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun urlImage() {
        lifecycleScope.launch {
            myBitmap = getBitmap() ?: convertImage()
            binding.profileImage.setImageBitmap(myBitmap)
        }
    }

    private fun convertImage(myImage: Int = R.drawable.myimage): Bitmap =
        BitmapFactory.decodeResource(resources, myImage)

    private suspend fun getBitmap(): Bitmap? {
        customProgressBar.show(requireActivity(), "Image Is Loading..", false)
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data("https://picsum.photos/1000/1000")
            .build()
        return try {
            val result = (loading.execute(request) as SuccessResult).drawable
            customProgressBar.dismiss()
            (result as BitmapDrawable).bitmap
        } catch (e: Exception) {
            customProgressBar.dismiss()
            Snackbar.make(requireView(), "No Internet :(", Snackbar.LENGTH_SHORT).show()
            null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (myBitmap != null)
            outState.putByteArray("IMAGE", Convertor.covertImages2ByteArray(myBitmap!!))
        else
            Log.i(TAG, "onSaveInstanceState: MyBit is Null")
        if (semesterNo!=null)
            outState.putString("Sem",semesterNo)
        else
            Log.i(TAG, "onSaveInstanceState: SemesterNo is Null")
    }

    override fun onResume() {
        super.onResume()
        setSemester()
    }

    private fun setSemester() {
        val weeks = resources.getStringArray(R.array.timers)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
        binding.Autocom.setAdapter(arrayAdapter)
    }
}