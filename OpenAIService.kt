package com.example.memeapp

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {

    @POST("v1/videos")
    suspend fun createVideo(
        @Header("Authorization") auth: String,
        @Body body: VideoRequest
    ): VideoResponse
}
