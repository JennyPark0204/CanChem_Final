package com.example.canchem.ui.home

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.canchem.data.source.myinterface.Search.MoleculeApiService
import com.example.canchem.data.source.myinterface.BookMark.BookMarkService

object NetworkModule {
    private const val BASE_URL = "http://13.124.223.31:8080"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val moleculeApiService: MoleculeApiService = retrofit.create(MoleculeApiService::class.java)
    val bookMarkSevice: BookMarkService = retrofit.create(BookMarkService::class.java)
}