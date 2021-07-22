package com.example.working.notfiy

import com.example.working.utils.AllMyConstant
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotifyInterface {
    @Headers(
        "Authorization: key=${AllMyConstant.API_KEY}",
        "Content-Type:${AllMyConstant.CONTENT_TYPE}"
    )

    @POST(AllMyConstant.URL_END_POINT)
    suspend fun sendNotification(
        @Body mainObject: MainObject
    ): Response<ResponseBody>
}