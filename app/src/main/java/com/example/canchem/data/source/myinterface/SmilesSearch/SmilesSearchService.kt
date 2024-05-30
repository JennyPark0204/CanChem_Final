package com.example.canchem.data.source.myinterface.SmilesSearch

import com.example.canchem.data.source.dataclass.Search.ChemicalCompound
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SmilesSearchService {
    @GET("/api/search/chem")
    fun smilesSearch(
        @Header("Authorization") token: String,
        @Query("smiles") smiles: String,
    ): Call<ChemicalCompound>
}