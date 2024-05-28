package com.example.canchem.ui.searchHistory

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
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
import com.example.canchem.data.source.myinterface.DeleteAllSearchHistoryInterface
import com.example.canchem.data.source.dataclass.SearchDataList
import com.example.canchem.data.source.myinterface.SearchHistoryInterface
import com.example.canchem.data.source.adapter.SearchRecyclerViewAdapter
import com.example.canchem.data.source.util.UserId
import com.example.canchem.databinding.ActivitySearchHistoryBinding
import com.example.canchem.databinding.ItemSearchBinding
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
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

class SearchHistoryActivity : AppCompatActivity(){
    private val ip : String = "13.124.223.31"
    //recyclerViewAdapter
    private lateinit var adapter: SearchRecyclerViewAdapter
    private lateinit var binding:ActivitySearchHistoryBinding
//    private var instance: SearchHistoryActivity? = null
    lateinit var mDatas : SearchDataList // 검색기록 데이터 리스트 변수

    companion object {
        var isChecked = 1
        private var instance: SearchHistoryActivity? = null
        fun getInstance(): SearchHistoryActivity? {
            if (instance == null) {
                instance = SearchHistoryActivity()
            }
            return instance!!
        }

        // 선택삭제시 삭제할 id값 받아오기
        private val idList = ArrayList<String>()

        fun setIsChecked(isStar : Boolean, id : String){
            if(isStar){
                idList.add(id)
            }else{
                idList.remove(id)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SearchRecyclerViewAdapter() //어댑터 객체 만듦
        val drawer = binding.searchHistory

        //firebase에 저장된 토큰 가져오기
        val database = Firebase.database
        val tokenInFirebase = database.getReference("Token")
        var accessToken : String? = null
        tokenInFirebase.child(UserId.userId!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                accessToken = snapshot.getValue().toString()
//                Toast.makeText(this@SearchHistoryActivity,"파이어베이스 성공!", Toast.LENGTH_SHORT).show()

                // retrofit 변수 생성
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(GsonConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                // retrofit객체 생성
                val searchService = retrofit.create(SearchHistoryInterface::class.java)
                val call = searchService.getSearchInfo(accessToken)


                call.enqueue(object : Callback<SearchDataList> {
                    override fun onResponse(call: Call<SearchDataList>, response: Response<SearchDataList>) { //요청성공시
                        if (response.isSuccessful) {
//                            Toast.makeText(this@SearchHistoryActivity,"retrofit도 성공!", Toast.LENGTH_SHORT).show()
                            mDatas = response.body()!!//여기에 retrofit으로 springboot에서 받은 검색기록 추가.
                            recyclerView(mDatas)
                            Toast.makeText(this@SearchHistoryActivity, mDatas.toString(), Toast.LENGTH_SHORT).show()
                        } else {
//                    Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SearchDataList>, t: Throwable) { //요청실패시
                        Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
                        Log.e("call error", t.toString())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

        // 검색기록 전체 삭제. 서버로 전송하는 코드 작성해야 함.
        binding.btnDeleteAll.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("전체 삭제하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val database = Firebase.database
                        val tokenInFirebase = database.getReference("Token")
                        var accessToken : String? = null
                        tokenInFirebase.child(UserId.userId!!).addValueEventListener(object: ValueEventListener {
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
                                val deleteAllService = retrofit.create(
                                    DeleteAllSearchHistoryInterface::class.java)
                                val call = deleteAllService.deleteAll(accessToken)


                                call.enqueue(object : Callback<String> {
                                    override fun onResponse(call: Call<String>, response: Response<String>) { //요청성공시
                                        if (response.isSuccessful) {
                                            mDatas.searchList.clear()
                                            recyclerView(mDatas)
                                            Toast.makeText(this@SearchHistoryActivity, "전체 삭제 완료", Toast.LENGTH_SHORT).show()
                                        } else {
//                    Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Error", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) { //요청실패시
                                        Toast.makeText(this@SearchHistoryActivity, "SearchHistoryActivity Server cannot 통신", Toast.LENGTH_SHORT).show()
                                        Log.e("call error", t.toString())
                                    }
                                })
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
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
        // 밑의 코드는 복사해서 혼주누나 코드에 붙여야됨. 수정할 부분은 주석으로 말해줌
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 드로어가 슬라이드될 때 호출됨
            }

            override fun onDrawerOpened(drawerView: View) { //여기서 비활성화 할 버튼 추가삭제
                binding.btnDeleteAll.isEnabled = false
                binding.btnDeleteSome.isEnabled = false
            }

            override fun onDrawerClosed(drawerView: View) { //여기서 활성화 할 버튼 추가삭제
                binding.btnDeleteAll.isEnabled = true
                binding.btnDeleteSome.isEnabled = true
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 드로어 상태가 변경될 때 호출됨
            }
        })
        // 검색기록 선택삭제 클릭시
        binding.btnDeleteSome.setOnClickListener{
//            findViewById<CheckBox>(R.id.btnChecked).visibility = View.VISIBLE
            binding.btnDeleteAll.visibility = View.GONE
            binding.btnDeleteSome.visibility = View.GONE
            binding.btnDeleteSomeYes.visibility = View.VISIBLE
            binding.btnDeleteSomeNo.visibility = View.VISIBLE
            findViewById<ImageView>(R.id.btnX).visibility = View.GONE
        }

        // 검색기록 선택삭제 중 삭제버튼 클릭시
        binding.btnDeleteSomeYes.setOnClickListener{
            for(i in idList){
                idList.forEach { id ->
                    mDatas.searchList.removeAll { it.id == id }
                }
            }
            recyclerView(mDatas)
            findViewById<CheckBox>(R.id.btnChecked).visibility = View.GONE
            binding.btnDeleteAll.visibility = View.VISIBLE
            binding.btnDeleteSome.visibility = View.VISIBLE
            binding.btnDeleteSomeYes.visibility = View.GONE
            binding.btnDeleteSomeNo.visibility = View.GONE
        }

        // 검색기록 선택삭제 중 취소버튼 클릭시
        binding.btnDeleteSomeYes.setOnClickListener{
            findViewById<CheckBox>(R.id.btnChecked).visibility = View.GONE
            binding.btnDeleteAll.visibility = View.VISIBLE
            binding.btnDeleteSome.visibility = View.VISIBLE
            binding.btnDeleteSomeYes.visibility = View.GONE
            binding.btnDeleteSomeNo.visibility = View.GONE
        }

        // side menu. 여기부터 아래 코드는 모든 액티비티에 포함됨.
        // 메뉴 클릭시
        binding.btnMenu.setOnClickListener {
            drawer.openDrawer(Gravity.RIGHT)
            binding.btnDeleteAll.isEnabled = false
            binding.btnDeleteSome.isEnabled = false
        }
        // x버튼 클릭시
        findViewById<ImageView>(R.id.btnX).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
            binding.btnDeleteAll.isEnabled = true
            binding.btnDeleteSome.isEnabled = true
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
                        val intent = Intent(this@SearchHistoryActivity, MainActivity::class.java)
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
            AlertDialog.Builder(this)
                .setTitle("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@SearchHistoryActivity, MainActivity::class.java)
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
            val intent = Intent(this, MyFavoriteActivity::class.java)
            startActivity(intent)
        }
        // 검색기록 클릭시
        findViewById<TextView>(R.id.btnSearchHistory).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
        }
        // 홈버튼 클릭시
        findViewById<ImageView>(R.id.btnHome).setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val drawer = binding.searchHistory
        if(drawer.isDrawerOpen(Gravity.RIGHT)){
            drawer.closeDrawer(Gravity.RIGHT)
            binding.btnDeleteAll.isEnabled = true
            binding.btnDeleteSome.isEnabled = true
        }else{
            finish()
        }
    }

    fun recyclerView(mData: SearchDataList){
        adapter.datalist = mData //데이터 넣어줌
        binding.recyclerView.adapter = adapter //리사이클러뷰에 어댑터 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this) //레이아웃 매니저 연결
    }
}