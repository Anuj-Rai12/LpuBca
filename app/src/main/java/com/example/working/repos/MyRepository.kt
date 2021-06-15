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

    fun createUser(email:String,password:String) =flow{
        emit(MySealed.Loading("User Account is Been Created"))
        val data=try {
            udi.currentUser!!.updateEmail(email).await()
            udi.currentUser!!.updatePassword(password).await()
            MySealed.Success(SUCCESS)
        }catch (e:Exception){
            MySealed.Error(null,e)
        }
        emit(data)
        }
        fun createAcc(icon: Bitmap, info1: UserInfo1) = flow {
            val byteArray=Convertor.covertImages2ByteArray(icon)
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

            emit(MySealed.Loading("User account is Been Created"))
            val data = try {
                reference.set(userdata).await()
                MySealed.Success(SUCCESS)
            } catch (e: Exception) {
                MySealed.Error(null, e)
            }
            emit(data)
        }

        fun signInAccount(email: String, password: String) = flow {
            emit(MySealed.Loading("User is Been Validated"))
            val data = try {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                MySealed.Success(SUCCESS)
            } catch (e: Exception) {
                MySealed.Error(null, e)
            }
            emit(data)
        }
    }

    const val SUCCESS = "Account is Created Successfully"