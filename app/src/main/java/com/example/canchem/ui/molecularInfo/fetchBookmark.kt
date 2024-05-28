package com.example.canchem.ui.molecularInfo

import android.content.Context
import android.widget.Toast
import com.example.canchem.ui.home.NetworkModule
import com.example.canchem.ui.home.getToken
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Call

fun fetchBookmark(context: Context,moleculeId: String){
    val bookmarkService = NetworkModule.bookmarkService
    getToken(context){ token ->
        if(token!=null){
            val call = bookmarkService.BookmarkSet(token, moleculeId)
            call.enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>){
                    if (response.isSuccessful) {
                        Toast.makeText(context, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "즐겨찾기 추가에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call:Call<Unit>, t:Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}