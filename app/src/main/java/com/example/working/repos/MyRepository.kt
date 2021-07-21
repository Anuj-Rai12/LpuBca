package com.example.working.repos

import android.graphics.Bitmap
import com.example.working.adminui.EMAIL
import com.example.working.adminui.NAME
import com.example.working.adminui.SEMESTER
import com.example.working.utils.Convertor
import com.example.working.utils.MySealed
import com.example.working.utils.getPathFile
import com.example.working.utils.userchannel.FireBaseUser
import com.example.working.utils.userchannel.UserInfo1
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class MyRepository @Inject constructor() {
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val udi by lazy {
        FirebaseAuth.getInstance()
    }
    private val reference by lazy {
        fireStore.collection(USERS).document(udi.currentUser?.uid!!)
    }

    fun createUser(email: String, password: String) = flow {
        emit(MySealed.Loading("User Account is Been Created"))
        val data = try {
            udi.currentUser!!.updateEmail(email).await()
            udi.currentUser!!.updatePassword(password).await()
            MySealed.Success(null)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun getProfileInfo() = flow {
        emit(MySealed.Loading(null))
        val data = try {
            val info = reference.get().await()
            val get = info?.toObject(FireBaseUser::class.java)
            MySealed.Success(get)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun createAcc(icon: Bitmap, info1: UserInfo1) = flow {
        val byteArray = Convertor.covertImages2ByteArray(icon)
        val ico = Blob.fromBytes(byteArray!!)
        val userdata = FireBaseUser(
            firstname = info1.firstname,
            lastname = info1.lastname,
            gender = info1.gender,
            dob = info1.dob,
            semester = info1.semester,
            phone = info1.phone,
            email = info1.email,
            password = info1.password,
            icon = ico
        )
        emit(MySealed.Loading("User Profile is Been Updating"))
        val data = try {
            reference.set(userdata).await()
            MySealed.Success(null)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun signInAccount(email: String, password: String) = flow {
        emit(MySealed.Loading("User is Been Validated"))
        val data = try {
            udi.signInWithEmailAndPassword(email, password).await()
            MySealed.Success(null)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun getUpdate(getUpdateTask: Task<DocumentSnapshot>) = flow {
        emit(MySealed.Loading("Checking For Update"))
        val data = try {
            val user = getUpdateTask.await()
            if (user.exists()) {
                val info = user.toObject(Update::class.java)
                MySealed.Success(info)
            } else {
                MySealed.Success(Update(null, null))
            }
        } catch (e: Exception) {
            MySealed.Error(null, null)
        }
        emit(data)
    }.flowOn(IO)

    fun checkResourcesExitsOrNot(semesterNo: String) = flow {
        emit(MySealed.Loading(null))
        val data = try {
            val check = fireStore.collection(semesterNo).get().await()
            MySealed.Success(check.isEmpty)
        } catch (e: Exception) {
            MySealed.Error(null,e)
        }
        emit(data)
    }.flowOn(IO)

    fun passwordRestEmail(email: String) = flow {
        emit(MySealed.Loading("Checking Email address"))
        val data = try {
            udi.sendPasswordResetEmail(email).await()
            MySealed.Success("Link Is Sent To your Email Address ")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    private val msg = "\n\n" +
            "Tip:\n" +
            "If you Want to see the changes so,re-start this App :)"

    fun updateValue(semesterNo: String, fileName: String) = flow {
        emit(MySealed.Loading("$fileName is updating..."))
        val data = try {
            when (fileName) {
                SEMESTER -> reference.update(fileName, semesterNo).await()
                NAME -> {
                    fireStore.runBatch { writeBatch ->
                        val tags = getPathFile(semesterNo)
                        writeBatch.update(reference, "firstname", tags.first())
                        writeBatch.update(reference, "lastname", tags.last())
                    }.await()
                }
            }
            MySealed.Success("$fileName Is Updated Successfully.$msg")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)

    fun updateNewEmail(newEmail: String) = flow {
        emit(MySealed.Loading("Email is Updating.."))
        val data = try {
            udi.currentUser?.updateEmail(newEmail)?.await()
            reference.update(EMAIL, newEmail).await()
            MySealed.Success("Email Is Updated Successfully.$msg")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }.flowOn(IO)
}

data class Update(
    val download: String? = null,
    val version: Long? = null
)

const val USERS = "Users"
const val VERSION = "version"
const val VERSION_DOC = "check"