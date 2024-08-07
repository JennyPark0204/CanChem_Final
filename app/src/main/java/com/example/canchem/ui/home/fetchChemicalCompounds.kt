package com.example.canchem.ui.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.canchem.data.source.dataclass.Search.ChemicalCompoundResponse
import com.example.canchem.ui.molecularInfo.MolecularInfoActivity
import com.example.canchem.ui.searchResult.SearchResultActivity
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

                    if(totalElements == 1){
                        val intent = Intent(context, MolecularInfoActivity::class.java)

                        intent.putExtra("compound", compounds[0])

                        context.startActivity(intent)
                    }
                    else{
                        val intent = Intent(context, SearchResultActivity::class.java)
                        intent.putExtra("totalElements", totalElements)
                        intent.putExtra("totalPages", totalPages)
                        intent.putParcelableArrayListExtra("compounds", ArrayList(compounds))
                        intent.putExtra("searchQuery", searchQuery)
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