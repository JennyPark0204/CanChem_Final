package com.example.canchem.ui.test

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.R
import com.example.canchem.databinding.ActivityYeonjeTestBinding
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.example.canchem.ui.searchHistory.SearchHistoryActivity

class YeonjeTestActivity : AppCompatActivity() {
    lateinit var binding : ActivityYeonjeTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYeonjeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var mToast : Toast ?= null
//        val intent = intent.extras
//        if(intent?.getString("key") == "yes"){
//            sayHello()
//        }

        // 토스트 메시지 띄우기
        binding.testText1.setOnClickListener{
            mToast = Toast.makeText(this, "ddddddddddddd", Toast.LENGTH_SHORT)
            mToast?.show()
        }
        // 토스트 메시지 삭제
        binding.testText2.setOnClickListener{
            mToast?.cancel()
        }


        binding.btnGo.setOnClickListener{
            val intent = Intent(this@YeonjeTestActivity, MyFavoriteActivity::class.java)
            startActivity(intent)
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
//    fun sayHello(){
//        binding.testText.text = "jkjkjkjkjk"
//    }
}