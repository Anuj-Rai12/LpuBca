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
import com.example.working.adminui.respotry.FileInfo
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.UplodFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.CustomProgressBar
import com.example.working.utils.MySealed
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


private const val FILETYPE = "FILE_KEY"
private const val SEM = "SEMESTER_No"
private const val SIZE = "FileSize"
private const val METADATA = "METALS"

@AndroidEntryPoint
class Uploader : Fragment(R.layout.uplod_fragment) {
    private lateinit var binding: UplodFragmentBinding
    private val adminViewModel: AdminViewModel by activityViewModels()
    private var semesterNo: String? = null
    private var material: String? = null
    private var fileType: String? = null
    private var filSize: String? = null

    @Inject
    lateinit var customProgressBar: CustomProgressBar

    private val request = registerForActivityResult(ActivityResultContracts.GetContent()) {
        adminViewModel.fileUrl = it
        setUI(adminViewModel.fileUrl)
    }

    private fun setUI(fileUrl: Uri?) {
        fileUrl?.let {
            binding.uploadSize.text = ""
            try {
                if (getMimeType(it)!! == "image/png" || getMimeType(it)!! == "image/jpeg") {
                    fileType = "Image"
                    binding.fileImage.setImageURI(it)
                } else {
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
            filSize = it.getString(SIZE)
            material = it.getString(METADATA)
        }
        filSize?.let {
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
        binding.materialCollection.setOnItemClickListener { _, _, position, _ ->
            getMaterial(position)
        }
        binding.uploadfile.setOnClickListener {
            val fileName = binding.uploaderFileName.text.toString()
            val folderName = binding.uploaderFolderName.text.toString()
            if (folderName.isEmpty()
                || folderName.isBlank()
                || fileName.isBlank()
                || fileName.isEmpty()
                || semesterNo.isNullOrEmpty()
                || material.isNullOrEmpty()
            ) {
                Log.i(TAG, "onViewCreated: folderName->$folderName")
                Log.i(TAG, "onViewCreated: fileName->$fileName")
                Log.i(TAG, "onViewCreated: Semester number->$semesterNo")
                Snackbar.make(requireView(), "Please File The Details", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            adminViewModel.fileUrl?.let {
                setUpload(folderName, fileName)
            }
        }
        binding.UpdateFile.setOnClickListener {
            /*val message =
                "File Name ->${binding.uploaderFileName.text.toString()}\n" +
                        "File Uploaded Size ->" +
                        "Local File Path ->${adminViewModel.fileUrl?.path}"
            dialog(message = message)*/
        }
    }

    private fun getMaterial(position: Int) {
        material = when (position) {
            0 -> "PPT"
            1 -> "BOOK"
            2 -> "OTHER Material"
            else -> null
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

    private fun setUpload(folderName: String, fileName: String) {
        val tagArray = folderName.split("\\s*,\\s*".toRegex()).toTypedArray()
        val tags: List<String> = tagArray.toList()
        val source =
            if (binding.sourceName.text.toString().isEmpty() || binding.sourceName.text.toString()
                    .isBlank()
            )
                FirebaseAuth.getInstance().currentUser?.uid!!
            else
                binding.sourceName.text.toString()

        adminViewModel.uploadFile(
            folderName = "$semesterNo/$material/${tags[0]}",
            subFolder = tags[1],
            fileName = fileName,
            source = source
        )
            .observe(viewLifecycleOwner) {
                when (it) {
                    is MySealed.Loading -> {
                        showLoading("File Is Uploading.")
                    }
                    is MySealed.Error -> {
                        hideLoading()
                        dialog("Error", "${it.exception?.localizedMessage}")
                    }
                    is MySealed.Success -> {
                        hideLoading()
                        val fileInfo = it.data as FileInfo
                        val message = "Local Source\n" +
                                "File name : ${adminViewModel.fileUrl?.lastPathSegment}\n" +
                                "File Type : $fileType\n" +
                                "File Path : ${adminViewModel.fileUrl}\n" +
                                "\nRemote Source\n" +
                                "File Path :${fileInfo.folderName}/" +
                                "${fileInfo.subFolder}/" +
                                "${fileInfo.fileName}\n" +
                                "File Size :${fileInfo.fileSize}\n" +
                                "Upload ID :${fileInfo.source}\n" +
                                "Download Url:${fileInfo.downloadUrl}"
                        filSize = "Size :${fileInfo.fileSize}"
                        binding.uploadSize.text = filSize
                        dialog("Success", message)
                        Log.i(TAG, "setUpload: $fileInfo")
                    }
                }
            }
    }

    private fun setSemester() {
        val weeks = resources.getStringArray(R.array.timers)
        val course = resources.getStringArray(R.array.Course)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
        val courseArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdaown, course)
        binding.uploaderCollection.setAdapter(arrayAdapter)
        binding.materialCollection.setAdapter(courseArrayAdapter)
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
        filSize?.let {
            outState.putString(SIZE, it)
        }
        material?.let {
            outState.putString(METADATA, it)
        }
    }
}