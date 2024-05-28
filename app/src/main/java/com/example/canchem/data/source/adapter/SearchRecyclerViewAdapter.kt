package com.example.canchem.data.source.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.databinding.ItemSearchBinding
import com.example.canchem.data.source.service.delete.DeleteOne

class SearchRecyclerViewAdapter(
    private val context: Context,
    private val token: String,
    private var searchDataList: List<SearchData>
) : RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchData = searchDataList[position]
        holder.bind(searchData)
    }

    override fun getItemCount(): Int {
        return searchDataList.size
    }

    fun updateData(newData: List<SearchData>) {
        searchDataList = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchData: SearchData) {
            binding.searchText.text = searchData.log

            // 닫기 버튼 클릭 리스너 설정
            binding.btnX.setOnClickListener {
                val searchId = searchDataList[adapterPosition].id.toString() // 클릭된 항목의 ID를 가져옴
                deleteSearchLog(token, searchId) // 삭제 요청 메소드 호출
            }
        }

        private fun deleteSearchLog(token: String, searchId: String) {
            val deleteService = DeleteOne(context)
            deleteService.deleteSearchLog(token, searchId, {
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

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
