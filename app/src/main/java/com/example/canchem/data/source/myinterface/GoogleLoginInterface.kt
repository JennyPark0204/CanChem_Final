package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.GoogleToken
import com.example.canchem.data.source.dataclass.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GoogleLoginInterface {
    @POST("api/login/google")
    fun getLoginToken(
        //@Header("Authorization") accessToken: String?,  // Baerer AccessToken
        @Body googleToken: GoogleToken
    ) : Call<Token>
}