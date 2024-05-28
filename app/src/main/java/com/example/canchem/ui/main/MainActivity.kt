package com.example.canchem.ui.main

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.canchem.R
import com.example.canchem.data.source.dataclass.DeleteToken
import com.example.canchem.data.source.myinterface.NaverLoginInterface
import com.example.canchem.data.source.myinterface.NaverLogoutInterface
import com.example.canchem.data.source.myinterface.NaverSignoutInterface
import com.example.canchem.data.source.dataclass.NaverToken
import com.example.canchem.data.source.dataclass.Token
import com.example.canchem.data.source.util.JobSchedulerUtil
import com.example.canchem.data.source.util.UserId
import com.example.canchem.databinding.ActivityMainBinding
import com.example.canchem.ui.home.SearchActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.example.canchem.ui.test.YeonjeTestActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
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
    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JobSchedulerUtil.scheduleJob(this, (Math.random()*1000).toInt())
        /* View Binding 설정 */
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()  // 상태표시줄 투명하게 만듦
        // 만약 시작부터 로그인이 안 된다면, 밑의 주석을 풀고 실행후 다시 주석처리 후에 실행.
//        clearLoginState()

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
            val intent = Intent(this@MainActivity, MyFavoriteActivity::class.java)
            startActivity(intent)
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
                    val intent = Intent(this@MainActivity, YeonjeTestActivity::class.java)
                    Log.d("yeonje액티비티", "로 ㄱㄱ")
                    startActivity(intent)
                }
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(this@MainActivity,"errorCode:$errorCode, errorDesc:$errorDescription",Toast.LENGTH_SHORT).show()
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

    //firebase DB
//    val firebaseUsers = FirebaseDatabase.getInstance().getReference("Users")
//    val firebaseUsersToken = firebaseUsers.child("Token")
    val database = Firebase.database
    val tokenInFirebase = database.getReference("Token")


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
    }

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
                        Toast.makeText(this@MainActivity, "네이버 로그인 성공\n" +
                                "user id : ${userId}\n" +
                                "email: ${email}\n" +
                                "mobile : ${mobile}\n" +
                                "nickname: ${nickname}\n" +
                                "name: ${name}\n" +
                                "gender: ${gender}\n" +
                                "profileImage: ${profileImage}\n"+
                                "accessToken: ${response.body()?.accessToken}"
                            , Toast.LENGTH_SHORT).show()
                        tokenInFirebase.child(UserId.userId!!).setValue(first + " " + response.body()?.accessToken)
                            .addOnSuccessListener {
                                // 성공적으로 데이터를 설정한 경우
                                Log.d("Firebase", "Token successfully added.")
                            }
                            .addOnFailureListener { e ->
                                // 데이터를 설정하는 데 실패한 경우
                                Log.e("Firebase", "Failed to add token.", e)
                            }
//                        tokenInFirebase.setValue(first + " " + response.body()?.accessToken) //firebase DB에 accessToken값 저장
                    } else {
                        Toast.makeText(this@MainActivity, "네이버 사용자 정보 실패", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<Token>, t: Throwable) { //요청실패시
                    Toast.makeText(this@MainActivity, "아예 실패", Toast.LENGTH_SHORT).show()
                    Log.e("call error", t.toString())
                }
            })
            // Read from the database 양식?
            tokenInFirebase.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = snapshot.getValue().toString()
//                    Toast.makeText(this@MainActivity, value, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Value is: " + value)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        override fun onFailure(httpState : Int, message: String) {
            val errorCode = NaverIdLoginSDK.getLastErrorCode().code
            val errorDesc = NaverIdLoginSDK.getLastErrorDescription()

            Toast.makeText(this@MainActivity, "네이버 로그인 실패\n" +
                    "Error Code : ${errorCode}\n" +
                    "Error Description : ${errorDesc}", Toast.LENGTH_SHORT).show()
        }
        override fun onError(errorCode: Int, message: String) {
            onFailure(errorCode, message)
        }
    }


    /* 네이버 연동 해제 */ //여기 수정했다 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val value = snapshot.getValue().toString()
                        Toast.makeText(this@MainActivity, value, Toast.LENGTH_LONG).show()
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
                                            val intent = Intent(this@MainActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener { e ->
                                            // 데이터를 삭제하는 데 실패한 경우
                                            Log.e("Firebase", "Failed to delete token.", e)
                                        }

                                    Toast.makeText(this@MainActivity, "회원탈퇴 성공", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e(TAG, "Response unsuccessful: ${response.code()}")
                                    Toast.makeText(this@MainActivity, "회원탈퇴에서 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<String>, t: Throwable) { //spring boot에 데이터 전송 실패시
                                Toast.makeText(this@MainActivity, "네이버 연동 해제 데이터 전송 실패", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this@MainActivity, "로그아웃이지롱", Toast.LENGTH_LONG).show()
        NaverIdLoginSDK.logout()
        clearLoginState()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
            .build()

        val logoutService = retrofit.create(NaverLogoutInterface::class.java)

        tokenInFirebase.child(UserId.userId!!).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue().toString()
                Toast.makeText(this@MainActivity, value, Toast.LENGTH_LONG).show()
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
                                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // 데이터를 삭제하는 데 실패한 경우
                                    Log.e("Firebase", "Failed to delete token.", e)
                                }
                            Toast.makeText(this@MainActivity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "Response unsuccessful: ${response.code()}")
                            Toast.makeText(this@MainActivity, "로그아웃에서 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) { //spring boot에 데이터 전송 실패시
                        Toast.makeText(this@MainActivity, "네이버 로그아웃 데이터 전송 실패", Toast.LENGTH_SHORT).show()
                        Log.e("call error", t.toString())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }
}