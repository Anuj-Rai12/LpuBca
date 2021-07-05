package com.example.working.recycle.unit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.working.adminui.AllData
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.SubjectItemBinding
import com.example.working.recycle.subject.SubjectViewHolder

class UnitRecycleView(private val function: (List<FileInfo>, String) -> Unit) :
    PagingDataAdapter<AllData, SubjectViewHolder>(
        DiffUtil
    ) {
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val current=getItem(position)
        current?.let {
            holder.unitBind(function,it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = SubjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<AllData>() {
            override fun areItemsTheSame(oldItem: AllData, newItem: AllData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AllData, newItem: AllData): Boolean {
                return oldItem == newItem
            }

        }
    }
}