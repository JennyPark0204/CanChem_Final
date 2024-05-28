package com.example.canchem.data.source.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.canchem.ui.searchHistory.SearchHistoryActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.canchem.databinding.ItemSearchBinding
import android.view.ViewGroup
import android.widget.Toast
import com.example.canchem.data.source.myinterface.DeleteOneSearchHistoryInterface
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.dataclass.SearchDataList
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
import retrofit2.converter.scalars.ScalarsConverterFactory


class SearchRecyclerViewAdapter(): RecyclerView.Adapter<SearchRecyclerViewAdapter.MyViewHolder>() {

    lateinit var datalist : SearchDataList//리사이클러뷰에서 사용할 데이터 미리 정의 -> 나중에 MainActivity등에서 datalist에 실제 데이터 추가
    private val ip : String = "13.124.223.31"
    val database = Firebase.database
    val tokenInFirebase = database.getReference("Token")

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int =datalist.searchList.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datalist.searchList[position])
    }

    inner class MyViewHolder(private val binding: ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {

        //private val searchActivity = SearchHistoryActivity.getInstance()
        private var mData: SearchData? = null

        init {
            binding.btnX.setOnClickListener {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://$ip:8080/")
                    .addConverterFactory(ScalarsConverterFactory.create()) //kotlin to json(역 일수도)
                    .build()

                val deleteService = retrofit.create(DeleteOneSearchHistoryInterface::class.java)

                tokenInFirebase.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val value = snapshot.getValue().toString()
                        val id = mData!!.id
                        Log.d("mData의 id는", id)
                        Log.d(ContentValues.TAG, "Value is: " + value)
                        val call = deleteService.deleteSearchHistory(value, id)
                        Log.i("call", call.toString())
                        call.enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) { // spring boot에 데이터 전송 성공시
                                if (response.isSuccessful) {
                                    Log.d("성공시 mData의 id는", id)
                                    Log.d("성공시 searchList는", datalist.searchList.toString())
                                    datalist.searchList.remove(mData)
                                    notifyDataSetChanged()
                                } else {
                                    Log.e(ContentValues.TAG, "Response unsuccessful: ${response.code()}")
                                }
                            }
                            override fun onFailure(call: Call<String>, t: Throwable) { //spring boot에 데이터 전송 실패시
                                Log.e("call error", t.toString())
                                Log.d("isnotsuccess", call.toString())
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                    }
                })
            }

//            var isBtnCheckBoxTrue = false // 즐겨찾기 해제인지 아닌지 판단
            // 선택삭제 체크버튼 클릭시
            binding.btnChecked.setOnCheckedChangeListener{buttonView, isChecked ->
                SearchHistoryActivity.setIsChecked(isChecked, mData!!.id)
            }
//            binding.btnChecked.setOnClickListener{
//                val newCheckedState = !binding.btnChecked.isChecked // 현재 상태의 반전된 값을 계산합니다.
//                binding.btnChecked.isChecked = newCheckedState // 반전된 상태로 설정합니다.
//                SearchHistoryActivity.setIsChecked(newCheckedState, mData!!.id) // 변경된 상태를 전달합니다.
//            }
            fun setCheckBox(){
                binding.btnChecked.visibility = View.VISIBLE
            }

        }
        //id값 받아서 SearchData를 return해줌
//        fun getSearchData(id : String) : MutableList<SearchData> {
//            val list = mutableListOf<SearchData>()
//            for(data in datalist){
//                if(data.id == id)
//                    list.add(data)
//            }
//            return list
//        }

//        fun setCheckBox(searchData: SearchData){
//            binding.btnChecked.isChecked = true
//            bind(searchData)
//        }

        fun bind(searchData: SearchData){
            mData = searchData
            binding.searchText.text = searchData.log
        }

    }
}


