package com.example.working.adminui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.working.adminui.respotry.AdminRepository
import com.example.working.adminui.respotry.FileInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) :
    ViewModel() {

    var fileInfo: FileInfo? = null
    var fileUrl: Uri? = null
    fun uploadFile(
        folderName: String,
        fileName: String,
        source: String
    ) = adminRepository.uploadFile(folderName, fileName, fileUrl!!, source).asLiveData()

}