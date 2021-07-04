package com.example.working.recycle.subject

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.working.databinding.SubjectItemBinding
import com.example.working.utils.SubjectInfo

class SubjectViewHolder(private val binding: SubjectItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(function: (String) -> Unit, subjectInfo: SubjectInfo) {
        binding.apply {
            binding.root.setOnClickListener {
                function(subjectInfo.subject ?: " ")
            }
            textView.text="${subjectInfo.subject}-"
            textView.append(subjectInfo.description ?: "No Description")
            subjectTeacherName.text = subjectInfo.teacher ?: "No Teacher"
        }
    }
}