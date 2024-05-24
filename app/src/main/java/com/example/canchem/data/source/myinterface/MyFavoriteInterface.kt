package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.FavoriteDataList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MyFavoriteInterface {
    @GET("api/search/bookmark") // 변경해야됨.
    fun getFavoriteInfo(
        @Header("Authorization") accessToken: String?,  // Baerer AccessToken
    ) : Call<FavoriteDataList>
}