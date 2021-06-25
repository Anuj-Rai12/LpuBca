package com.example.working.adminui.respotry

import android.net.Uri
import com.example.working.utils.Materials
import com.example.working.utils.MyFilePath
import com.example.working.utils.MySealed
import com.google.firebase.firestore.FirebaseFirestore
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
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun uploadFile(
        folderName: String,
        fileName: String,
        fileUrl: Uri,
        source: String
    ) = flow {
        emit(MySealed.Loading(null))
        val data = try {
            val fileUpload = storageReference.child(folderName)
            val fileInfo = fileUpload.putFile(fileUrl).await()
            val url = fileUpload.downloadUrl.await()
            val data = FileInfo(
                fileSize = getFileSize(fileInfo.totalByteCount),
                downloadUrl = url.toString(),
                folderPath = folderName,
                fileName = fileName,
                sourceId = source
            )
            MySealed.Success(data)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }

    private fun getFileSize(fileSize: Long): String {
        return if (fileSize.toInt() / 1024 <= 1000)
            "${(fileSize / 1024).toDouble()} Kb"
        else
            "${(fileSize / 1048576).toDouble()} Mb"
    }

    fun deleteFile(path: String) = flow {
        emit(MySealed.Loading("Deleting the File."))
        val data = try {
            storageReference.child(path).delete().await()
            MySealed.Success("File Path :$path\n\nDeleted Successfully.")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }

    fun addFirstSet(path: MyFilePath, materials: Materials) = flow {
        emit(MySealed.Loading("1 Set is Being Uploading"))
        val data = try {
            fireStore.collection(path.collection!!).document(path.document!!).set(materials).await()
            MySealed.Success(null)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }
}

data class FileInfo(
    val fileSize: String? = null,
    val downloadUrl: String? = null,
    val folderPath: String? = null,
    val fileName: String? = null,
    val sourceId: String? = null
)