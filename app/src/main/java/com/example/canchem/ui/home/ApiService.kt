package com.example.canchem

import com.example.canchem.data.source.mol_Name
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoleculeApiService {
   @GET("/getMolInfo_Name")
   fun getCompound(@Query("mol_Name") searchQuery: String): Call<mol_Name>
}