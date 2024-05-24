package com.example.canchem.data.source.myinterface.Search

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImageUploadService {
    @Multipart
    @POST("/predict/")
    fun uploadImage(
        @Query("token") token: String,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>
}