package com.example.canchem.data.source.myinterface

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

interface DeleteAllSearchHistoryInterface {
    @DELETE("api/delete/all/log/search")
    fun deleteAll(
        @Header("Authorization") accessToken: String?,  // Bearer AccessToken
    ) : Call<String>
}