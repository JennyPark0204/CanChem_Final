package com.example.canchem.data.source.myinterface.BookMark

import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.Call

interface BookmarkOffService {
    @DELETE("api/save/bookmark/{moleculeId}")
    fun BookmarkOff(@Header("Authorization") token:String, @Path("moleculeId") moleculeId: String):Call<Unit>
}