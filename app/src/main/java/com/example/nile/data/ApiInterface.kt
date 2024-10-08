package com.example.nile.data

import com.example.nile.data.model.SndNotification
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("/send-notification")
    suspend fun `send-notification`(@Body requestData: SndNotification)
}