package com.example.working.utils.userchannel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo1(
    val firstname:String,
    val lastname:String,
    val semester:String,
    val email:String,
    val password:String,
    val icon:ByteArray
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
        if (!icon.contentEquals(other.icon)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + semester.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + icon.contentHashCode()
        return result
    }
}
