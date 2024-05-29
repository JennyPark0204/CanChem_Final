package com.example.canchem.data.source.myinterface

import com.example.canchem.data.source.dataclass.SearchDataList
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Query

interface DeleteSelectHistoryInterface{

    @HTTP(method = "DELETE", path = "/api/edit/log/search", hasBody = true)
    fun deleteSearchLogs(
        @Header("Authorization") authToken: String,
        @Body searchDataList: SearchDataList
    ): Call<Void>
}
