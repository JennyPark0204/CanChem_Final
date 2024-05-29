package com.example.canchem.ui.searchResult

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.R
import com.example.canchem.data.source.adapter.SearchResult.CompoundAdapter
import com.example.canchem.databinding.ActivitySearchResultBinding
import com.example.canchem.data.source.dataclass.Search.ChemicalCompound
import com.example.canchem.data.source.dataclass.Search.ChemicalCompoundResponse
import com.example.canchem.ui.home.NetworkModule
import com.example.canchem.ui.home.getToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.canchem.ui.home.SearchActivity
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.molecularInfo.MolecularInfoActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.example.canchem.ui.searchHistory.SearchHistoryActivity

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var adapter: CompoundAdapter
    private lateinit var drawer : DrawerLayout

    private lateinit var nextButton : TextView
    private lateinit var prevButton : TextView


    private var compounds: ArrayList<ChemicalCompound> = arrayListOf()
    private var totalElements = 0
    private var totalPages = 0
    private var currentPage = 0
    private var searchQuery: String? = null
    private var searchResultActivity: SearchResultActivity ?= null
    private var isClickEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawer = binding.searchResult
        searchResultActivity = this

        // Intent로부터 데이터 추출
        totalElements = intent.getIntExtra("totalElements", 0)
        totalPages = intent.getIntExtra("totalPages", 0)
        searchQuery = intent.getStringExtra("searchQuery")
        compounds = intent.getParcelableArrayListExtra("compounds") ?: arrayListOf()

        // 리사이클러뷰 설정
        setupRecyclerView()

        // 페이징 처리
        setupPagingButtons()

        setOnClick()

        addDrawerListener()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchResult)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val query = searchQuery ?: "검색어"
        binding.totalElementsText.text = "\"$query\"에 관한 ${totalElements}개의 검색결과가 있습니다."

        binding.backBt.setOnClickListener{
            onBackPressed()
        }
    }

    private fun setupRecyclerView(){
        adapter = CompoundAdapter(this, compounds) { compound ->
            if(isClickEnabled) {
                val intent = Intent(this@SearchResultActivity, MolecularInfoActivity::class.java).apply {
                    putExtra("compound", compound)
                }
                startActivity(intent)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupPagingButtons() {
        val paginationContainer = binding.paginationContainer
        paginationContainer.removeAllViews()

        val pageRange = 5 // 현재 페이지를 중심으로 표시할 페이지 범위

        // 이전 버튼
        prevButton = TextView(this).apply {
            text = "<<"
            textSize = 16f
            setPadding(16, 16, 16, 16)
            isClickable = true
            setOnClickListener {
                if (currentPage > 0) {
                    currentPage -= 1
                    onPageTextClick(currentPage)
                }
            }
        }
        paginationContainer.addView(prevButton)

        // 페이지 번호 표시
        val startPage = maxOf(currentPage - pageRange / 2, 0)
        val endPage = minOf(startPage + pageRange, totalPages)

        for (i in startPage until endPage) {
            var textView = TextView(this).apply {
                text = (i + 1).toString()
                textSize = 16f
                setPadding(16, 16, 16, 16)
                setTextColor(ContextCompat.getColor(this@SearchResultActivity, R.color.black))
                isClickable = true
                setOnClickListener {
                        currentPage = i
                        onPageTextClick(currentPage)
                }
            }

            if (i == currentPage) {
                textView.setTypeface(null, Typeface.BOLD) // 현재 페이지는 볼드 처리
            }

            val outValue = TypedValue()
            theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            textView.setBackgroundResource(outValue.resourceId)
            paginationContainer.addView(textView)
        }

        // 다음 버튼
        nextButton = TextView(this).apply {
            text = ">>"
            textSize = 16f
            setPadding(16, 16, 16, 16)
            isClickable = true
            setOnClickListener {
                if (currentPage < totalPages - 1) {
                    currentPage += 1
                    onPageTextClick(currentPage)
                }
            }
        }
        paginationContainer.addView(nextButton)
    }

    private fun onPageTextClick(page: Int){
        if (isClickEnabled) {
            currentPage = page
            setupPagingButtons() // 페이지 버튼 다시 설정
            searchQuery?.let {
                fetchNextPageData(it, currentPage)
            }
        }
    }

    private fun fetchNextPageData(search: String, page: Int) {
        val service = NetworkModule.moleculeApiService
        getToken(this@SearchResultActivity) { token ->
            if (token != null) {
                val call = service.getCompounds(token, search, page)
                call.enqueue(object : Callback<ChemicalCompoundResponse> {
                    override fun onResponse(call: Call<ChemicalCompoundResponse>, response: Response<ChemicalCompoundResponse>) {
                        if (response.isSuccessful) {
                            val compoundResponse = response.body()
                            compoundResponse?.let {
                                compounds.clear()
                                compounds.addAll(it.searchResults)
                                adapter.notifyDataSetChanged() // RecyclerView 갱신
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val message = "서버 통신에 실패하였습니다. 코드: ${response.code()}, 오류 메시지: $errorBody"
                            Log.e("FetchNextPageData", message)
                            Toast.makeText(this@SearchResultActivity, message, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ChemicalCompoundResponse>, t: Throwable) {
                        Toast.makeText(this@SearchResultActivity, "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
    private fun setOnClick(){
        //메뉴
        binding.btnMenu.setOnClickListener{
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
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("탈퇴하실 경우, 모든 정보가 삭제됩니다.")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@SearchResultActivity, MainActivity::class.java)
                        intent.putExtra("function", "signout")
                        startActivity(intent)
                        finish()
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
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@SearchResultActivity, MainActivity::class.java)
                        intent.putExtra("function", "logout")
                        startActivity(intent)
                        finish()
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
            val intent = Intent(this, SearchHistoryActivity::class.java)
            startActivity(intent)
        }
        // 홈버튼 클릭시
        findViewById<ImageView>(R.id.btnHome).setOnClickListener{
            var intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
    private fun addDrawerListener(){
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 드로어가 슬라이드될 때 호출됨
            }

            override fun onDrawerOpened(drawerView: View) {
                disableComponents()
            }

            override fun onDrawerClosed(drawerView: View) {
                enableComponents()
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 드로어 상태가 변경될 때 호출됨
            }
        })
    }

    // 클릭 비활성화
    private fun disableComponents(){
        isClickEnabled = false
        nextButton.isClickable = false
        prevButton.isClickable = false
    }

    // 클릭 활성화
    private fun enableComponents(){
        isClickEnabled = true
        nextButton.isClickable = true
        prevButton.isClickable = true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.RIGHT)){
            drawer.closeDrawer(Gravity.RIGHT)
        }else{
            finish()
        }
    }
}
