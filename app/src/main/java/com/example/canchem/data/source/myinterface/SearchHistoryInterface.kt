package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.SearchDataList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface SearchHistoryInterface {
    @GET("api/get/log/search")
    fun getSearchInfo(
        @Header("Authorization") accessToken: String?,  // Bearer AccessToken
    ) : Call<SearchDataList>
}