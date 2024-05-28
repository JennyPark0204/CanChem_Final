package com.example.canchem.ui.myFavorite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.R
import com.example.canchem.ui.home.SearchActivity
import com.example.canchem.data.source.myinterface.DeleteAllMyFavoriteInterface
import com.example.canchem.data.source.dataclass.FavoriteDataList
import com.example.canchem.data.source.adapter.FavoriteRecyclerViewAdapter
import com.example.canchem.data.source.myinterface.DeleteOneStarInterface
import com.example.canchem.data.source.myinterface.MyFavoriteInterface
import com.example.canchem.data.source.util.UserId
import com.example.canchem.databinding.ActivityMyFavoriteBinding
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.searchHistory.SearchHistoryActivity
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
import retrofit2.converter.scalars.ScalarsConverterFactory

class MyFavoriteActivity : AppCompatActivity() {
    private val ip : String = "13.124.223.31"
    //recyclerViewAdapter
    private lateinit var adapter: FavoriteRecyclerViewAdapter
    private lateinit var binding: ActivityMyFavoriteBinding
    lateinit var mDatas : FavoriteDataList // 즐겨찾기 데이터 리스트 변수

    companion object{
        private var instance: MyFavoriteActivity? = null
//        private var id = "-100"
        private val idList = ArrayList<String>()

        fun setIsBtnStar(isStar : Boolean, id : String){
            if(isStar){
                idList.remove(id)
            }else{
                idList.add(id)
            }
        }

        fun getInstance(): MyFavoriteActivity? {
            return instance
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = FavoriteRecyclerViewAdapter() //어댑터 객체 만듦
        val drawer = binding.myFavorite

        //firebase에 저장된 토큰 가져오기
        val database = Firebase.database
        val tokenInFirebase = database.getReference("Token")
        var accessToken : String? = null
        tokenInFirebase.child(UserId.userId!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                accessToken = snapshot.getValue().toString()
//                Toast.makeText(this@MyFavoriteActivity,"파이어베이스 성공!", Toast.LENGTH_SHORT).show()
                Log.d("onDataChange는 " , "성공")
                // retrofit 변수 생성
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                // retrofit객체 생성
                val myFavoriteService = retrofit.create(MyFavoriteInterface::class.java)
                val call = myFavoriteService.getFavoriteInfo(accessToken)


                call.enqueue(object : Callback<FavoriteDataList> {
                    override fun onResponse(call: Call<FavoriteDataList>, response: Response<FavoriteDataList>) { //요청성공시
                        if (response.isSuccessful) {
//                            Toast.makeText(this@MyFavoriteActivity,response.toString(), Toast.LENGTH_SHORT).show()
                            mDatas = response.body()!! //여기에 retrofit으로 springboot에서 받은 검색기록 추가.
                            Log.d("mDatas는 " , mDatas.toString())
                            for(i in idList){
//                                Toast.makeText(this@MyFavoriteActivity,mDatas.favoriteList.get(i.toInt()).toString(),Toast.LENGTH_SHORT).show()
//                                mDatas.favoriteList.remove(mDatas.favoriteList.get(i.toInt()))
                                idList.forEach { id ->
                                    mDatas.favoriteList.removeAll { it.id == id }
                                    val retrofit2 = Retrofit.Builder()
                                        .baseUrl("http://$ip:8080/")
                                        .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
                                        .build()
                                    val myFavoriteDelete = retrofit.create(DeleteOneStarInterface::class.java)
                                    val call2 = myFavoriteDelete.deleteStar(accessToken, id)
                                    call2.enqueue(object : Callback<String> {
                                        override fun onResponse(call: Call<String>, response: Response<String>) { //요청성공시
                                            if (response.isSuccessful) {
                                                Log.d("삭제삭제", "굿")
                                            }
                                        }
                                        override fun onFailure(call: Call<String>, t: Throwable) { //요청실패시
                                            Toast.makeText(this@MyFavoriteActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
                                            Log.e("call error", t.toString())
                                        }
                                    })
                                }
                            }
                            recyclerView(mDatas)
                            Toast.makeText(this@MyFavoriteActivity, mDatas.toString(), Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(ContentValues.TAG, "Response unsuccessful: ${response.code()}")
//                    Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<FavoriteDataList>, t: Throwable) { //요청실패시
                        Toast.makeText(this@MyFavoriteActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
                        Log.e("call error", t.toString())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

        // 즐겨찾기 전체 삭제. 서버에 전송하는 코드 작성해야 함
        binding.btnDeleteAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("전체 삭제하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val database = Firebase.database
                        val tokenInFirebase = database.getReference("Token")
                        var accessToken: String? = null
                        tokenInFirebase.child(UserId.userId!!)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    accessToken = snapshot.getValue().toString()
//                Toast.makeText(this@SearchHistoryActivity,"파이어베이스 성공!", Toast.LENGTH_SHORT).show()

                                    // retrofit 변수 생성
                                    val retrofit = Retrofit.Builder()
                                        .baseUrl("http://$ip:8080/")
                                        .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
                                        .build()

                                    // retrofit객체 생성
                                    val deleteAllService =
                                        retrofit.create(DeleteAllMyFavoriteInterface::class.java)
                                    val call = deleteAllService.deleteAll(accessToken)


                                    call.enqueue(object : Callback<String> {
                                        override fun onResponse(
                                            call: Call<String>,
                                            response: Response<String>
                                        ) { //요청성공시
                                            if (response.isSuccessful) {
                                                mDatas.favoriteList.clear()
                                                recyclerView(mDatas)
                                                Toast.makeText(
                                                    this@MyFavoriteActivity,
                                                    "전체 삭제 완료",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
//                    Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<String>,
                                            t: Throwable
                                        ) { //요청실패시
                                            Toast.makeText(
                                                this@MyFavoriteActivity,
                                                "SearchHistoryActivity Server cannot 통신",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.e("call error", t.toString())
                                        }
                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.w(
                                        ContentValues.TAG,
                                        "Failed to read value.",
                                        error.toException()
                                    )
                                }
                            })
                    }
                })
                .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")
                    }
                })
                .create()
                .show()
        }

        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 드로어가 슬라이드될 때 호출됨
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.btnDeleteAll.isEnabled = false
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.btnDeleteAll.isEnabled = true
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 드로어 상태가 변경될 때 호출됨
            }
        })
        // side menu. 여기부터 아래 코드는 모든 액티비티에 포함됨.
        // 메뉴 클릭시
        binding.btnMenu.setOnClickListener {
            drawer.openDrawer(Gravity.RIGHT)
        }
        // x버튼 클릭시
        findViewById<ImageView>(R.id.btnX).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
        }
        // My Page 열기 버튼 클릭시
        findViewById<ImageView>(R.id.btnOpenDown).setOnClickListener{
            findViewById<ImageView>(R.id.btnOpenDown).visibility = View.GONE
            findViewById<ImageView>(R.id.btnCloseUp).visibility = View.VISIBLE
            findViewById<TextView>(R.id.btnMyFavorite).visibility = View.VISIBLE
            findViewById<TextView>(R.id.btnSearchHistory).visibility = View.VISIBLE
        }
        // My Page 닫기 버튼 클릭시
        findViewById<ImageView>(R.id.btnCloseUp).setOnClickListener{
            findViewById<ImageView>(R.id.btnOpenDown).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.btnCloseUp).visibility = View.GONE
            findViewById<TextView>(R.id.btnMyFavorite).visibility = View.GONE
            findViewById<TextView>(R.id.btnSearchHistory).visibility = View.GONE
        }
        // My Page 글씨로 열고 닫기
        findViewById<TextView>(R.id.btnMyPage).setOnClickListener{
            if(findViewById<ImageView>(R.id.btnOpenDown).visibility == View.VISIBLE){
                findViewById<ImageView>(R.id.btnOpenDown).visibility = View.GONE
                findViewById<ImageView>(R.id.btnCloseUp).visibility = View.VISIBLE
                findViewById<TextView>(R.id.btnMyFavorite).visibility = View.VISIBLE
                findViewById<TextView>(R.id.btnSearchHistory).visibility = View.VISIBLE
            }else{
                findViewById<ImageView>(R.id.btnOpenDown).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.btnCloseUp).visibility = View.GONE
                findViewById<TextView>(R.id.btnMyFavorite).visibility = View.GONE
                findViewById<TextView>(R.id.btnSearchHistory).visibility = View.GONE
            }
        }
        // 회원탈퇴 클릭시
        findViewById<TextView>(R.id.btnSignout).setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("탈퇴하실 경우, 모든 정보가 삭제됩니다.")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@MyFavoriteActivity, MainActivity::class.java)
                        intent.putExtra("function", "signout")
                        startActivity(intent)
                    }
                })
                .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")
                    }
                })
                .create()
                .show()
        }
        // 로그아웃 클릭시
        findViewById<TextView>(R.id.btnLogout).setOnClickListener{
            Log.d("로그아웃", "클릭")
            AlertDialog.Builder(this)
                .setTitle("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@MyFavoriteActivity, MainActivity::class.java)
                        Log.d("로그아웃", "으로 메인 넘어감")
                        intent.putExtra("function", "logout")
                        startActivity(intent)
                    }
                })
                .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")
                    }
                })
                .create()
                .show()

        }
        // 즐겨찾기 클릭시
        findViewById<TextView>(R.id.btnMyFavorite).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
        }
        // 검색기록 클릭시
        findViewById<TextView>(R.id.btnSearchHistory).setOnClickListener{
            val intent = Intent(this, SearchHistoryActivity::class.java)
            startActivity(intent)
        }
        // 홈버튼 클릭시
        findViewById<ImageView>(R.id.btnHome).setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val drawer = binding.myFavorite
        if(drawer.isDrawerOpen(Gravity.RIGHT)){
            drawer.closeDrawer(Gravity.RIGHT)
        }else{
            finish()
        }
    }

    fun recyclerView(mData: FavoriteDataList){
        adapter.datalist = mData //데이터 넣어줌
        binding.recyclerView.adapter = adapter //리사이클러뷰에 어댑터 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this) //레이아웃 매니저 연결
    }
}