package com.example.canchem.ui.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.canchem.data.source.dataclass.Search.ChemicalCompoundResponse
import com.example.canchem.ui.molecularInfo.ApiActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchChemicalCompounds(context: Context, token: String, searchQuery: String, page: Int) {
    val service = NetworkModule.moleculeApiService
    val call = service.getCompounds(token, searchQuery, page)
    call.enqueue(object : Callback<ChemicalCompoundResponse> {
        override fun onResponse(call: Call<ChemicalCompoundResponse>, response: Response<ChemicalCompoundResponse>) {
            if (response.isSuccessful) {
                val compoundResponse = response.body()
                compoundResponse?.let {
                    val totalElements = it.totalElements
                    val totalPages = it.totalPages
                    val compounds = it. searchResults
                    compounds.forEach { compound ->
                        // 각 화합물에 대한 처리
                        val id = compound.id
                        val cid = compound.cid
                        val inpacName = compound.inpacName
                        val molecularFormula = compound.molecularFormula
                        val molecularWeight = compound.molecularWeight
                        val isomericSmiles = compound.isomericSmiles
                        val inchi = compound.inchi
                        val inchiKey = compound.inchiKey
                        val canonicalSmiles = compound.canonicalSmiles
                        val description = compound.description
                        val image2DUrl = compound.image2DUri
                        val image3DConformer = compound.image3DConformer
                    }
                    if(totalElements == 0){
                        Toast.makeText(context, "검색 결과가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else if(totalElements == 1){
                        Toast.makeText(context, "검색 결과 하나 인텐트.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, ApiActivity::class.java)

                        intent.putExtra("compounds", compounds)

                        context.startActivity(intent)
                    }
                    else{
                        val intent = Intent(context, SearchActivity::class.java)

                        intent.putExtra("totalElements", totalElements)
                        intent.putExtra("totalPages", totalPages)
                        intent.putExtra("compounds", compounds)

                        context.startActivity(intent)
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val message = "서버 통신에 실패하였습니다. 코드: ${response.code()}, 오류 메시지: $errorBody"
                Log.e("FetchChemicalCompounds", message)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        override fun onFailure(call: Call<ChemicalCompoundResponse>, t: Throwable) {
            Toast.makeText(context, "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}