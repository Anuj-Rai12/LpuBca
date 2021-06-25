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
import com.example.working.utils.CustomProgress
import com.example.working.utils.MyFilePath
import com.example.working.utils.MySealed
import com.example.working.utils.getDateTime
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
            ) {
                Snackbar.make(requireView(), "Please Enter Info", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setFirst(teacherName)
        }
        Log.i(TAG, "onViewCreated: $args")
    }

    private fun setFirst(teacherName: String) {
        myViewModel.addFirstSet(args.path, args.meta).observe(viewLifecycleOwner) {
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
                    setUploading(teacherName)
                    //dialog(it.data.toString(), "Success!")
                }
            }
        }
    }

    private fun setUploading(teacher: String) {
        val folderName = binding.FolderName.text.toString()
        val myFilePath = MyFilePath(folderName, material)
        val filePath = listOf(args.path, myFilePath)
        val allData = AllData(
            fileInfo = myViewModel.fileInfo,
            date = getDateTime(),
            map = myViewModel.fileName,
            teacher = teacher
        )
        Log.i(TAG, "setUploading: $allData\n\n\n\n")
        Log.i(TAG, "setUploading: $filePath")
    }

    private fun dialog(message: String, title: String="Error!") {
        val action = SubjectFragmentDirections.actionGlobalPasswordDialog2(title, message)
        findNavController().navigate(action)
    }

    private fun setUnitSpinner() = binding.unitTypeLayout.setAdapter(unitAdapter)

    override fun onResume() {
        super.onResume()
        setUnitSpinner()
    }
}

data class AllData(
    val fileInfo: FileInfo? = null,
    val date: String? = null,
    val teacher: String? = null,
    val map: Map<String, String>? = null
)