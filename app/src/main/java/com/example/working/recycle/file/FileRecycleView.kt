package com.example.working.recycle.file


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.SubjectItemBinding
import com.example.working.recycle.subject.SubjectViewHolder

class FileRecycleView constructor(private val function:(FileInfo)->Unit): ListAdapter<FileInfo,SubjectViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding=SubjectItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val currItem=getItem(position)
        currItem?.let {
            holder.bindFile(function,it)
        }
    }
companion object{
    val DiffUtil= object : DiffUtil.ItemCallback<FileInfo>() {
        override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
            return  oldItem.date==newItem.date
        }

        override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
            return oldItem==newItem
        }
    }
}
}