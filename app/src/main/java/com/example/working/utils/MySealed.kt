package com.example.working.utils


import java.lang.Exception

sealed class MySealed<T>(
    val data: T? = null,
    val exception: Exception? = null
) {
    class Loading<T>(data: T?) : MySealed<T>(data)
    class Success<T>(data: T) : MySealed<T>(data)
    class Error<T>(data: T? = null, exception: Exception?) : MySealed<T>(data, exception)
}

sealed class MySealedChannel {
    data class DeleteAndChannel<T>(val userdata: T) : MySealedChannel()
}