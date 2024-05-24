package com.example.canchem.data.source.adapter

import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor : Interceptor { //인터셉터. 아마 안쓸듯?
    override fun intercept(chain: Interceptor.Chain): Response {
//        //JWT 가져오기
//        var token = TOKEN
        var token = 2 //아무값






        val request = chain.request()
            .newBuilder()
            .addHeader("JWT", "$token")
            .build()
        val response = chain.proceed(request)

        return response
    }

}