package com.example.canchem.data.source.myinterface.BookMark

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BookmarkService {
    @POST("api/save/bookmark/{moleculeId}")
    fun BookmarkSet(@Header("Authorization") token:String, @Path("moleculeId") moleculeId: String)
}