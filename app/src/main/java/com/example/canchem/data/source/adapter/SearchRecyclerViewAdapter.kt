package com.example.canchem.data.source.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.dataclass.SearchDataList
import com.example.canchem.databinding.ItemSearchBinding
import com.example.canchem.data.source.service.delete.DeleteOne
import com.example.canchem.ui.home.getToken

class SearchRecyclerViewAdapter(
    private val context: Context,
    private val token: String?,
    private var searchDataList: List<SearchData>,
) : RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    private var isDeleteMode: Boolean = false
    private val selectedItems = mutableListOf<SearchData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchData = searchDataList[position]
        holder.bind(searchData, isDeleteMode)
    }

    override fun getItemCount(): Int {
        return searchDataList.size
    }

    fun updateData(newData: List<SearchData>) {
        searchDataList = newData
        selectedItems.clear()  // 새로운 데이터가 업데이트되면 선택된 항목 초기화
        notifyDataSetChanged()
    }

    fun toggleDeleteMode() {
        isDeleteMode = !isDeleteMode
        selectedItems.clear()  // 삭제 모드를 변경할 때도 선택된 항목 초기화
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<SearchData> {
        return selectedItems.toList()
    }


    inner class ViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchData: SearchData, isDeleteMode: Boolean) {
            binding.searchText.text = searchData.log

            // 닫기 버튼 클릭 리스너 설정
            binding.btnX.setOnClickListener {
                val searchId = searchDataList[adapterPosition].id.toString() // 클릭된 항목의 ID를 가져옴
                deleteSearchLog(searchId)
            }

            // Delete mode에 따라 체크박스 visibility 설정
            binding.btnChecked.visibility = if (isDeleteMode) View.VISIBLE else View.GONE
            binding.btnX.visibility = if(isDeleteMode) View.INVISIBLE else View.VISIBLE

            // 체크박스 상태 초기화
            binding.btnChecked.isChecked = selectedItems.contains(searchData)

            binding.btnChecked.setOnClickListener {
                if (binding.btnChecked.isChecked)
                    selectedItems.add(searchData)
                else
                    selectedItems.remove(searchData)
            }
        }

        private fun deleteSearchLog(searchId: String) {
            getToken(context) { token ->
                val deleteService = DeleteOne(context)
                token?.let {
                    deleteService.deleteSearchLog(it, searchId, {
                        // 성공 시 처리할 내용
                        showToast("검색 기록이 삭제되었습니다.")
                        // 삭제 후에 해당 아이템을 리스트에서 제거하고 어댑터를 갱신
                        searchDataList = searchDataList.filterNot { it.id.toString() == searchId }
                        notifyDataSetChanged()
                    }, { errorMessage ->
                        // 실패 시 처리할 내용
                        showToast(errorMessage)
                    })
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

