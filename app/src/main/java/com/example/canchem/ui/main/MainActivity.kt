package com.example.canchem.ui.main

import android.content.ContentValues.TAG
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
import com.example.canchem.databinding.ActivityMainBinding
import com.example.canchem.ui.test.YeonjeTestActivity
import com.example.canchem.ui.webview.WebViewActivity
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

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JobSchedulerUtil.scheduleJob(this, (Math.random()*1000).toInt())
        /* View Binding 설정 */
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        enableEdgeToEdge()  // 상태표시줄 투명하게 만듦

        // 다른 액티비티에서 intent로 회원탈퇴 하는 경우 판단.
        val extras = intent.extras
        if (extras?.getString("function") == "signout") {
            naverDeleteToken()
        }
        if (extras?.getString("function") == "logout") {
            naverLogout()
        }


        mAuth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
        } else {
            // Handle the case where the user is not signed in
        }

        /* 네아로 SDK 객체 초기화 */
        val naverClientId = getString(R.string.naver_client_id) // 발급 받은 naver client id 값
        val naverClientSecret = getString(R.string.naver_client_secret) // 발급 받은 naver client secret 값
        val naverClientName = getString(R.string.naver_client_name) // 어플 이름
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)    // 네아로 객체 초기화

//        googleLogInCheck()  // 구글 아이디로 로그인 되어 있는지 확인
//        googleAuthLauncher()    // Google 로그인 초기화
//        googleSignInClient = getGoogleClient()  // Google Client 초기화


        /* Google 로그인 버튼 클릭*/
//        binding.btnGoogleLogin.setOnClickListener {
////            resultLauncher.launch(googleSignInClient.getSignInIntent())  // 구글 로그인 창으로 넘어감
//            googleSignIn()
//        }

        /* 네이버 로그인 버튼 클릭 */
        binding.btnNaverLogin.setOnClickListener {

            /* 네이버 Access Token 받기 */
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    val accessToken = NaverIdLoginSDK.getAccessToken().toString()   // 접근 토큰
                    val refreshToken = NaverIdLoginSDK.getRefreshToken().toString() // 갱신 토큰
                    val expiresAt = NaverIdLoginSDK.getExpiresAt().toString()   // 만료 기한 (초)
                    val type = NaverIdLoginSDK.getTokenType().toString()    // 토큰 타입
                    val state = NaverIdLoginSDK.getState().toString()   // 로그인 인스턴트의 현재 상태

                    NidOAuthLogin().callProfileApi(nidProfileCallback)

                    Log.i("intent", intent.toString())
                    val intent = Intent(this@MainActivity, WebViewActivity::class.java)
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

//        /* 로그아웃 버튼 클릭 */
//        binding.btnNaverLogout.setOnClickListener {
//            naverLogout()   // 네이버 로그아웃
////            googleLogout()  // 구글 로그아웃
//        }

    }

    val first : String = "Bearer"
//    lateinit var googleSignInClient : GoogleSignInClient
//    lateinit var resultLauncher : ActivityResultLauncher<Intent>

    //firebase DB
//    val firebaseUsers = FirebaseDatabase.getInstance().getReference("Users")
//    val firebaseUsersToken = firebaseUsers.child("Token")
    val database = Firebase.database
    val tokenInFirebase = database.getReference("Token")

//    override fun onStart() {
//        super.onStart()
//
//        googleLogInCheck()  // 구글 아이디로 로그인 되어 있는지 확인
//    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    //구글 로그인
    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
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
                        tokenInFirebase.setValue(first + " " + response.body()?.accessToken) //firebase DB에 accessToken값 저장
                    } else {
                        Toast.makeText(this@MainActivity, "실패", Toast.LENGTH_SHORT).show()
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
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() {
                // 서버에서 토큰 삭제에 성공한 상태
                // 여기에 accessToken 담은 정보만 spring boot로 전달

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                val signoutService = retrofit.create(NaverSignoutInterface::class.java)

                tokenInFirebase.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val value = snapshot.getValue().toString()
                        Toast.makeText(this@MainActivity, value, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Value is: " + value)
                        val call = signoutService.signout(value)
                        Log.i("call", call.toString())
                        call.enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) { // spring boot에 데이터 전송 성공시
                                if (response.isSuccessful) {
                                    Toast.makeText(this@MainActivity, "회원탈퇴 성공", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e(TAG, "Response unsuccessful: ${response.code()}")
                                    Toast.makeText(this@MainActivity, "실패", Toast.LENGTH_SHORT).show()
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
                tokenInFirebase.setValue("failToDelete") //firebase DB의 accessToken에 null값 저장
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
        NaverIdLoginSDK.logout()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
            .build()

        val logoutService = retrofit.create(NaverLogoutInterface::class.java)

        tokenInFirebase.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue().toString()
                Toast.makeText(this@MainActivity, value, Toast.LENGTH_LONG).show()
                Log.d(TAG, "Value is: " + value)
                val call = logoutService.logout(value)
                Log.i("call", call.toString())
                call.enqueue(object : Callback<DeleteToken> {
                    override fun onResponse(call: Call<DeleteToken>, response: Response<DeleteToken>) { // spring boot에 데이터 전송 성공시
                        if (response.isSuccessful) {
                            tokenInFirebase.setValue("logout") //firebase DB의 accessToken에 null값 저장
                            Toast.makeText(this@MainActivity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "Response unsuccessful: ${response.code()}")
                            Toast.makeText(this@MainActivity, "실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<DeleteToken>, t: Throwable) { //spring boot에 데이터 전송 실패시
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


    /* Google Client 초기화 */
//    private fun getGoogleClient() : GoogleSignInClient {
//        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            //.requestIdToken(getString(R.string.google_client_id))   // Id Token 값 요청
//            .requestEmail() // Email 요청
//            .requestProfile()   // 프로필 정보 요청
//            .build()
//
//        Log.i("Request to Google", "Send Success")
//
//        return GoogleSignIn.getClient(this@MainActivity, googleSignInOptions)
//    }

    /* Google 로그인 초기화 */
//    private fun googleAuthLauncher() {
//        Toast.makeText(this@MainActivity, "구글 로그인 초기화", Toast.LENGTH_SHORT).show()
//        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                Toast.makeText(this@MainActivity, "구글 로그인 RESULT_OK", Toast.LENGTH_SHORT).show()
//                val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//
//                handleSignInResult(task)    // Google 사용자 정보 받아오기
//            } else if (result.resultCode == Activity.RESULT_CANCELED) {
//                // 사용자가 로그인을 취소한 경우
//                Toast.makeText(this@MainActivity, "Google Sign-In canceled by user", Toast.LENGTH_SHORT).show()
//                Log.e(TAG, "Google Sign-In canceled by user")
//            } else {
//                Log.e(TAG, "Google Sign-In failed with resultCode: ${result.resultCode}")
//                Toast.makeText(this@MainActivity, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    /* Google 사용자 정보 받아오기 */
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//
//            val userId = account?.id // 식별 ID 값
//            val email = account?.email // Email
//            val name = account?.displayName // 이름
////            val token = account?.idToken // 토큰값
//
//            val googleUserInfo = GoogleToken(userId, email, name) //dto와 다름
//
////            Toast.makeText(this, "구글 로그인 성공\n" +
////                    "User Id : ${userId}\n" +
////                    "Email : ${email}\n" +
////                    "Full Name : ${name}\n" +
////                    "Token : ${token}", Toast.LENGTH_SHORT).show()
//
//            Toast.makeText(this, "구글 로그인 성공\n" +
//                    "User Id : ${userId}\n" +
//                    "Email : ${email}\n" +
//                    "Full Name : ${name}\n", Toast.LENGTH_SHORT).show()
//
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://$ip:8080/")
//                .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
//                .build()
//
//            val googleLoginService = retrofit.create(GoogleLoginInterface::class.java)
//            val call = googleLoginService.getLoginToken(googleUserInfo)
//            Log.i("call", call.toString())
//            call.enqueue(object : Callback<Token> {
//                override fun onResponse(call: Call<Token>, response: Response<Token>) { //요청성공시
//                    if (response.isSuccessful) {
////                        Toast.makeText(this@MainActivity, "성공", Toast.LENGTH_SHORT).show()
//                        Toast.makeText(this@MainActivity, "네이버 로그인 성공\n" +
//                                "user id : ${userId}\n" +
//                                "email: ${email}\n" +
//                                "name: ${name}\n" +
//                                "accessToken: ${response.body()?.accessToken}"
//                            , Toast.LENGTH_SHORT).show()
//                        tokenInFirebase.setValue(first + " "+ response.body()?.accessToken) //firebase DB에 accessToken값 저장
//                    } else {
//                        Toast.makeText(this@MainActivity, "실패", Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//
//                override fun onFailure(call: Call<Token>, t: Throwable) { //요청실패시
//                    Toast.makeText(this@MainActivity, "아예 실패", Toast.LENGTH_SHORT).show()
//                    Log.e("call error", t.toString())
//                }
//            })
//
//            Log.i("LogIn", "구글 로그인 성공")
////            Log.i("Token", token.toString())
//        } catch (e: ApiException) {
//            Toast.makeText(this@MainActivity, "구글 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
//            Log.e("GoogleSignIn", "구글 로그인 실패: ${e.message}")
//        }
//    }


    /* Google 로그아웃 */
//    private fun googleLogout() { //로그아웃.setOnClickListener 눌리면 로그아웃되게 하는 메소드? 로그인화면으로 이동시켜야겠네
//    mAuth.signOut()
//        googleSignInClient.signOut() //이게 뭐징?
//        tokenInFirebase.setValue("logout") //firebase DB의 accessToken에 null값 저장
//        Toast.makeText(this@MainActivity, "구글 로그아웃 성공", Toast.LENGTH_SHORT).show()
//    }

    /* Google 연동 해제 */
    // 여기도 수정했다~~~~~~~~~~~~~~~~~~~
//    private fun googleDeleteToken() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://$ip:8080/")
//            .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
//            .build()
//
//        val signoutService = retrofit.create(GoogleSignoutInterface::class.java)
//        val call = signoutService.signout(tokenInFirebase.get().toString())
//        Log.i("call", call.toString())
//        call.enqueue(object : Callback<Token> {
//            override fun onResponse(call: Call<Token>, response: Response<Token>) { // spring boot에 데이터 전송 성공시
//                if (response.isSuccessful) {
//                    googleLogout()
//                    googleSignInClient.revokeAccess()
//
////                    val intent = Intent(this@MainActivity, HomeActivity::class.java) //로그인 화면으로 돌아오게 해야 함
////                    startActivity(intent)
//
//                    Toast.makeText(this@MainActivity, "성공", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@MainActivity, "실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onFailure(call: Call<Token>, t: Throwable) { //spring boot에 데이터 전송 실패시
////                Toast.makeText(this@MainActivity, "데이터 전송 실패", Toast.LENGTH_SHORT).show()
//                Log.e("call error", t.toString())
//            }
//        })
//    }

    /* 구글 아이디로 로그인 되어 있는지 확인 */
//    private fun googleLogInCheck() {
//        val account = GoogleSignIn.getLastSignedInAccount(this) // 로그인 되어 있는지 가져옴
//        if (account != null) {
//            Toast.makeText(this@MainActivity, "이미 로그인 됨", Toast.LENGTH_SHORT).show()
//
//            resultLauncher.launch(googleSignInClient.signInIntent)  // 구글 로그인 창으로 넘어감
//        }
//    }

    private fun isAccessTokenAlive(){ //만료일 지났을 경우 현재 토큰을 갱신 토큰으로 변경
//        val refreshToken = NaverIdLoginSDK.getRefreshToken().toString() // 갱신 토큰
//        val expiresAt = NaverIdLoginSDK.getExpiresAt().toString()


//        if(LocalDateTime.now().toString() > NaverIdLoginSDK.getExpiresAt().toString()){
//
//        }

    }

}