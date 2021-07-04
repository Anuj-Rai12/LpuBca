package com.example.working.recycle.subject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.working.databinding.SubjectItemBinding
import com.example.working.utils.SubjectInfo

class SubjectRecycleView constructor(private val function: (String) -> Unit) :
    ListAdapter<SubjectInfo, SubjectViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = SubjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(function,it)
        }
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<SubjectInfo>() {
            override fun areItemsTheSame(oldItem: SubjectInfo, newItem: SubjectInfo): Boolean {
                return oldItem.subject == newItem.subject
            }

            override fun areContentsTheSame(oldItem: SubjectInfo, newItem: SubjectInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}