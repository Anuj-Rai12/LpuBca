package com.example.working.recycle.alluser

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.working.databinding.UserItemBinding
import com.example.working.utils.Convertor
import com.example.working.utils.userchannel.FireBaseUser

class UserViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(fireBaseUser: FireBaseUser, function: (String) -> Unit) {
        binding.apply {
            userImg.setImageBitmap(Convertor.covertByteArray2image(fireBaseUser.icon?.toBytes()!!))
            usernameTxt.text="${fireBaseUser.firstname} ${fireBaseUser.lastname}"
            userMobileTxt.text=fireBaseUser.phone
            userEmailTxt.text=fireBaseUser.email
            userSemesterTxt.text=fireBaseUser.semester
            userDobTxt.text=fireBaseUser.dob
            userGenderTxt.text=fireBaseUser.gender
            root.setOnClickListener {
                function(fireBaseUser.id?:"")
            }
        }
    }
}