package com.example.working.adminui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.working.R
import com.example.working.databinding.ModiferFragmentBinding
import com.example.working.utils.Materials
import com.example.working.utils.MyFilePath
import com.example.working.utils.getDateTime
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class Modifier : Fragment(R.layout.modifer_fragment) {
    private lateinit var binding: ModiferFragmentBinding
    private var semesterNo: String? = null
    private var material: String? = null
    private val courseArrayAdapter: ArrayAdapter<String> by lazy {
        val course = resources.getStringArray(R.array.Course)
        ArrayAdapter(requireContext(), R.layout.dropdaown, course)
    }
    private val arrayAdapter: ArrayAdapter<String> by lazy {
        val weeks = resources.getStringArray(R.array.timers)
        ArrayAdapter(requireContext(), R.layout.dropdaown, weeks)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ModiferFragmentBinding.bind(view)
        setSpinner()
        binding.semesterCollection.setOnItemClickListener { _, _, position, _ ->
            semesterNo = arrayAdapter.getItem(position)
        }
        binding.subjectTypeLayout.setOnItemClickListener { _, _, position, _ ->
            material = courseArrayAdapter.getItem(position)
        }
        binding.firstSetBtn.setOnClickListener {
            val desc = binding.setFolderName.text.toString()
            if (semesterNo.isNullOrEmpty() || material.isNullOrEmpty()
                || desc.isEmpty() || desc.isBlank()
            ) {
                Snackbar.make(requireView(), "Please Enter Value", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val str = MyFilePath(
                collection = semesterNo,
                document = material
            )
            val materials = Materials(
                udi = FirebaseAuth.getInstance().currentUser?.uid,
                time = getDateTime(),
                description = desc
            )
            val action =
                ModifierDirections.actionModifierToSubjectFragment(path = str, meta = materials)
            findNavController().navigate(action)
        }
    }

    private fun setSpinner() {
        binding.apply {
            semesterCollection.setAdapter(arrayAdapter)
            subjectTypeLayout.setAdapter(courseArrayAdapter)
        }
    }

    override fun onResume() {
        super.onResume()
        setSpinner()
    }
}