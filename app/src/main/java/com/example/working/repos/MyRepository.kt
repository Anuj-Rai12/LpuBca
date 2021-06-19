package com.example.working.repos

import android.graphics.Bitmap
import com.example.working.utils.Convertor
import com.example.working.utils.MySealed
import com.example.working.utils.userchannel.FireBaseUser
import com.example.working.utils.userchannel.UserInfo1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


class MyRepository @Inject constructor() {
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val udi by lazy {
        FirebaseAuth.getInstance()
    }
    private val reference by lazy {
        fireStore.collection("Users").document(udi.currentUser?.uid!!)
    }

    private val update by lazy {
        fireStore.collection("version").document("check")
    }

    @Singleton
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
    }

    @Singleton
    fun getProfileInfo() = flow {
        emit(MySealed.Loading(null))
        val data = try {
            val info = reference.get().await()
            val get = info.toObject(FireBaseUser::class.java)
            MySealed.Success(get)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }

    @Singleton
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
    }

    @Singleton
    fun signInAccount(email: String, password: String) = flow {
        emit(MySealed.Loading("User is Been Validated"))
        val data = try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            MySealed.Success(null)
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }

    @Singleton
    fun getUpdate() = flow {
        emit(MySealed.Loading("Checking For Update"))
        val data = try {
            val user = update.get().await()
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
    }
    @Singleton
    fun passwordRestEmail(email: String) = flow {
        emit(MySealed.Loading("Checking Email address"))
        val data = try {
            udi.sendPasswordResetEmail(email).await()
            MySealed.Success("Link Is Sent To your Email Address ")
        } catch (e: Exception) {
            MySealed.Error(null, e)
        }
        emit(data)
    }
}

data class Update(
    val download: String? = null,
    val version: Long? = null
)

