package com.example.working.utils

import com.example.working.room.UserData
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
    data class DeleteAndChannel(val userData: UserData) : MySealedChannel()
}