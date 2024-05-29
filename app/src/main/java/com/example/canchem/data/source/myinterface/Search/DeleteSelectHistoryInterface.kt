package com.example.canchem.data.source.myinterface.Search

import com.example.canchem.data.source.dataclass.SearchDataList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface DeleteSelectHistoryInterface {

    @DELETE("edit/log/search")
    suspend fun deleteSelectedSearchLogs(
        @Header("Authorization") token: String,
        @Body searchLogs: SearchDataList
    ): Call<Void>
}
