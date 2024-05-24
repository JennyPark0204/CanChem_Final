package com.example.canchem.data.source.myinterface

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

interface DeleteOneStarInterface {
    @DELETE("api/delete/bookmark/{id}")
    fun deleteStar(
        @Header("Authorization") accessToken: String?,  // Bearer AccessToken
        @Path("id") id: String
    ) : Call<String>
}