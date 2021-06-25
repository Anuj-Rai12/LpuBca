package com.example.working.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
fun getDateTime(): String {
    val current = Date()
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    return formatter.format(current)
}

@IgnoreExtraProperties
@Parcelize
data class Materials(
    val udi: String? = null,
    val time: String? = null,
    val description: String? = null
) : Parcelable

@Parcelize
data class MyFilePath(
    val collection: String? = null,
    val document: String? = null
) : Parcelable


class CustomProgress @Inject constructor(private val customProgressBar: CustomProgressBar) {
    fun hideLoading(context: Context) {
        val con = context as Activity
        con.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        customProgressBar.dismiss()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun showLoading(context: Context, string: String?, boolean: Boolean = false) {
        val con = context as Activity
        con.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        customProgressBar.show(con, string, boolean)
    }
}