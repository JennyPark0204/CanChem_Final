package com.example.canchem.data.source.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.example.canchem.data.source.dataclass.RefreshToken
import com.example.canchem.data.source.myinterface.RefreshTokenInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenRefreshJobService : JobService() {
    private val ip : String = "13.124.223.31"
    override fun onStartJob(params: JobParameters?): Boolean {
        refreshAccessToken(params)
        return true // 작업이 비동기적으로 수행됨을 나타냄
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true // 작업을 다시 시도할 필요가 있음을 나타냄
    }

    private fun refreshAccessToken(params: JobParameters?) {
        Log.d("잡스케줄러 눌리긴","함")
        var refreshToken: String? = null
        var accessToken : String?

        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
            .build()
//        val refreshTokenService= retrofit.create(NewRefreshTokenInterface::class.java)

        val first : String = "Bearer"
        val database = Firebase.database
        val tokenInFirebase = database.getReference("Token")
        tokenInFirebase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accessToken = snapshot.getValue().toString()
                val refreshTokenService = retrofit.create(RefreshTokenInterface::class.java)
                val call = refreshTokenService.getRefreshToken(accessToken)
                call.enqueue(object : Callback<RefreshToken> {
                    override fun onResponse(
                        call: Call<RefreshToken>,
                        response: Response<RefreshToken>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("잡스케줄러 눌리긴", "함4 통신성공")
                            refreshToken = response.body()?.accessToken
                            tokenInFirebase.setValue(first + " " + refreshToken)

                            Log.d("통신이 돼서", "토큰 갱신 성공")
                        } else {
                            Log.d("잡스케줄러 눌리긴", "함 통신실패")
                        }
                        jobFinished(params, true)
                    }

                    override fun onFailure(call: Call<RefreshToken>, t: Throwable) {
                        Log.d("통신이 안돼서", "토큰 갱신 실패", t)
                        jobFinished(params, true)
                    }
                })

            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("잡스케줄러 눌리긴","했지만 파이어베이스 실패")
            }
        })
    }

//    companion object {
//        private const val TAG = "TokenRefreshJobService"
//    }


}