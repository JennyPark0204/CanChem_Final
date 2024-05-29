package com.example.canchem.data.source.service.delete

import android.content.Context
import android.util.Log
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.dataclass.SearchDataList
import com.example.canchem.data.source.myinterface.DeleteSelectHistoryInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class DeleteSelect(private val context: Context) {

    private val deleteSelected: DeleteSelectHistoryInterface
    private val baseUrl = "http://13.124.223.31:8080"

    init {
        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        // Retrofit을 사용하여 인터페이스 구현체 생성
        deleteSelected = retrofit.create(DeleteSelectHistoryInterface::class.java)
    }

    fun deleteSearchLog(token: String, searchDataList: List<SearchData>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        // 서버에 DELETE 요청 보내기
        val call = deleteSelected.deleteSearchLogs(token, SearchDataList(ArrayList(searchDataList)))
        Log.d("call 성공", searchDataList.toString())
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                    Log.e("검색 성공", response.toString())
                } else {
                    onFailure("검색 기록 삭제에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure("서버 통신 실패: ${t.message}")
                Log.d("서버 통신 오류 : ",  "${t.message}")
            }
        })
    }
}
