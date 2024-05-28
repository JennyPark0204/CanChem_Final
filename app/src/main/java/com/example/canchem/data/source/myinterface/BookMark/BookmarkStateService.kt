package com.example.canchem.data.source.myinterface.BookMark

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import com.example.canchem.data.source.dataclass.BookMark.BookmarkState

interface BookmarkStateService {
    @GET("api/check/bookmark/{moleculeId}")
    fun getBookmark(@Header("Authorization") token:String, @Path("moleculeId") moleculeId: String): Call<BookmarkState>
}