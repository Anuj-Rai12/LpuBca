package com.example.working.utils.userchannel

import android.os.Parcelable
import com.google.firebase.firestore.Blob
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo1(
    val firstname:String?=null,
    val lastname:String?=null,
    val gender:String?=null,
    val dob:String?=null,
    val semester:String?=null,
    val phone:String?=null,
    val email:String?=null,
    val password:String?=null,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInfo1

        if (firstname != other.firstname) return false
        if (lastname != other.lastname) return false
        if (semester != other.semester) return false
        if (email != other.email) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + semester.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }
}
data class FireBaseUser(
    val firstname:String?=null,
    val lastname:String?=null,
    val gender:String?=null,
    val dob:String?=null,
    val semester:String?=null,
    val phone:String?=null,
    val email:String?=null,
    val password:String?=null,
    val icon:Blob?=null,
)