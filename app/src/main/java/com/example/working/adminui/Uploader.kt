package com.example.working.adminui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.working.utils.getDateTime
import com.example.working.utils.getPathFile
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
    private var unitNo: String? = null
    private val courseArrayAdapter: ArrayAdapter<String> by lazy {
        val course = resources.getStringArray(R.array.Course)
        ArrayAdapter(requireContext(), R.layout.dropdaown, course)
    }
    private val arrayAdapter: ArrayAdapter<String> by lazy {
        val weeks = resources.getStringArray(R.array.timers)
        ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
    }
    private val unitAdapter: ArrayAdapter<String> by lazy {
        val unit = resources.getStringArray(R.array.UnitName)
        ArrayAdapter(requireContext(), R.layout.dropdaown, unit)
    }

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
            semesterNo = arrayAdapter.getItem(position)
        }
        binding.materialCollection.setOnItemClickListener { _, _, position, _ ->
            material = courseArrayAdapter.getItem(position)
        }
        binding.unitTypeLayout.setOnItemClickListener { _, _, position, _ ->
            unitNo = unitAdapter.getItem(position)
        }
        binding.uploadfile.setOnClickListener {
            val fileName = binding.uploaderFileName.text.toString()
            val folderName = binding.uploaderFolderName.text.toString()
            if (!checkUI(fileName, folderName))
                return@setOnClickListener
            adminViewModel.fileUrl?.let {
                setUpload(folderName, fileName)
                return@setOnClickListener
            }
            Snackbar.make(requireView(), "please Select Image", Snackbar.LENGTH_SHORT).show()
        }
        binding.deletebtn.setOnClickListener {
            val fileName = binding.uploaderFileName.text.toString()
            val folderName = binding.uploaderFolderName.text.toString()
            val path: String = if (!checkUI(fileName, folderName))
                return@setOnClickListener
            else if (checkUI(fileName, folderName)) {
                generatePath(folderName, fileName)
            } else {
                Toast.makeText(activity, "Unexpected Error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.i(TAG, "onViewCreated: path -> $path")
            adminViewModel.deleteFile(path).observe(viewLifecycleOwner) {
                when (it) {
                    is MySealed.Loading -> {
                        showLoading(it.data as String)
                    }
                    is MySealed.Error -> {
                        hideLoading()
                        dialog("Error", "${it.exception?.localizedMessage}")
                    }
                    is MySealed.Success -> {
                        hideLoading()
                        adminViewModel.fileName.remove(fileName)
                        dialog(title = "Success", message = it.data as String)
                    }
                }
            }
        }

        binding.UpdateFile.setOnClickListener {
            val fileName = binding.uploaderFileName.text.toString()
            val downloadLink = binding.getDownloadLink.text.toString()
            val folderName = binding.uploaderFolderName.text.toString()
            if (fileName.isBlank() || fileName.isEmpty()
                || !useRegex(fileName) || downloadLink.isEmpty() || downloadLink.isBlank()
            ) {
                Log.i(TAG, "onViewCreated: fileName-> $fileName")
                Log.i(TAG, "onViewCreated: UseRegex -> ${useRegex(fileName)}")
                Snackbar.make(requireView(), "Please enter the value", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (folderName.isNotBlank() && folderName.isNotEmpty())
                generatePath(folderName, fileName)
            Log.i(TAG, "onViewCreated: ${adminViewModel.folderName}")
            downloadLink(downloadLink, fileName)
        }
    }

    private fun downloadLink(downloadLink: String, fileName: String) {
        val fileInfo = FileInfo(
            fileSize = null,
            downloadUrl = downloadLink,
            folderPath = null,
            fileName = fileName,
            sourceId = getSourceId(),
            date = getDateTime()
        )
        setMap(fileInfo)
        val msg = "Download Link ->${fileInfo.downloadUrl}\n" +
                "File Size -> ${fileInfo.fileSize}\n" +
                "Data -> ${fileInfo.date}\n" +
                "SourceId ->${fileInfo.sourceId}\n" +
                "FolderPath ->${fileInfo.folderPath}\n" +
                "File Name ->${fileInfo.fileName}"
        Log.i(TAG, "downloadLink: ${adminViewModel.fileName}")
        dialog(title = "Success!", message = msg)
    }

    private fun useRegex(input: String): Boolean {
        val regex =
            Regex(pattern = "^[a-zA-Z]+\\.[a-zA-Z]+$", options = setOf(RegexOption.IGNORE_CASE))
        return regex.matches(input)
    }

    private fun checkUI(fileName: String, folderName: String): Boolean {
        return if (folderName.isEmpty()
            || folderName.isBlank()
            || fileName.isBlank()
            || fileName.isEmpty()
            || semesterNo.isNullOrEmpty()
            || material.isNullOrEmpty()
            || unitNo.isNullOrEmpty()
            || !useRegex(fileName)
        ) {
            Log.i(TAG, "onViewCreated: folderName->$folderName")
            Log.i(TAG, "onViewCreated: fileName->$fileName")
            Log.i(TAG, "onViewCreated: Material IS Here->$material")
            Log.i(TAG, "onViewCreated: Semester number->$semesterNo")
            Log.i(TAG, "onViewCreated: Unit number->$unitNo")
            Log.i(TAG, "checkUI: File name TYPe ${useRegex(folderName)}")
            Snackbar.make(requireView(), "Please File The Details", Snackbar.LENGTH_SHORT)
                .show()
            false
        } else
            true
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
            }
            else -> binding.fileImage.setImageResource(R.drawable.unknownfile)
        }
    }

    private fun dialog(title: String = "File Info", message: String) {
        val action = UploaderDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }

    private fun generatePath(folderName: String, fileName: String): String {
        val tags = getPathFile(folderName)
        var str = "$semesterNo/$material/"
        if (tags.size >= 2) {
            adminViewModel.folderName = tags[1]
        }
        tags.forEach { char ->
            str = "$str$char/"
        }
        return "$str$unitNo/$fileName"
    }

    private fun getSourceId(): String {
        return if (binding.sourceName.text.toString()
                .isEmpty() || binding.sourceName.text.toString()
                .isBlank()
        )
            FirebaseAuth.getInstance().currentUser?.uid!!
        else
            binding.sourceName.text.toString()
    }

    private fun setUpload(folderName: String, fileName: String) {
        Log.i(TAG, "setUpload: ${generatePath(folderName, fileName)}")
        adminViewModel.uploadFile(
            folderName = generatePath(folderName, fileName),
            fileName = fileName,
            source = getSourceId()
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
                                "File Path :${fileInfo.folderPath}\n" +
                                "Upload Date:${fileInfo.date}\n" +
                                "File Size :${fileInfo.fileSize}\n" +
                                "Upload ID :${fileInfo.sourceId}\n" +
                                "Download Url:${fileInfo.downloadUrl}"
                        filSize = "Size :${fileInfo.fileSize}"
                        binding.uploadSize.text = filSize
                        setMap(fileInfo)
                        dialog("Success", message)
                        Log.i(TAG, "setUpload: $fileInfo\n\n\n")
                        Log.i(TAG, "setUpload: ${adminViewModel.fileName}")
                    }
                }
            }
    }

    private fun setMap(fileInfo: FileInfo) {
        adminViewModel.fileName.putAll(setOf("${fileInfo.fileName}" to fileInfo))
    }

    private fun setSemester() {
        val weeks = resources.getStringArray(R.array.timers)
        val weekArray = ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
        val unit = resources.getStringArray(R.array.UnitName)
        val unitO = ArrayAdapter(requireContext(), R.layout.dropdaown, unit)
        val course = resources.getStringArray(R.array.Course)
        val courseArray = ArrayAdapter(requireContext(), R.layout.dropdaown, course)
        binding.uploaderCollection.setAdapter(weekArray)
        binding.materialCollection.setAdapter(courseArray)
        binding.unitTypeLayout.setAdapter(unitO)
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

    private fun getMimeType(uri: Uri) = com.example.working.utils.getMimeType(uri, requireContext())

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