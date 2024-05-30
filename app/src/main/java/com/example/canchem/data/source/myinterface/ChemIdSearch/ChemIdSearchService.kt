package com.example.canchem.data.source.myinterface.ChemIdSearch

import com.example.canchem.data.source.dataclass.Search.ChemicalCompound
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ChemIdSearchService {
    @GET("/api/search/{moleculeId}")
    fun ChemIdSearch(@Header("Authorization") token:String, @Path("moleculeId") moleculeId: String): Call<ChemicalCompound>
}