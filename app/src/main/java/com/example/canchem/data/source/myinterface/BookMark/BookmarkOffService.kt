package com.example.canchem.data.source.myinterface.BookMark

import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

interface BookmarkOffService {
    @DELETE("api/save/bookmark/{moleculeId}")
    fun BookmarkOff(@Header("Authorization") token:String, @Path("moleculeId") moleculeId: String)
}