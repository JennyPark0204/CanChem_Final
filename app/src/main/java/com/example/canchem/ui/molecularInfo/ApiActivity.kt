package com.example.canchem.ui.molecularInfo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.databinding.ActivityApiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import com.example.canchem.MoleculeApiService
import com.example.canchem.R
import com.example.canchem.data.source.mol_Name

class ApiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApiBinding
    private lateinit var moleculeApiService: MoleculeApiService

    var isStarFilled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchQuery = intent.getStringExtra("search_query")
//        Log.d("SearchQuery", "Search query: $searchQuery")

//        Retrofit 인스턴스 생성
        var retrofit = Retrofit.Builder()
            .baseUrl("http://59.152.141.225:8008/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        API 서비스 인터페이스 가져오기
        moleculeApiService = retrofit.create(MoleculeApiService::class.java)

//        분자정보 가져오기
        searchQuery?.let{
            fetchCompoundInfo(it)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.api)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 이미지뷰에 클릭 리스너 추가
        binding.star.setOnClickListener {
            // 현재 즐겨찾기 상태에 따라 이미지 변경
            if (isStarFilled) {
                binding.star.setImageResource(R.drawable.ic_star_empty)
            } else {
                binding.star.setImageResource(R.drawable.ic_star_filled)
            }
            // 상태 토글
            isStarFilled = !isStarFilled
        }
    }

    private fun fetchCompoundInfo(searchQuery: String) {

        // API 호출
        val call = moleculeApiService.getCompound(searchQuery)

        // 비동기로 API 응답 처리
        call.enqueue(object : Callback<mol_Name> {
            override fun onResponse(call: Call<mol_Name>, response: Response<mol_Name>) {
                if (response.isSuccessful) {
                    // API 호출이 성공한 경우
//                    Toast.makeText(this@ApiActivity, "API 호출에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    val compound = response.body()
                    Log.d("Compound_Object", "Compound object: $compound")

                    // 분자 정보를 화면에 표시
                    compound?.let {
                        binding.inpacName.text = it.inpacName
                        binding.cid.text = it.cid.toString()
                        binding.inchi.text = it.inchi
//                        Log.d("CID_Value", "CID value: ${it.cid}")
                        binding.inpacName.text = it.inpacName
                    }
                } else {
                    // API 호출이 실패한 경우
                    Toast.makeText(this@ApiActivity, "API 호출에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<mol_Name>, t: Throwable) {
                // 네트워크 오류 등으로 호출이 실패한 경우
                Toast.makeText(this@ApiActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }
}