package com.example.working.adminui.respotry

import android.net.Uri
import com.example.working.utils.MySealed
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRepository @Inject constructor() {
    //    private var uploadTask: UploadTask? = null
    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val storageReference: StorageReference by lazy {
        storage.getReferenceFromUrl("gs://testfile-de86e.appspot.com/")
    }

    fun uploadFile(
        folderName: String,
        subFolder: String,
        fileName: String,
        fileUrl: Uri,
        source: String
    ) = flow {
        emit(MySealed.Loading(null))
        val data = try {
            val fileUpload = storageReference.child("$folderName/$subFolder/$fileName")
            val fileInfo = fileUpload.putFile(fileUrl).await()
            val url = fileUpload.downloadUrl.await()
            val data = FileInfo(
                fileSize = getFileSize(fileInfo.totalByteCount),
                downloadUrl = url.toString(),
                folderName = folderName,
                subFolder = subFolder,
                fileName = fileName,
                source = source
            )
            MySealed.Success(data)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }

    private fun getFileSize(fileSize: Long): String {
        return if (fileSize.toInt() / 1024 <= 1000)
            "${(fileSize/1024).toDouble()} Kb"
        else
            "${(fileSize / 1048576).toDouble()} Mb"
    }
    //Log.i(TAG, "setUpload: ${(it.bytesTransferred / 1048576).toBigDecimal()} mb/s")
}

data class FileInfo(
    val fileSize: String? = null,
    val downloadUrl: String? = null,
    val folderName: String? = null,
    val subFolder: String? = null,
    val fileName: String? = null,
    val source: String? = null
)