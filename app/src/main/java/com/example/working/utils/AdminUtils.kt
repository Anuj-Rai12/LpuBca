package com.example.working.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import com.example.working.adminui.respotry.FileInfo
import com.google.firebase.firestore.IgnoreExtraProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.Parcelize
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@SuppressLint("SimpleDateFormat")
fun getDateTime(): String {
    val current = Date()
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    return formatter.format(current)
}

@IgnoreExtraProperties
@Parcelize
data class Materials(
    var id: String? = null,
    var udi: String? = null,
    val time: String? = null,
    val description: String? = null,
    val subject: Map<String, SubjectInfo>? = null
) : Parcelable

@IgnoreExtraProperties
@Parcelize
data class SubjectInfo(
    val subject: String? = null,
    val description: String? = null,
    val teacher: String? = null
) : Parcelable

@Parcelize
data class SubjectList(
    val subjectInfo: List<SubjectInfo>
) : Parcelable

@Parcelize
data class FileInfoList(
    val fileInfo: List<FileInfo>
) : Parcelable

@Parcelize
data class MyFilePath(
    val collection: String? = null,
    val document: String? = null
) : Parcelable

fun rand(from: Int = 1, to: Int = 10000000): Int {
    return Random.nextInt(to - from) + from
}

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

fun getPathFile(file: String): List<String> {
    val tagArray = file.split("\\s*,\\s*".toRegex()).toTypedArray()
    return tagArray.toList()
}

fun isDocFile(input: String): Boolean {
    val regex = Regex(pattern = "^[a-zA-Z]+\\.doc$", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}

fun isPngFile(input: String): Boolean {
    val regex = Regex(pattern = "^[a-zA-Z]+\\.png$", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}

fun isJpgFile(input: String): Boolean {
    val regex = Regex(pattern = "^[a-zA-Z]+\\.jpg$", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}

fun isPdfFile(input: String): Boolean {
    val regex = Regex(pattern = "^[a-zA-Z]+\\.pdf$", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}

fun isWebsiteFile(input: String): Boolean {
    val regex = Regex(pattern = "^[a-zA-Z]+\\.com$", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}
fun getFileDir(fileName:String,context: Context): File {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName)
    else
        File(
            Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DOWNLOADS),fileName)
}
const val SHARED_WEBSITE="Hey, Check this Interesting Website,"
const val SHARE_IMAGE="Hey,Check this Image,"
const val SHARE_PDF="Hey Check this PDF,"