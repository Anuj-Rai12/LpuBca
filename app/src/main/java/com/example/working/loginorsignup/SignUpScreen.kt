package com.example.working.loginorsignup

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
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
import com.example.working.utils.SendData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

const val TAG="MYTAG"
class SignUpScreen : Fragment(R.layout.sign_framgnet), SendData {
    private lateinit var binding: SignFramgnetBinding
    private var myBitmap: Bitmap? = null
    //private val myViewModel: MyViewModel by activityViewModels()
    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it != null)
                getCameraImage(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignFramgnetBinding.bind(view)
        savedInstanceState?.let {
            myBitmap=it.getByteArray("IMAGE")?.let {byte->
                Convertor.covertByteArray2image(byte)
            }
        }
        myBitmap?.let {
            binding.profileImage.setImageBitmap(it)
            binding.profileImage.setBackgroundColor(Color.WHITE)
        }
        binding.nextBtn.setOnClickListener {
            val action = SignUpScreenDirections.actionSignUpScreenToDateDetailScr()
            findNavController().navigate(action)
        }
        binding.backTo.setOnClickListener {
            val action = SignUpScreenDirections.actionGlobalLoginScreen2()
            findNavController().navigate(action)
        }
        getImage()
    }

    private fun getImage() {
        binding.setimagedude.setOnClickListener {
            val myBottomSheet = BottomSheet()
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
        Snackbar.make(requireView(), "Loading...", Snackbar.LENGTH_SHORT).show()
        lifecycleScope.launch {
            myBitmap = getBitmap()
            binding.profileImage.setImageBitmap(myBitmap)
        }
    }

    private suspend fun getBitmap(): Bitmap? {
        binding.progress.isVisible=true
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data("https://picsum.photos/800/800")
            .build()
        return try {
            val result = (loading.execute(request) as SuccessResult).drawable
            binding.progress.isVisible=false
            (result as BitmapDrawable).bitmap
        } catch (e: Exception) {
            binding.progress.isVisible=false
            Toast.makeText(activity, "Oops Something went wrong", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (myBitmap!=null)
            outState.putByteArray("IMAGE",Convertor.covertImages2ByteArray(myBitmap!!))
        else
            Log.i(TAG, "onSaveInstanceState: MyBit is Null")
    }
}