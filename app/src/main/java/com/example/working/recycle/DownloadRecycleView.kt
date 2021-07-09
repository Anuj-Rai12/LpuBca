package com.example.working.recycle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.FileItemBinding
import com.example.working.recycle.file.FileViewHolder
import com.example.working.room.UserData

class DownloadRecycleView(private val context: Context,private val function: (FileInfo) -> Unit) :
    ListAdapter<UserData, FileViewHolder>(
        DiffUtil
    ) {

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<UserData>() {
            override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding=FileItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem=getItem(position)
        currentItem.fileInfo.let {
            holder.bindFile(fileInfo = it,function = function,context = context)
        }
    }

}