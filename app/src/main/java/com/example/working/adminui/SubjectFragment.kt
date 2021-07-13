package com.example.working.adminui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.adminui.viewmodel.AdminViewModel
import com.example.working.databinding.SubjectFragmentBinding
import com.example.working.loginorsignup.TAG
import com.example.working.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubjectFragment : Fragment(R.layout.subject_fragment) {
    private lateinit var binding: SubjectFragmentBinding
    private val myViewModel: AdminViewModel by activityViewModels()
    private val args: SubjectFragmentArgs by navArgs()
    private var material: String? = null

    @Inject
    lateinit var customProgress: CustomProgress
    private val unitAdapter: ArrayAdapter<String> by lazy {
        val unit = resources.getStringArray(R.array.UnitName)
        ArrayAdapter(requireContext(), R.layout.dropdaown, unit)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SubjectFragmentBinding.bind(view)
        setUnitSpinner()
        binding.pathText.text = "${args.path.collection}/${args.path.document}"
        binding.unitTypeLayout.setOnItemClickListener { _, _, position, _ ->
            material = unitAdapter.getItem(position)
        }
        binding.secondBtn.setOnClickListener {
            val teacherName = binding.TeacherName.text.toString()
            if (material.isNullOrEmpty()
                || teacherName.isEmpty() || teacherName.isBlank()
                || binding.FolderName.text.toString().isBlank()
                || binding.FolderName.text.toString().isEmpty()
                || binding.myFolderName.text.toString().isBlank()
                || binding.myFolderName.text.toString().isEmpty()
            ) {
                Log.i(TAG, "onViewCreated: material->$material")
                Snackbar.make(requireView(), "Please Enter Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setFirst(teacherName)
        }
        Log.i(TAG, "onViewCreated: $args")
        binding.updateFile.setOnClickListener {
            if (
                material.isNullOrEmpty()
                || binding.FolderName.text.toString().isBlank()
                || binding.FolderName.text.toString().isEmpty()
                || myViewModel.fileName.isNullOrEmpty()
            ) {
                Snackbar.make(requireView(), "Please Enter Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updateFile()
        }
        binding.addSubject.setOnClickListener {
            val teacherName = binding.TeacherName.text.toString()
            if (material.isNullOrEmpty() || teacherName.isEmpty() || teacherName.isBlank()
                || binding.FolderName.text.toString().isBlank()
                || binding.FolderName.text.toString().isEmpty()
                || binding.myFolderName.text.toString().isBlank()
                || binding.myFolderName.text.toString().isEmpty()
            ) {
                Snackbar.make(requireView(), "Please Enter Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addSubject(teacherName)
        }
    }

    private fun addSubject(teacher: String) {
        val map = mapOf(
            binding.FolderName.text.toString() to SubjectInfo(
                subject = binding.FolderName.text.toString(),
                description = binding.myFolderName.text.toString(),
                teacher = teacher
            )
        )
        if (checkBeforeUpload())
            myViewModel.addMoreSubject(args.path, map).observe(viewLifecycleOwner) {
                when (it) {
                    is MySealed.Error -> {
                        customProgress.hideLoading(requireActivity())
                        dialog("${it.exception?.localizedMessage}")
                    }
                    is MySealed.Loading -> {
                        customProgress.showLoading(requireActivity(), it.data.toString())
                    }
                    is MySealed.Success -> {
                        customProgress.hideLoading(requireActivity())
                        setUploading()
                    }
                }
            }
    }

    private fun checkBeforeUpload(): Boolean {
        return if (!myViewModel.fileName.isNullOrEmpty()) {
            true
        } else {
            Snackbar.make(requireView(), "File Is Empty ", Snackbar.LENGTH_SHORT).show()
            false
        }
    }

    private fun updateFile() {
        val folderName = binding.FolderName.text.toString()
        val myFilePath = MyFilePath(folderName, material)
        val filePath = listOf(args.path, myFilePath)
        val map = myViewModel.fileName.values.last()
        myViewModel.updateSecondPath(filePath, map).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    customProgress.hideLoading(requireActivity())
                    dialog("${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    customProgress.showLoading(requireActivity(), it.data.toString())
                }
                is MySealed.Success -> {
                    customProgress.hideLoading(requireActivity())
                    myViewModel.fileName.clear()
                    dialog(it.data.toString(), "Success!")
                }
            }
        }
    }

    private fun setFirst(teacherName: String) {
        val map = mapOf(
            binding.FolderName.text.toString() to SubjectInfo(
                subject = binding.FolderName.text.toString(),
                description = binding.myFolderName.text.toString(),
                teacher = teacherName,
            )
        )
        val materials = Materials(
            udi = args.meta.udi,
            time = args.meta.time,
            description = args.meta.description,
            subject = map,
            priority =args.meta.priority
        )
        if (checkBeforeUpload())
            myViewModel.addFirstSet(args.path, materials = materials).observe(viewLifecycleOwner) {
                when (it) {
                    is MySealed.Error -> {
                        customProgress.hideLoading(requireActivity())
                        dialog("${it.exception?.localizedMessage}")
                    }
                    is MySealed.Loading -> {
                        customProgress.showLoading(requireActivity(), it.data.toString())
                    }
                    is MySealed.Success -> {
                        customProgress.hideLoading(requireActivity())
                        setUploading()
                        //dialog(it.data.toString(), "Success!")
                    }
                }
            }
    }

    private fun setUploading() {
        val folderName = binding.FolderName.text.toString()
        val myFilePath = MyFilePath(folderName, material)
        val filePath = listOf(args.path, myFilePath)
        val allData = AllData(
            date = getDateTime(),
            map = myViewModel.fileName,
            unit = myViewModel.folderName
        )
        myViewModel.addSecondSet(filePath, allData).observe(viewLifecycleOwner) {
            when (it) {
                is MySealed.Error -> {
                    customProgress.hideLoading(requireActivity())
                    dialog("${it.exception?.localizedMessage}")
                }
                is MySealed.Loading -> {
                    customProgress.showLoading(requireActivity(), it.data.toString())
                }
                is MySealed.Success -> {
                    customProgress.hideLoading(requireActivity())
                    myViewModel.fileName.clear()
                    myViewModel.folderName = null
                    dialog(it.data.toString(), title = "Success!!")
                }
            }
        }
    }

    private fun dialog(message: String, title: String = "Error!") {
        val action = SubjectFragmentDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }

    private fun setUnitSpinner() {
        val unit = resources.getStringArray(R.array.UnitName)
        val unitO = ArrayAdapter(requireContext(), R.layout.dropdaown, unit)
        binding.unitTypeLayout.setAdapter(unitO)
    }

    override fun onResume() {
        super.onResume()
        setUnitSpinner()
    }
}

data class AllData(
    var id:String?=null,
    val date: String? = null,
    val map: Map<String, FileInfo>? = null,
    val unit: String? = null
)