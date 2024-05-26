package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.newRefreshToken
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PATCH

interface NewRefreshTokenInterface {
    @PATCH("api/login/google")
    fun getRefreshToken(
        @Header("Authorization") accessToken: String?,  // Baerer AccessToken
    ) : Call<newRefreshToken>
}