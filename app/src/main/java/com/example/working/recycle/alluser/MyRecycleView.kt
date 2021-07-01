package com.example.working.recycle.alluser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.working.databinding.UserItemBinding
import com.example.working.utils.userchannel.FireBaseUser

class MyRecycleView constructor(private val function: (String) -> Unit) :
    ListAdapter<FireBaseUser, UserViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it,function)
        }
    }

    companion object {
        val DiffUtil = object : DiffUtil.ItemCallback<FireBaseUser>() {
            override fun areItemsTheSame(oldItem: FireBaseUser, newItem: FireBaseUser): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FireBaseUser, newItem: FireBaseUser): Boolean {
                return oldItem == newItem
            }
        }
    }
}