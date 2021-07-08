package com.example.working.recycle.file


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.FileItemBinding


class FileRecycleView constructor(private val context: Context,private val function: (FileInfo) -> Unit) :
    ListAdapter<FileInfo, FileViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.bindFile(function, it,context)
        }
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<FileInfo>() {
            override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}