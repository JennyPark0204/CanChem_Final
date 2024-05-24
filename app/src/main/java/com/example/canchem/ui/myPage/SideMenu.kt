package com.example.canchem.ui.myPage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.canchem.R
import com.example.canchem.databinding.ActivityMenuBinding
import com.example.canchem.databinding.ActivityMyFavoriteBinding
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.example.canchem.ui.searchHistory.SearchHistoryActivity

class SideMenu  : AppCompatActivity(){
    var activity : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extra = intent.extras
        activity = extra?.getString("Activity")

        binding.btnX.setOnClickListener {
            if(activity == "SearchHistoryActivity"){
                val intent = Intent(this, SearchHistoryActivity::class.java)
                intent.putExtra("key", "btnXClicked")
                startActivity(intent)
            }
//          else if로 액티비티별 x버튼 클릭시 만들면 됨.


        }

//        //menu에서 x버튼 클릭시
//        binding.btnX.setOnClickListener {
//            val drawerMyFavorite = findViewById<DrawerLayout>(R.id.myFavorite)
//            val drawerSearchHistory = findViewById<DrawerLayout>(R.id.searchHistory)
//            if(drawerMyFavorite.isDrawerOpen(Gravity.RIGHT)){
//                drawerMyFavorite.closeDrawer(Gravity.RIGHT)
////                val intent = Intent(this@SideMenu,MyFavoriteActivity::class.java)
////                startActivity(intent)
//            }else{
//                drawerSearchHistory.closeDrawer(Gravity.RIGHT)
////                val intent = Intent(this@SideMenu,SearchHistoryActivity::class.java)
////                startActivity(intent)
//            }
//        }

        binding.btnSignout.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("function", "signout")
            startActivity(intent)
        }

//        //회원 탈퇴버튼 클릭시
//        findViewById<TextView>(R.id.btnSignout).setOnClickListener {
//            Toast.makeText(this, "회원탈퇴클릭딸깍", Toast.LENGTH_SHORT)
//            val mainActivityIntent = Intent(this, MainActivity::class.java)
//            mainActivityIntent.putExtra("message", "signout")
//            startActivity(mainActivityIntent)
//
////            (applicationContext as MainActivity).naverDeleteToken() //아마 여기서 main 함수 호출하려고 하는거.
//        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        val drawer1 = findViewById<DrawerLayout>(R.id.myFavorite)
//        val drawer2 = findViewById<DrawerLayout>(R.id.searchHistory)
//        if(drawer1.isDrawerOpen(Gravity.RIGHT)){
//            drawer1.closeDrawer(Gravity.RIGHT)
////            val intent = Intent(this@SideMenu,MyFavoriteActivity::class.java)
////            startActivity(intent)
//        }else{
//            drawer2.closeDrawer(Gravity.RIGHT)
////            val intent = Intent(this@SideMenu,SearchHistoryActivity::class.java)
////            startActivity(intent)
//        }
        if(activity == "SearchHistoryActivity"){
            val intent = Intent(this, SearchHistoryActivity::class.java)
            intent.putExtra("key", "btnXClicked")
            startActivity(intent)
        }
    }
}