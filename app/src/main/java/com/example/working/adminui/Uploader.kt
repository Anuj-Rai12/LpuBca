package com.example.working.adminui

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.UplodFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.CustomProgressBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import javax.inject.Inject


private const val FILETYPE = "FILE_KEY"
private const val SEM = "SEMESTER_No"
private const val SIZE = "FileSize"

@AndroidEntryPoint
class Uploader : Fragment(R.layout.uplod_fragment) {
    private lateinit var binding: UplodFragmentBinding
    private val adminViewModel: AdminViewModel by activityViewModels()
    private var semesterNo: String? = null
    private var localFileSize: String? = null
    private var fileType: String? = null

    @Inject
    lateinit var customProgressBar: CustomProgressBar

    private val request = registerForActivityResult(ActivityResultContracts.GetContent()) {
        adminViewModel.fileUrl = it
        setUI(adminViewModel.fileUrl)
    }

    private fun setUI(fileUrl: Uri?) {
        fileUrl?.let {
            try {
                if (getMimeType(it)!! == "image/png" || getMimeType(it)!! == "image/jpeg") {
                    fileType = "Image"
                    binding.fileImage.setImageURI(it)
                    binding.uploadSize.text = ""
                } else {
                    val file = File(it.path!!)
                    val fileSize = java.lang.String.valueOf(file.length() / 1024).toDouble()
                    localFileSize = if (fileSize.toInt() <= 1000)
                        "Size :${fileSize} KB"
                    else
                        "Size :${file.length() / 1048576} MB"
                    binding.uploadSize.text = localFileSize
                    getMimeType(it)?.let { type ->
                        Log.i(TAG, "setUI: $type")
                        fileType = type
                        setImage(fileType!!)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "Cannot Select File", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "setUI: Exception found ${e.localizedMessage}")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UplodFragmentBinding.bind(view)
        setSemester()
        savedInstanceState?.let {
            fileType = it.getString(FILETYPE)
            semesterNo = it.getString(SEM)
            localFileSize = it.getString(SIZE)
        }
        localFileSize?.let {
            binding.uploadSize.text = it
        }
        fileType?.let {
            setImage(it)
        }
        binding.openFileExplore.setOnClickListener {
            request.launch("*/*")
        }
        binding.uploaderCollection.setOnItemClickListener { _, _, position, _ ->
            getPosition(position)
        }
        binding.uploadfile.setOnClickListener {
            val fileName = binding.uploaderFileName.text.toString()
            val folderName = binding.uploaderFolderName.text.toString()
            if (folderName.isEmpty()
                || folderName.isBlank()
                || fileName.isBlank()
                || fileName.isEmpty()
                || semesterNo.isNullOrEmpty()
            ) {
                Log.i(TAG, "onViewCreated: folderName->$folderName")
                Log.i(TAG, "onViewCreated: fileName->$fileName")
                Log.i(TAG, "onViewCreated: Semester number->$semesterNo")
                Snackbar.make(requireView(), "Please File The Details", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            adminViewModel.fileUrl?.let {
                setUpload(it, folderName, fileName)
                return@setOnClickListener
            }
        }
        binding.UpdateFile.setOnClickListener {
            val message =
                "File Name ->${binding.uploaderFileName.text.toString()}\n" +
                        "File Uploaded Size ->" +
                        "Local File Path ->${adminViewModel.fileUrl?.path}"
            dialog(message = message)
        }
    }

    private fun setImage(imageType: String) {
        when (imageType) {
            "application/pdf" -> binding.fileImage.setImageResource(R.drawable.fileimage)
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> binding
                .fileImage.setImageResource(R.drawable.wordfile)
            "application/msword" -> binding
                .fileImage.setImageResource(R.drawable.wordfile)
            "Image" -> {
                binding.fileImage.setImageURI(adminViewModel.fileUrl)
                binding.uploadSize.text = ""
            }
            else -> binding.fileImage.setImageResource(R.drawable.unknownfile)
        }
    }

    private fun dialog(title: String = "File Info", message: String) {
        val action = UploaderDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }
    private fun setUpload(uri: Uri, folderName: String, fileName: String) {
        val tagArray = folderName.split("\\s*,\\s*".toRegex()).toTypedArray()
        val tags: List<String> = tagArray.toList()
        Log.i(
            TAG,
            "setUpload: File Uri -> $uri \nfolderName -> $tags \nfile Name -> $fileName \nSemester Name $semesterNo"
        )
        Log.i(TAG, "setUpload: file path ->${uri.lastPathSegment}")
    }

    private fun setSemester() {
        val weeks = resources.getStringArray(R.array.timers)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
        binding.uploaderCollection.setAdapter(arrayAdapter)
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

    private fun getMimeType(uri: Uri): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver? = context?.contentResolver
            cr?.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri.toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            )
        }
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

    override fun onResume() {
        super.onResume()
        setSemester()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fileType?.let {
            outState.putString(FILETYPE, it)
        }
        semesterNo?.let {
            outState.putString(SEM, it)
        }
        localFileSize?.let {
            outState.putString(SIZE, it)
        }
    }
}