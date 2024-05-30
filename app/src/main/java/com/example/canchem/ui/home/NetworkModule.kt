package com.example.canchem.ui.home


import com.example.canchem.data.source.myinterface.BookMark.BookmarkOffService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.canchem.data.source.myinterface.Search.MoleculeApiService
import com.example.canchem.data.source.myinterface.BookMark.BookmarkStateService
import com.example.canchem.data.source.myinterface.BookMark.BookmarkService
import com.example.canchem.data.source.myinterface.ChemIdSearch.ChemIdSearchService
import com.example.canchem.data.source.myinterface.SmilesSearch.SmilesSearchService
import retrofit2.create

object NetworkModule {
    private const val BASE_URL = "http://13.124.223.31:8080"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val moleculeApiService: MoleculeApiService = retrofit.create(MoleculeApiService::class.java)
    val bookmarkStateService: BookmarkStateService = retrofit.create(BookmarkStateService::class.java)
    val bookmarkService: BookmarkService = retrofit.create(BookmarkService::class.java)
    val bookmarkOffService: BookmarkOffService = retrofit.create(BookmarkOffService::class.java)
    var chemIdSearchService : ChemIdSearchService = retrofit.create(ChemIdSearchService::class.java)
}