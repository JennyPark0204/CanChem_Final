package com.example.canchem.ui.searchHistory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.data.source.adapter.SearchRecyclerViewAdapter
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.service.GetSearchHistoryService
import com.example.canchem.data.source.service.delete.DeleteAll
import com.example.canchem.databinding.ActivitySearchHistoryBinding
import com.example.canchem.ui.home.getToken

class SearchHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchHistoryBinding
    private lateinit var adapter: SearchRecyclerViewAdapter
    private var token: String = ""
    private var searchDataList: List<SearchData> = emptyList()

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
        adapter = SearchRecyclerViewAdapter(this, token, searchDataList)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchSearchHistory() {
        getToken(this@SearchHistoryActivity) { receivedToken ->
            token = receivedToken ?: ""
            val searchHistoryService = GetSearchHistoryService(this)
            if (token.isNotEmpty()) {
                searchHistoryService.getSearchHistory(
                    token,
                    { searchDataList ->
                        this.searchDataList = searchDataList
                        adapter.updateData(searchDataList)
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
                showDeleteConfirmationDialog()
            } else {
                showToast("토큰 값 오류")
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("전체 삭제")
            .setMessage("정말로 모든 검색 기록을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ -> deleteAllSearchHistory() }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun deleteAllSearchHistory() {
        val deleteAllService = DeleteAll(this)
        deleteAllService.deleteAllSearchHistory(
            token,
            {
                showToast("전체 검색 기록이 삭제되었습니다.")
                // 화면 갱신
                searchDataList = emptyList()
                adapter.updateData(searchDataList)
            },
            { errorMessage ->
                showToast(errorMessage)
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
