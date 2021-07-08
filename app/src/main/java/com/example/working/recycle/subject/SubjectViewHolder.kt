package com.example.working.recycle.subject

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.working.R
import com.example.working.adminui.AllData
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.SubjectItemBinding
import com.example.working.utils.*

class SubjectViewHolder(private val binding: SubjectItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(function: (String) -> Unit, subjectInfo: SubjectInfo) {
        binding.apply {
            binding.unitCurrentData.isVisible=false
            binding.subjectTeacherName.isVisible=true
            binding.root.setOnClickListener {
                function(subjectInfo.subject ?: " ")
            }
            folderBox.setImageResource(R.drawable.ic_folder_name)
            textView.text="${subjectInfo.subject},\n"
            textView.append(subjectInfo.description ?: "No Description")
            subjectTeacherName.text = subjectInfo.teacher ?: "No Teacher"
        }
    }

    fun unitBind(function: (List<FileInfo>, String) -> Unit, allData: AllData) {
        binding.apply {
            val title:String=allData.unit?:allData.id?:" "
            textView.text=title
            unitCurrentData.isVisible=true
            subjectTeacherName.isVisible=false
            textView.textAlignment=View.TEXT_ALIGNMENT_TEXT_START
            textView.textSize=18.toFloat()
            folderBox.setImageResource(R.drawable.ic_folder_name)
            unitCurrentData.text=allData.date?:"00/00/0000"
            root.setOnClickListener {
                val fileInfo=allData.map?.values
                function(fileInfo?.toList()!!,title)
            }
        }
    }

}