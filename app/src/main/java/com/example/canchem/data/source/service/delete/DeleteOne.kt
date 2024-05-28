package com.example.canchem.data.source.service.delete

import android.content.Context
import android.widget.Toast
import com.example.canchem.data.source.myinterface.DeleteOneSearchHistoryInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DeleteOne(private val context: Context) {

    private val deleteOneInterface: DeleteOneSearchHistoryInterface
    private val baseUrl = "http://13.124.223.31:8080/"

    init {
        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Retrofit을 사용하여 인터페이스 구현체 생성
        deleteOneInterface = retrofit.create(DeleteOneSearchHistoryInterface::class.java)
    }

    fun deleteSearchLog(token: String, logId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // 서버에 DELETE 요청 보내기
        val call = deleteOneInterface.deleteSearchHistory(token, logId)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure("검색 기록 삭제에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure("서버 통신 실패: ${t.message}")
            }
        })
    }
}
