package com.example.canchem.ui.main

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.canchem.R
import com.example.canchem.data.source.dataclass.NaverToken
import com.example.canchem.data.source.dataclass.Token
import com.example.canchem.data.source.myinterface.NaverLoginInterface
import com.example.canchem.data.source.myinterface.NaverLogoutInterface
import com.example.canchem.data.source.myinterface.NaverSignoutInterface
import com.example.canchem.data.source.util.JobSchedulerUtil
import com.example.canchem.data.source.util.UserId
import com.example.canchem.databinding.ActivityMainBinding
import com.example.canchem.ui.home.SearchActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {
    private val ip : String = "13.124.223.31"
    companion object{
        var mainActivity : MainActivity ?= null
    }
    // 버튼 두 번 클릭해야 나가지는거 구현
    private var backpressedTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JobSchedulerUtil.scheduleJob(this, (Math.random()*1000).toInt())
        /* View Binding 설정 */
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()  // 상태표시줄 투명하게 만듦
        // 만약 시작부터 로그인이 안 된다면, 밑의 주석을 풀고 실행후 다시 주석처리 후에 실행.
        clearLoginState()


        mainActivity = this

        val savedState = getSavedLoginState()

        // 다른 액티비티에서 intent로 회원탈퇴 하는 경우 판단.
        val extras = intent.extras
        if (extras?.getString("function") == "signout") {
            naverDeleteToken()
        }
        if (extras?.getString("function") == "logout") {
            Log.d("로그아웃", "으로 메인 넘어가긴했어")
            naverLogout()
        }
        if (savedState == "OK") {
            // 로그인 상태가 ok인 경우 처리
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        /* 네이버 로그인 버튼 클릭 */
        binding.btnNaverLogin.setOnClickListener {
            /* 네아로 SDK 객체 초기화 */
            val naverClientId = getString(R.string.naver_client_id) // 발급 받은 naver client id 값
            val naverClientSecret = getString(R.string.naver_client_secret) // 발급 받은 naver client secret 값
            val naverClientName = getString(R.string.naver_client_name) // 어플 이름
            NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)    // 네아로 객체 초기화
            /* 네이버 Access Token 받기 */
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    val accessToken = NaverIdLoginSDK.getAccessToken().toString()   // 접근 토큰
                    val refreshToken = NaverIdLoginSDK.getRefreshToken().toString() // 갱신 토큰
                    val expiresAt = NaverIdLoginSDK.getExpiresAt().toString()   // 만료 기한 (초)
                    val type = NaverIdLoginSDK.getTokenType().toString()    // 토큰 타입
                    val state = NaverIdLoginSDK.getState().toString()   // 로그인 인스턴트의 현재 상태
                    saveLoginState(state)
                    NidOAuthLogin().callProfileApi(nidProfileCallback)

                    Log.i("intent", intent.toString())
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    Log.d("버튼클릭", "1")
                    startActivity(intent)
                    finish()
                }
                override fun onFailure(httpStatus: Int, message: String) {
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }
            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)  // 토큰 가져오기
        }


    }
    private fun saveLoginState(state: String) {
        val sharedPref = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("login_state", state)
            apply()
        }
    }
    private fun getSavedLoginState(): String? {
        val sharedPref = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("login_state", null)
    }

    private fun clearLoginState() {
        val sharedPref = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("login_state")
            apply()
        }
    }
    val first : String = "Bearer"

    val database = Firebase.database
    val tokenInFirebase = database.getReference("Token")

    /* 네이버 사용자 정보 가져오기*/
    val nidProfileCallback = object : NidProfileCallback<NidProfileResponse> {
        override fun onSuccess(response: NidProfileResponse) {
            val userId = response.profile?.id   // 고유 아이디
            val email = response.profile?.email // 이메일
            val mobile = response.profile?.mobile   // 휴대폰 번호
            val nickname = response.profile?.nickname
            val name = response.profile?.name
            val gender = response.profile?.gender
            val profileImage = response.profile?.profileImage
            //test
            UserId.userId = userId
//            tokenInFirebase.child(userId!!).setValue(null)
            val naverUserInfo = NaverToken(userId, email, name, nickname, mobile, gender, profileImage)


            // retrofit 변수 생성
            val retrofit = Retrofit.Builder()
                .baseUrl("http://$ip:8080/")
                .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
                .build()

            // retrofit객체 생성
            val naverLoginService = retrofit.create(NaverLoginInterface::class.java)
            val call = naverLoginService.getLoginToken(naverUserInfo)

            Log.i("call", call.toString())
            call.enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) { //요청성공시
                    if (response.isSuccessful) {
                        tokenInFirebase.child(UserId.userId!!).setValue(first + " " + response.body()?.accessToken)
                            .addOnSuccessListener {
                                // 성공적으로 데이터를 설정한 경우
                                Log.d("Firebase", "Token successfully added.")
                                Log.d("버튼클릭", "2")
                            }
                            .addOnFailureListener { e ->
                                // 데이터를 설정하는 데 실패한 경우
                                Log.e("Firebase", "Failed to add token.", e)
                            }
                    } else {
                    }

                }

                override fun onFailure(call: Call<Token>, t: Throwable) { //요청실패시
                    Log.e("call error", t.toString())
                }
            })
        }
        override fun onFailure(httpState : Int, message: String) {
        }
        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode, message)
        }
    }


    /* 네이버 연동 해제 */
    internal fun naverDeleteToken() {
        clearLoginState()
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() {
                // 서버에서 토큰 삭제에 성공한 상태
                // 여기에 accessToken 담은 정보만 spring boot로 전달

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                val signoutService = retrofit.create(NaverSignoutInterface::class.java)
                Log.d("회원탈퇴", "눌림")
                tokenInFirebase.child(UserId.userId!!).addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.getValue().toString()
                        Log.d("회원탈퇴", "Value is는: " + value)
                        val call = signoutService.signout(value)
                        Log.i("call", call.toString())
                        call.enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) { // spring boot에 데이터 전송 성공시
                                if (response.isSuccessful) {
                                    tokenInFirebase.child(UserId.userId!!).removeValue()
                                        .addOnSuccessListener {
                                            // 성공적으로 데이터를 삭제한 경우
                                            Log.d("Firebase", "Token successfully deleted.")
                                        }
                                        .addOnFailureListener { e ->
                                            // 데이터를 삭제하는 데 실패한 경우
                                            Log.e("Firebase", "Failed to delete token.", e)
                                        }

                                    Toast.makeText(this@MainActivity, "회원탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e(TAG, "Response unsuccessful: ${response.code()}")
                                }
                            }
                            override fun onFailure(call: Call<String>, t: Throwable) { //spring boot에 데이터 전송 실패시
                                Log.e("call error", t.toString())
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                })
            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                Log.e(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.e(TAG, "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없음
                onFailure(errorCode, message)
            }
        })
    }
    /* 네이버 로그아웃 */
    private fun naverLogout() {
        Log.d("로그아웃", "으로 네이버 로그아웃")
        NaverIdLoginSDK.logout()
        clearLoginState()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
            .build()

        val logoutService = retrofit.create(NaverLogoutInterface::class.java)

        tokenInFirebase.child(UserId.userId!!).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue().toString()
                Log.d("로그아웃", "Value is: " + value)
                val call = logoutService.logout(value)
                Log.i("call", call.toString())
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) { // spring boot에 데이터 전송 성공시
                        if (response.isSuccessful) {
                            tokenInFirebase.child(UserId.userId!!).removeValue()
                                .addOnSuccessListener {
                                    // 성공적으로 데이터를 삭제한 경우
                                    Log.d("Firebase", "Token successfully deleted.")
                                }
                                .addOnFailureListener { e ->
                                    // 데이터를 삭제하는 데 실패한 경우
                                    Log.e("Firebase", "Failed to delete token.", e)
                                }
                            Toast.makeText(this@MainActivity, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "Response unsuccessful: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) { //spring boot에 데이터 전송 실패시
                        Log.e("call error", t.toString())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish()
        }
    }
}