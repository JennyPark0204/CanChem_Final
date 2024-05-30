package com.example.canchem.data.source.myinterface.Search

import com.example.canchem.data.source.dataclass.Search.ChemicalCompound
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageUploadService {
    @Multipart
    @POST("/predict/")
    fun uploadImage(
        @Header("token") token: String,
        @Part image: MultipartBody.Part
    ): Call<ChemicalCompound>
}
