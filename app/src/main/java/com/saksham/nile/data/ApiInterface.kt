package com.saksham.nile.data

import com.saksham.nile.data.model.SndNotification
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("/send-notification")
    suspend fun `send-notification`(@Body requestData: SndNotification)
}