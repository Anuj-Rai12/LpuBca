package com.example.working.recycle.resource

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.working.databinding.FloderItemBinding
import com.example.working.utils.Materials
import com.example.working.utils.SubjectInfo


class ResourcesViewHolder(private val binding: FloderItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(function: (List<SubjectInfo>, String) -> Unit, materials: Materials) {
        binding.apply {
            myName.text = materials.udi
            newsTitle.text = "${materials.id},"
            subjectDesc.text = materials.description ?: "No Description"
            SourceTime.text = materials.time ?: "00/00/0000"
            root.setOnClickListener {
                val subInfo: MutableList<SubjectInfo> = mutableListOf()
                materials.subject?.filter { sub ->
                    val key = sub.key
                    val subj = sub.value
                    subj.orginalsub = key
                    subInfo.add(subj)
                }
                function(subInfo, materials.id ?: " ")
            }
        }
    }
}