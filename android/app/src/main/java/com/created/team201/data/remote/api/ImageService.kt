package com.created.team201.data.remote.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {

    @POST("v1/images")
    suspend fun postImage(
        @Part imageUrl: MultipartBody.Part,
    ): Response<Unit>
}
