package com.example.working.recycle.share

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.working.R
import com.example.working.databinding.ShareFileItemBinding
import com.example.working.loginorsignup.TAG
import com.example.working.userfagment.LocalFileInfo


class ShareViewHolder(private val binding: ShareFileItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(localFileInfo: LocalFileInfo) {
        Log.i(TAG, "bind: Got the OBJECT ")
        val uri=localFileInfo.fileUrl
        binding.apply {
            if (uri.lastPathSegment?.contains(".png",true)!! ||uri.lastPathSegment?.contains(".jpg",true)!!) {
                shareFileImage.setBackgroundResource(R.drawable.ic_image)
            } else if (uri.lastPathSegment?.contains(".docx",true)!!|| uri.lastPathSegment?.contains(".doc",true)!!) {
                shareFileImage.setBackgroundResource(R.drawable.myword)
            } else if (uri.lastPathSegment?.contains(".pdf",true)!!)
                shareFileImage.setBackgroundResource(R.drawable.mypdf)
            else
                shareFileImage.setBackgroundResource(R.drawable.ic_folder_name)
            textView.text=uri.toString()
        }
    }
}