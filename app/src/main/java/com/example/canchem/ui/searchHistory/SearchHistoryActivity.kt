package com.example.canchem.ui.searchHistory

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canchem.R
import com.example.canchem.data.source.adapter.SearchRecyclerViewAdapter
import com.example.canchem.data.source.dataclass.SearchData
import com.example.canchem.data.source.dataclass.SearchDataList
import com.example.canchem.data.source.service.GetSearchHistoryService
import com.example.canchem.data.source.service.delete.DeleteAll
import com.example.canchem.data.source.service.delete.DeleteSelect
import com.example.canchem.databinding.ActivitySearchHistoryBinding
import com.example.canchem.ui.home.SearchActivity
import com.example.canchem.ui.home.getToken
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import kotlinx.coroutines.launch

class SearchHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchHistoryBinding
    private lateinit var adapter: SearchRecyclerViewAdapter
    private var token: String = ""
    private var toggleDeleteSome: Boolean = false
    private var searchDataList: List<SearchData> = emptyList()
    private lateinit var drawer : DrawerLayout
    private var backpressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchSearchHistory()
        setupDeleteButtons()

        drawer = binding.searchHistory
        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 드로어가 슬라이드될 때 호출됨
            }

            override fun onDrawerOpened(drawerView: View) {
                binding.btnDeleteAll.isEnabled = false
                binding.btnDeleteSomeCancle.isEnabled = false
                binding.btnDeleteSome.isEnabled = false
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.btnDeleteAll.isEnabled = true
                binding.btnDeleteSomeCancle.isEnabled = true
                binding.btnDeleteSome.isEnabled = true
            }

            override fun onDrawerStateChanged(newState: Int) {
                // 드로어 상태가 변경될 때 호출됨
            }
        })
        // side menu. 여기부터 아래 코드는 모든 액티비티에 포함됨.
        // 메뉴 클릭시
        binding.btnMenu.setOnClickListener {
            drawer.openDrawer(Gravity.RIGHT)
        }
        // x버튼 클릭시
        findViewById<ImageView>(R.id.btnX).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
        }
        // My Page 열기 버튼 클릭시
        findViewById<ImageView>(R.id.btnOpenDown).setOnClickListener{
            findViewById<ImageView>(R.id.btnOpenDown).visibility = View.GONE
            findViewById<ImageView>(R.id.btnCloseUp).visibility = View.VISIBLE
            findViewById<TextView>(R.id.btnMyFavorite).visibility = View.VISIBLE
            findViewById<TextView>(R.id.btnSearchHistory).visibility = View.VISIBLE
        }
        // My Page 닫기 버튼 클릭시
        findViewById<ImageView>(R.id.btnCloseUp).setOnClickListener{
            findViewById<ImageView>(R.id.btnOpenDown).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.btnCloseUp).visibility = View.GONE
            findViewById<TextView>(R.id.btnMyFavorite).visibility = View.GONE
            findViewById<TextView>(R.id.btnSearchHistory).visibility = View.GONE
        }
        // My Page 글씨로 열고 닫기
        findViewById<TextView>(R.id.btnMyPage).setOnClickListener{
            if(findViewById<ImageView>(R.id.btnOpenDown).visibility == View.VISIBLE){
                findViewById<ImageView>(R.id.btnOpenDown).visibility = View.GONE
                findViewById<ImageView>(R.id.btnCloseUp).visibility = View.VISIBLE
                findViewById<TextView>(R.id.btnMyFavorite).visibility = View.VISIBLE
                findViewById<TextView>(R.id.btnSearchHistory).visibility = View.VISIBLE
            }else{
                findViewById<ImageView>(R.id.btnOpenDown).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.btnCloseUp).visibility = View.GONE
                findViewById<TextView>(R.id.btnMyFavorite).visibility = View.GONE
                findViewById<TextView>(R.id.btnSearchHistory).visibility = View.GONE
            }
        }
        // 회원탈퇴 클릭시
        findViewById<TextView>(R.id.btnSignout).setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("탈퇴하실 경우, 모든 정보가 삭제됩니다.")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@SearchHistoryActivity, MainActivity::class.java)
                        intent.putExtra("function", "signout")
                        startActivity(intent)
                        finish()
                    }
                })
                .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")
                    }
                })
                .create()
                .show()
        }
        // 로그아웃 클릭시
        findViewById<TextView>(R.id.btnLogout).setOnClickListener{
            Log.d("로그아웃", "클릭")
            AlertDialog.Builder(this)
                .setTitle("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@SearchHistoryActivity, MainActivity::class.java)
                        Log.d("로그아웃", "으로 메인 넘어감")
                        intent.putExtra("function", "logout")
                        startActivity(intent)
                        finish()
                    }
                })
                .setNegativeButton("취소", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")
                    }
                })
                .create()
                .show()

        }
        // 즐겨찾기 클릭시
        findViewById<TextView>(R.id.btnMyFavorite).setOnClickListener{
            val intent = Intent(this, MyFavoriteActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 검색기록 클릭시
        findViewById<TextView>(R.id.btnSearchHistory).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
        }
        // 홈버튼 클릭시
        findViewById<ImageView>(R.id.btnHome).setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.RIGHT)){
            drawer.closeDrawer(Gravity.RIGHT)
        }else{
            if (System.currentTimeMillis() > backpressedTime + 2000) {
                backpressedTime = System.currentTimeMillis();
                Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
                finish()
            }

        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchRecyclerViewAdapter(this, token, searchDataList)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchSearchHistory() {
        getToken(this@SearchHistoryActivity) { receivedToken ->
            token = receivedToken ?: ""
            val searchHistoryService = GetSearchHistoryService(this)
            if (token.isNotEmpty()) {
                searchHistoryService.getSearchHistory(
                    token,
                    { searchDataList ->
                        this.searchDataList = searchDataList
                        adapter.updateData(searchDataList)
                    },
                    {
                        showToast("검색 기록이 없습니다.")
                    }
                )
            } else {
                showToast("토큰 값 오류")
            }
        }
    }

    private fun setupDeleteButtons() {
        binding.btnDeleteAll.setOnClickListener {
            if (token.isNotEmpty()) {
                showDeleteConfirmationDialog()
            } else {
                showToast("토큰 값 오류")
            }
        }

        binding.btnDeleteSome.setOnClickListener {
            if (toggleDeleteSome) {
                // 이미 삭제 모드가 활성화된 상태에서 삭제 버튼을 눌렀을 때
                showDeleteSelectedConfirmationDialog()

            } else {
                if(searchDataList.isNotEmpty())
                {
                    // 삭제 모드가 비활성화된 상태에서 삭제 버튼을 눌렀을 때
                    adapter.toggleDeleteMode()
                    toggleDeleteSome = true
                    binding.btnDeleteSomeCancle.visibility = View.VISIBLE
                    binding.btnDeleteAll.isEnabled = false
                    binding.btnDeleteSomeCancle.setOnClickListener {
                        adapter.toggleDeleteMode() // 삭제 모드를 해제
                        toggleDeleteSome = false
                        binding.btnDeleteSomeCancle.visibility = View.GONE
                        binding.btnDeleteAll.isEnabled = true
                    }
                }
                else{
                    showToast("검색 기록이 없습니다.")
                }

            }
        }
    }



    private fun showDeleteConfirmationDialog() {
        if(searchDataList.isNotEmpty())
        {
            AlertDialog.Builder(this)
                .setTitle("전체 삭제")
                .setMessage("정말로 모든 검색 기록을 삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ -> deleteAllSearchHistory() }
                .setNegativeButton("아니오", null)
                .show()
        }
        else{
            showToast("검색 기록이 없습니다.")
        }

    }

    private fun showDeleteSelectedConfirmationDialog() {
        val list = adapter.getSelectedItems()

        if (list.isEmpty()) {
            Toast.makeText(this, "선택된 항목이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("선택 항목 삭제")
            .setMessage("선택한 검색 기록을 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                deleteSelectedHistory(list)
                searchDataList =
                if (searchDataList.isEmpty()) emptyList() else searchDataList.subtract(list).toList()
                adapter.updateData(searchDataList)
            }
            .setNegativeButton("아니오", null)
            .show()
    }



    private fun deleteAllSearchHistory() {
        val deleteAllService = DeleteAll(this)
        deleteAllService.deleteAllSearchHistory(
            token,
            {
                // 화면 갱신
                searchDataList = emptyList()
                adapter.updateData(searchDataList)
            },
            { errorMessage ->
                showToast(errorMessage)
            }
        )
    }

    private fun deleteSelectedHistory(selectedItems: List<SearchData>){
        val deleteSelectedService = DeleteSelect(this)
        deleteSelectedService.deleteSearchLog(token, selectedItems,
            onSuccess = {
                // 성공적으로 삭제된 경우 처리할 내용
                //Toast.makeText(this, "선택된 검색 기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                // 삭제 후 화면 갱신 또는 다른 작업 수행
            },
            onFailure = { errorMessage ->
                // 삭제 실패 또는 오류 발생 시 처리할 내용
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
