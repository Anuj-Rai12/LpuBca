package com.example.working.adminui.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.example.working.adminui.AllData
import com.example.working.adminui.respotry.AdminRepository
import com.example.working.adminui.respotry.FileInfo
import com.example.working.notfiy.MainObject
import com.example.working.notfiy.NotifyInterface
import com.example.working.recycle.paginguser.AdminQuery
import com.example.working.room.UserData
import com.example.working.userfagment.LocalFileInfo
import com.example.working.utils.Materials
import com.example.working.utils.MyFilePath
import com.example.working.utils.MySealedChannel
import com.example.working.utils.SubjectInfo
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    @AdminQuery
    private val adminQuery: DocumentReference,

    private val notifyInterface: NotifyInterface
) :
    ViewModel() {
    var notifyFlag: Boolean? = null
    var notifytitle: String? = null
    var notifyDesc: String? = null
    var notifyImage: String? = null

    var bookTrueId: Long? = null
    var bookFileInfo: FileInfo? = null
    var adminEmail: String? = null
    var folderName: String? = null
    var fileUrl: Uri? = null
    var fileName: MutableMap<String, FileInfo> = mutableMapOf()
    private val _taskEvent = Channel<MySealedChannel>()
    val taskEvent = _taskEvent.receiveAsFlow()
    private var getAllLocalFile = mutableListOf<LocalFileInfo>()
    private var _localData = MutableLiveData<MutableList<LocalFileInfo>>()
    val localData: LiveData<MutableList<LocalFileInfo>>
        get() = _localData

    fun uploadFile(
        folderName: String,
        fileName: String,
        source: String
    ) = adminRepository.uploadFile(folderName, fileName, fileUrl!!, source).asLiveData()

    fun sendNotification(mainObject: MainObject) =
        adminRepository.sendNotification(notifyInterface, mainObject).asLiveData()

    fun getGalleryFile(uri: Uri) {
        val obj = LocalFileInfo(uri)
        getAllLocalFile.add(obj)
        _localData.value = getAllLocalFile
    }

    val getAdminEmailId = adminRepository.getAdminEmailId(adminQuery).asLiveData()

    fun deleteFile(path: String) = adminRepository.deleteFile(path).asLiveData()

    fun addFirstSet(path: MyFilePath, materials: Materials) =
        adminRepository.addFirstSet(path, materials).asLiveData()

    fun addSecondSet(filePath: List<MyFilePath>, allData: AllData) =
        adminRepository.addSecondSet(filePath, allData).asLiveData()

    fun updateSecondPath(filePath: List<MyFilePath>, map: FileInfo) =
        adminRepository.updateSecondPath(filePath, map).asLiveData()

    fun deleteDownload(userData: UserData) {
        viewModelScope.launch {
            adminRepository.deleteDownloadDB(userData)
            _taskEvent.send(MySealedChannel.DeleteAndChannel(userData))
        }
    }

    fun saveDownload(userData: UserData) = adminRepository.saveDownloadFile(userData).asLiveData()

    fun addMoreSubject(path: MyFilePath, map: Map<String, SubjectInfo>) =
        adminRepository.addMoreSubject(path, map).asLiveData()

    fun deleteLocalItem(localFileInfo: LocalFileInfo) {
        viewModelScope.launch {
            getAllLocalFile.remove(localFileInfo)
            _localData.value = getAllLocalFile
            _taskEvent.send(MySealedChannel.DeleteAndChannel(localFileInfo))
        }
    }
}