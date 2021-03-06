package com.example.working.adminui.respotry

import android.net.Uri
import android.os.Parcelable
import com.example.working.adminui.AllData
import com.example.working.notfiy.MainObject
import com.example.working.notfiy.NotifyInterface
import com.example.working.room.RoomDataBaseInstance
import com.example.working.room.UserData
import com.example.working.utils.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.parcelize.Parcelize
import javax.inject.Inject


class AdminRepository @Inject constructor(
    db: RoomDataBaseInstance
) {
    private val getUserDao = db.getDao()

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
                sourceId = source,
                date = getDateTime()
            )
            MySealed.Success(data)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun saveDownloadFile(userData: UserData) = flow {
        getUserDao.insert(userData)
        emit(MySealed.Success("${userData.fileInfo.fileName} Is Saved"))
    }.flowOn(IO)

    suspend fun deleteDownloadDB(userData: UserData) {
        getUserDao.delete(userData)
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
    }.flowOn(IO)

    fun addFirstSet(path: MyFilePath, materials: Materials) = flow {
        emit(MySealed.Loading("1 Set is Being Uploading"))
        val data = try {
            fireStore.collection(path.collection!!).document(path.document!!).set(materials).await()
            MySealed.Success(null)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun addSecondSet(path: List<MyFilePath>, allData: AllData) = flow {
        emit(MySealed.Loading("2 Set is Being Uploading"))
        val data = try {
            fireStore.collection(path.first().collection!!).document(path.first().document!!)
                .collection(path.last().collection!!).document(path.last().document!!)
                .set(allData).await()
            MySealed.Success("All File is Uploaded Successfully")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun updateSecondPath(path: List<MyFilePath>, allData: FileInfo) = flow {
        emit(MySealed.Loading("File Is Updating..."))
        val data = try {
            fireStore.collection(path.first().collection!!).document(path.first().document!!)
                .collection(path.last().collection!!).document(path.last().document!!)
                .update("map.${rand()}", allData).await()
            MySealed.Success("File is  Updated")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun addMoreSubject(path: MyFilePath, map: Map<String, SubjectInfo>) = flow {
        emit(MySealed.Loading("Adding More Subject"))
        val data = try {
            fireStore.collection(path.collection!!).document(path.document!!)
                .update("subject.${map.keys.first()}", map.values.first()).await()
            MySealed.Success("File Is Added")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun getAdminEmailId(adminQuery: DocumentReference) = flow {
        emit(MySealed.Loading(null))
        val data = try {
            val info = adminQuery.get().await()
            val email = info.get("email") as String?
            MySealed.Success(email)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun sendNotification(notifyInterface: NotifyInterface, mainObject: MainObject) = flow {
        emit(MySealed.Loading("Notification is in Process"))
        val data = try {
            val response = notifyInterface.sendNotification(mainObject = mainObject)
            val str = if (response.isSuccessful)
                "Success,Message Sent Successfully"
            else
                "Error,Message not Sent"

            MySealed.Success(str)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)
}

@IgnoreExtraProperties
@Parcelize
data class FileInfo(
    val fileSize: String? = null,
    val downloadUrl: String? = null,
    var folderPath: String? = null,
    val fileName: String? = null,
    var sourceId: String? = null,
    val date: String? = null,
    var localDownloadUrl: String? = null
) : Parcelable