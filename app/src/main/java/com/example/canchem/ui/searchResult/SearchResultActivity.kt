package com.example.canchem.ui.searchResult

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var adapter: CompoundAdapter
    private var compounds: ArrayList<ChemicalCompound> = arrayListOf()
    private var totalElements = 0
    private var totalPages = 0
    private var currentPage = 0
    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Intent로부터 데이터 추출
        totalElements = intent.getIntExtra("totalElements", 0)
        totalPages = intent.getIntExtra("totalPages", 0)
        searchQuery = intent.getStringExtra("searchQuery")
        compounds = intent.getParcelableArrayListExtra("compounds") ?: arrayListOf()


        // 리사이클러뷰 설정
        setupRecyclerView()

        // 페이징 처리
        setupPagingButtons()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchResult)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView(){
        adapter = CompoundAdapter(this, compounds) { compound ->
            // 아이템 클릭 시 처리할 작업
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupPagingButtons() {
        val paginationContainer = binding.paginationContainer
        paginationContainer.removeAllViews()

        for (i in 0..totalPages-1) {
            val button = Button(this).apply {
                text = (i+1).toString()
                setOnClickListener {
                    onPageButtonClick(i)
                }
            }
            paginationContainer.addView(button)
        }
    }

    private fun onPageButtonClick(page: Int){
        currentPage = page
        try{
            searchQuery?.let {
                fetchNextPageData(it, currentPage)
            }
        }catch (e: Exception){
            Toast.makeText(this@SearchResultActivity, currentPage.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchNextPageData(search:String, page: Int) {
        val service = NetworkModule.moleculeApiService
        getToken(this@SearchResultActivity){token->
            if(token != null){
                val call = service.getCompounds(token, search, page)
                call.enqueue(object : Callback<ChemicalCompoundResponse> {
                    override fun onResponse(call: Call<ChemicalCompoundResponse>, response: Response<ChemicalCompoundResponse>) {
                        if (response.isSuccessful) {
                            val compoundResponse = response.body()
                            compoundResponse?.let {
                                compounds.clear()
                                compounds.addAll(it.searchResults)
                                adapter.notifyDataSetChanged() //RecyclerView 갱신
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
}
