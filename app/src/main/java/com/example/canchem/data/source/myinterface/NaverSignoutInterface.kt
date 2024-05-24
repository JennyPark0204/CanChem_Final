package com.example.canchem.data.source.myinterface

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header

interface NaverSignoutInterface {
    @DELETE("api/withdraw")
    fun signout(
        @Header("Authorization") accessToken: String?,  // Baerer AccessToken
    ) : Call<String>
}