package com.example.working.recycle.file

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.working.R
import com.example.working.adminui.respotry.FileInfo
import com.example.working.databinding.FileItemBinding
import com.example.working.utils.*


class FileViewHolder(private val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    fun bindFile(function: (FileInfo) -> Unit, fileInfo: FileInfo, context: Context) {
        binding.apply {
            fileUpdateTime.text = fileInfo.date
            sharedUserName.text = "Shared By: ${fileInfo.sourceId}"
            fileNameTxt.text = fileInfo.fileName
            if (isDocFile(fileInfo.fileName!!) || isDocxFile(fileInfo.fileName))
                subjectImg.setBackgroundResource(R.drawable.myword)
            else if (isPdfFile(fileInfo.fileName))
                subjectImg.setBackgroundResource(R.drawable.mypdf)
            else if (isJpgFile(fileInfo.fileName) || isPngFile(fileInfo.fileName))
                subjectImg.setBackgroundResource(R.drawable.ic_image)
            else if (isWebsiteFile(fileInfo.fileName))
                subjectImg.setBackgroundResource(R.drawable.ic_web)
            ClickLayout.setOnClickListener {
                function(fileInfo)
            }
            if (fileInfo.fileSize != null)
                fileSizeTxt.text = "File Size: ${fileInfo.fileSize}"
            else
                fileSizeTxt.isVisible = false
            ShareSubject.setOnClickListener {
                val shareIt = shareOperation(fileInfo)
                shareText(shareIt!!,context,fileInfo.downloadUrl!!,fileInfo.sourceId!!,
                    fileInfo = "${fileInfo.folderPath},${fileInfo.fileName}")
            }
        }
    }

    private fun shareOperation(file: FileInfo): String? {
        return if (isPdfFile(file.fileName!!))
            SHARE_PDF
        else if (isPngFile(file.fileName) || isJpgFile(file.fileName))
            SHARE_IMAGE
        else if (isDocFile(file.fileName) || isDocxFile(file.fileName))
            SHARED_DOC
        else if (isWebsiteFile(file.fileName))
            SHARED_WEBSITE
        else
            null
    }
}