package com.example.canchem.data.source.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.canchem.data.source.dataclass.FavoriteData
import com.example.canchem.data.source.dataclass.FavoriteDataList
import com.example.canchem.databinding.ItemFavoriteBinding
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.molecularInfo.MolecularInfoActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FavoriteRecyclerViewAdapter: RecyclerView.Adapter<FavoriteRecyclerViewAdapter.MyViewHolder>() {

    lateinit var datalist : FavoriteDataList//리사이클러뷰에서 사용할 데이터 미리 정의 -> 나중에 MainActivity등에서 datalist에 실제 데이터 추가
    private val ip : String = "13.124.223.31"
    val database = Firebase.database
    val tokenInFirebase = database.getReference("Token")

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int =datalist.favoriteList.size

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datalist.favoriteList[position])
    }

    inner class MyViewHolder(private val binding: ItemFavoriteBinding): RecyclerView.ViewHolder(binding.root) {

//        private val favoriteActivity = MyFavoriteActivity.getInstance()
        var mData: FavoriteData? = null
        var isBtnStarTrue = true // 즐겨찾기 해제인지 아닌지 판단

        init {
            // 별 버튼 클릭시
            binding.btnStar.setOnClickListener {
                if(isBtnStarTrue){ // 즐겨찾기 해제
                    binding.btnStar.isChecked = false
                    isBtnStarTrue = false
                    MyFavoriteActivity.setIsBtnStar(isBtnStarTrue, mData!!.id)
                }else{ // 즐겨찾기 재등록
                    binding.btnStar.isChecked = true
                    isBtnStarTrue = true
                    MyFavoriteActivity.setIsBtnStar(isBtnStarTrue, mData!!.id)
                }
            }
            // 텍스트 클릭시
            binding.favoriteText.setOnClickListener{
                val intent = Intent(itemView.context, MolecularInfoActivity::class.java)
                intent.putExtra("chemId", mData?.chem_id)
                itemView.context.startActivity(intent)
            }
        }


        fun bind(favoriteData: FavoriteData){
//            if(isBtnStarTrue){ // 즐겨찾기 on인 경우
                mData = favoriteData
                binding.favoriteText.text = favoriteData.molecular_formula
        }
    }
}