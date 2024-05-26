package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.RefreshToken
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.PATCH

interface RefreshTokenInterface {
    @PATCH("api/login/reissue")
    fun getRefreshToken(
        @Header("Authorization") accessToken: String?,  // Baerer AccessToken
    ) : Call<RefreshToken>
}