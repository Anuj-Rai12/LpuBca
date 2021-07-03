package com.example.working.recycle.resource

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.working.databinding.FloderItemBinding
import com.example.working.utils.Materials
import com.example.working.utils.SubjectInfo

class ResourcesRecycleView constructor(private val function: (List<SubjectInfo>) -> Unit) :
    PagingDataAdapter<Materials, ResourcesViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourcesViewHolder {
        val binding = FloderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResourcesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResourcesViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(function,it)
        }
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<Materials>() {
            override fun areItemsTheSame(oldItem: Materials, newItem: Materials): Boolean {
                return oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: Materials, newItem: Materials): Boolean {
                return oldItem == newItem
            }
        }
    }
}