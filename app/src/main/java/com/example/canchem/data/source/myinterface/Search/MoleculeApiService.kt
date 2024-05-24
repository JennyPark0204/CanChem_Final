package com.example.canchem.data.source.myinterface.Search

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import com.example.canchem.data.source.dataclass.Search.ChemicalCompoundResponse

interface MoleculeApiService {
    @GET("/api/search/chem")
    fun getCompounds(
        @Header("Authorization") token: String,
        @Query("keyword") searchQuery: String,
        @Query("page") page: Int
    ): Call<ChemicalCompoundResponse>
}