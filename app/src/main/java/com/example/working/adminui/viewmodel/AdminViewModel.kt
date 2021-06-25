package com.example.working.adminui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.working.adminui.respotry.AdminRepository
import com.example.working.adminui.respotry.FileInfo
import com.example.working.utils.Materials
import com.example.working.utils.MyFilePath
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) :
    ViewModel() {

    var fileInfo: FileInfo? = null
    var fileUrl: Uri? = null
    var fileName: MutableMap<String, String> = mutableMapOf()
    fun uploadFile(
        folderName: String,
        fileName: String,
        source: String
    ) = adminRepository.uploadFile(folderName, fileName, fileUrl!!, source).asLiveData()

    fun deleteFile(path: String) = adminRepository.deleteFile(path).asLiveData()

    fun addFirstSet(path: MyFilePath, materials: Materials) =
        adminRepository.addFirstSet(path, materials).asLiveData()

    fun addSecondSet(){

    }

}