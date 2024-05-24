package com.example.canchem.data.source.myinterface

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header

interface DeleteAllMyFavoriteInterface {
    @DELETE("api/delete/all/bookmark")
    fun deleteAll(
        @Header("Authorization") accessToken: String?,  // Bearer AccessToken
    ) : Call<String>
}