package com.example.canchem.ui.molecularInfo

import android.content.Context
import android.widget.Toast
import com.example.canchem.ui.home.NetworkModule
import com.example.canchem.ui.home.getToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchBookmarkOff(context: Context,moleculeId: String){
    val bookmarkOffService = NetworkModule.bookmarkOffService
    getToken(context){ token ->
        if(token!=null){
            val call = bookmarkOffService.BookmarkOff(token, moleculeId)
            call.enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>){
                    if (response.isSuccessful) {

                    } else {
                        Toast.makeText(context, "즐겨찾기 해제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call:Call<Unit>, t:Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}