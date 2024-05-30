package com.example.canchem.ui.home

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.canchem.data.source.dataclass.Search.ChemicalCompound
import com.example.canchem.ui.molecularInfo.MolecularInfoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchSmilesCompound(context: Context, token : String, smiles : String){
    val service = NetworkModule.smilesSearchService
    val call = service.smilesSearch(token, smiles)
    call.enqueue(object : Callback<ChemicalCompound>{
        override fun onResponse(call: Call<ChemicalCompound>, response: Response<ChemicalCompound>) {
            if(response.isSuccessful){
                val compound = response.body()
                compound?.let {
                    val intent = Intent(context, MolecularInfoActivity::class.java)
                    intent.putExtra("compound", compound)
                }
            }
            else{
                Toast.makeText(context, "이미지 검색 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call:Call<ChemicalCompound>, t:Throwable) {
            Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}