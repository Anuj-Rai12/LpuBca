package com.example.working.recycle.share

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.working.databinding.ShareFileItemBinding
import com.example.working.loginorsignup.TAG
import com.example.working.userfagment.LocalFileInfo
import javax.inject.Inject

class ShareRecycleView @Inject constructor() : ListAdapter<LocalFileInfo, ShareViewHolder>(DiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareViewHolder {
        val binding=ShareFileItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ShareViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShareViewHolder, position: Int) {
        val currentItem=getItem(position)
        currentItem?.let {
            Log.i(TAG, "onBindViewHolder: SENDER the Object")
            holder.bind(it)
        }
    }
    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<LocalFileInfo>() {
            override fun areItemsTheSame(oldItem: LocalFileInfo, newItem: LocalFileInfo): Boolean {
                return oldItem.fileUrl == newItem.fileUrl
            }

            override fun areContentsTheSame(
                oldItem: LocalFileInfo,
                newItem: LocalFileInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}