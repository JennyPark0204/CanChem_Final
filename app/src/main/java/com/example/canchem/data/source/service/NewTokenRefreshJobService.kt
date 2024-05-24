package com.example.canchem.data.source.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import com.example.canchem.data.source.dataclass.newRefreshToken
import com.example.canchem.data.source.myinterface.NewRefreshTokenInterface
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class NewTokenRefreshJobService : JobService() {
    private val ip : String = "13.124.223.31"
    override fun onStartJob(params: JobParameters?): Boolean {
        refreshAccessToken(params)
        return true // 작업이 비동기적으로 수행됨을 나타냄
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true // 작업을 다시 시도할 필요가 있음을 나타냄
    }

    private fun refreshAccessToken(params: JobParameters?) {
        var refreshToken: String? = null
        var accessToken : String?

        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
            .build()
        val refreshTokenService= retrofit.create(NewRefreshTokenInterface::class.java)

        val first : String = "Bearer"
        val database = Firebase.database
        val tokenInFirebase = database.getReference("Token")
        tokenInFirebase.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accessToken = snapshot.getValue().toString()
                val call = refreshTokenService.getRefreshToken(accessToken)
                call.enqueue(object : Callback<newRefreshToken> {
                    override fun onResponse(call: Call<newRefreshToken>, response: Response<newRefreshToken>) {
                        if (response.isSuccessful) {
                            refreshToken = response.body()?.refreshToken
                            tokenInFirebase.setValue(first + " " + refreshToken)
//                        val newAccessToken = response.body()?.accessToken
//                        val newRefreshToken = response.body()?.refreshToken
//
//                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
//                        editor.putString("access_token", newAccessToken)
//                        editor.putString("refresh_token", newRefreshToken)
//                        editor.apply()

                            Log.d(TAG, "토큰 갱신 성공")
                        } else {
                            Log.e(TAG, "토큰 갱신 실패: ${response.message()}")
                        }
                        jobFinished(params, !response.isSuccessful)
                    }

                    override fun onFailure(call: Call<newRefreshToken>, t: Throwable) {
                        Log.e(TAG, "토큰 갱신 실패", t)
                        jobFinished(params, true)
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

//    companion object {
//        private const val TAG = "TokenRefreshJobService"
//    }


}