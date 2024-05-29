package com.example.canchem.ui.searchHistory

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.data.source.adapter.SearchRecyclerViewAdapter
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.dataclass.SearchDataList
import com.example.canchem.data.source.service.GetSearchHistoryService
import com.example.canchem.data.source.service.delete.DeleteAll
import com.example.canchem.data.source.service.delete.DeleteSelect
import com.example.canchem.databinding.ActivitySearchHistoryBinding
import com.example.canchem.ui.home.getToken
import kotlinx.coroutines.launch

class SearchHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchHistoryBinding
    private lateinit var adapter: SearchRecyclerViewAdapter
    private var token: String = ""
    private var toggleDeleteSome: Boolean = false
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

        binding.btnDeleteSome.setOnClickListener {
            if (toggleDeleteSome) {
                // 이미 삭제 모드가 활성화된 상태에서 삭제 버튼을 눌렀을 때
                showDeleteSelectedConfirmationDialog()

            } else {
                if(searchDataList.isNotEmpty())
                {
                    // 삭제 모드가 비활성화된 상태에서 삭제 버튼을 눌렀을 때
                    adapter.toggleDeleteMode()
                    toggleDeleteSome = true
                    binding.btnDeleteSomeCancle.visibility = View.VISIBLE
                    binding.btnDeleteAll.isEnabled = false
                    binding.btnDeleteSomeCancle.setOnClickListener {
                        adapter.toggleDeleteMode() // 삭제 모드를 해제
                        toggleDeleteSome = false
                        binding.btnDeleteSomeCancle.visibility = View.GONE
                        binding.btnDeleteAll.isEnabled = true
                    }
                }
                else{
                    showToast("검색 기록이 없습니다.")
                }

            }
        }
    }



    private fun showDeleteConfirmationDialog() {
        if(searchDataList.isNotEmpty())
        {
            AlertDialog.Builder(this)
                .setTitle("전체 삭제")
                .setMessage("정말로 모든 검색 기록을 삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ -> deleteAllSearchHistory() }
                .setNegativeButton("아니오", null)
                .show()
        }
        else{
            showToast("검색 기록이 없습니다.")
        }

    }

    private fun showDeleteSelectedConfirmationDialog() {
        val list = adapter.getSelectedItems()

        if (list.isEmpty()) {
            Toast.makeText(this, "선택된 항목이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("선택 항목 삭제")
            .setMessage("선택한 검색 기록을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                deleteSelectedHistory(list)
                searchDataList =
                if (searchDataList.isEmpty()) emptyList() else searchDataList.subtract(list).toList()
                adapter.updateData(searchDataList)
            }
            .setNegativeButton("아니오", null)
            .show()
    }



    private fun deleteAllSearchHistory() {
        val deleteAllService = DeleteAll(this)
        deleteAllService.deleteAllSearchHistory(
            token,
            {
                // 화면 갱신
                searchDataList = emptyList()
                adapter.updateData(searchDataList)
            },
            { errorMessage ->
                showToast(errorMessage)
            }
        )
    }

    private fun deleteSelectedHistory(selectedItems: List<SearchData>){
        val deleteSelectedService = DeleteSelect(this)
        deleteSelectedService.deleteSearchLog(token, selectedItems,
            onSuccess = {
                // 성공적으로 삭제된 경우 처리할 내용
                //Toast.makeText(this, "선택된 검색 기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                // 삭제 후 화면 갱신 또는 다른 작업 수행
            },
            onFailure = { errorMessage ->
                // 삭제 실패 또는 오류 발생 시 처리할 내용
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
