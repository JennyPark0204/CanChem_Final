package com.example.canchem.ui.searchResult

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.R
import com.example.canchem.databinding.ActivitySearchResultBinding
import com.example.canchem.data.source.dataclass.Search.ChemicalCompound

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Intent로부터 데이터 추출
        val totalElements = intent.getIntExtra("totalElements", 0)
        val totalPages = intent.getIntExtra("totalPages", 0)
        val compounds: ArrayList<ChemicalCompound>? = intent.getParcelableArrayListExtra("compounds")
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}