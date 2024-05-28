package com.example.canchem.ui.searchHistory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.data.source.adapter.SearchRecyclerViewAdapter
import com.example.canchem.data.source.service.GetSearchHistoryService
import com.example.canchem.data.source.service.delete.DeleteAll
import com.example.canchem.databinding.ActivitySearchHistoryBinding
import com.example.canchem.ui.home.getToken

class SearchHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchHistoryBinding
    private lateinit var adapter: SearchRecyclerViewAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchSearchHistory()
        setupDeleteButtons()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchSearchHistory() {
        getToken(this@SearchHistoryActivity) { receivedToken ->
            token = receivedToken ?: ""
            val searchHistoryService = GetSearchHistoryService(this)
            if (token.isNotEmpty()) {
                searchHistoryService.getSearchHistory(
                    token,
                    { searchDataList ->
                        adapter = SearchRecyclerViewAdapter(this, token, searchDataList)
                        binding.recyclerView.adapter = adapter
                    },
                    {
                        showToast("검색 기록이 없습니다.")
                    }
                )
            } else {
                showToast("토큰 값 오류")
            }
        }
    }

    private fun setupDeleteButtons() {
        binding.btnDeleteAll.setOnClickListener {
            if (token.isNotEmpty()) {
                // 전체 삭제 요청
                val deleteAllService = DeleteAll(this)
                deleteAllService.deleteAllSearchHistory(
                    token,
                    {
                        showToast("전체 검색 기록이 삭제되었습니다.")
                        // 화면 갱신
                        fetchSearchHistory()
                    },
                    { errorMessage ->
                        showToast(errorMessage)
                    }
                )
            } else {
                showToast("토큰 값 오류")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
