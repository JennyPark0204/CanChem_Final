package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.DeleteToken
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header

interface NaverLogoutInterface {
    @DELETE("api/logout")
    fun logout(
        @Header("Authorization") accessToken: String?,  // Baerer AccessToken
    ) : Call<String>
}