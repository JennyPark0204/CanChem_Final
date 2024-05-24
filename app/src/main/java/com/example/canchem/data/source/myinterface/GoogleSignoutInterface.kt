package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.Token
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header

interface GoogleSignoutInterface {
    @DELETE("api/logout")
    fun signout(
        @Header("Authorization") accessToken: String?,  // Bearer AccessToken
    ) : Call<Token>
}