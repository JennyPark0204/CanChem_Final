package com.example.canchem.data.source.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.dataclass.SearchDataList
import com.example.canchem.data.source.myinterface.SearchHistoryInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GetSearchHistoryService(private val context: Context) {

    private val searchHistoryInterface: SearchHistoryInterface
    private val ip : String = "13.124.223.31"
    init {
        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/") // 서버의 base URL을 설정해주세요
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Retrofit을 사용하여 인터페이스 구현체 생성
        searchHistoryInterface = retrofit.create(SearchHistoryInterface::class.java)
    }

    fun getSearchHistory(accessToken: String, onResponse: (MutableList<SearchData>) -> Unit, onFailure: () -> Unit) {
        // 서버에서 데이터 가져오기
        val call = searchHistoryInterface.getSearchInfo(accessToken)
        call.enqueue(object : Callback<SearchDataList> {
            override fun onResponse(call: Call<SearchDataList>, response: Response<SearchDataList>) {
                if (response.isSuccessful) {
                    val searchDataList = response.body()?.searchList?.toMutableList()
                    if (!searchDataList.isNullOrEmpty()) {
                        onResponse(searchDataList)
                    } else {
                        onFailure()
                    }
                }
            }

            override fun onFailure(call: Call<SearchDataList>, t: Throwable) {
                // 서버 통신 실패 시 처리할 내용
                val errorMessage = "서버 통신 실패: ${t.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                Log.e("SearchHistoryService", errorMessage) // 실패한 이유를 로그로 출력
                onFailure()
            }
        })
    }

}
