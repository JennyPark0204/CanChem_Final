package com.example.canchem.ui.molecularInfo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.canchem.databinding.ActivityMolecularInfoBinding
import com.example.canchem.R
import com.example.canchem.data.source.dataclass.BookMark.BookmarkState
import com.example.canchem.data.source.dataclass.Search.ChemicalCompound
import com.example.canchem.ui.home.NetworkModule
import com.example.canchem.ui.home.SearchActivity
import com.example.canchem.ui.home.getToken
import com.example.canchem.ui.main.MainActivity
import com.example.canchem.ui.myFavorite.MyFavoriteActivity
import com.example.canchem.ui.webView.WebGLViewr
import com.squareup.picasso.Picasso
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class MolecularInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMolecularInfoBinding
    private lateinit var compoundImage: ImageView
    private var compound: ChemicalCompound? = null
    private var currentStar = false
    private var moleculeId : String? = null
    private var isStarFilled = false
    private var urlCid : String? = null
    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMolecularInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawer = binding.molecularInfo

        //ImageView 초기화
        compoundImage = findViewById(R.id.Image2D)
        compound = intent.getParcelableExtra("compound")
        compound?.let { setText(it) }
        urlCid?.let { onWebView(it) }
        setOnClick()

    }

    private fun setText(compound : ChemicalCompound)
    {
        compound?.let {
            if (!it.image2DUri.isNullOrEmpty()) {
                Picasso.get().load(it.image2DUri).into(compoundImage)
            } else {
                compoundImage.setImageResource(R.drawable.ic_no_image)
            }
            urlCid = it.cid
            moleculeId = it.id

            binding.CompoundName.text = it.synonyms?.firstOrNull() ?: "Unknown"
            binding.cid.text = "CID : ${it.cid ?: "N/A"}"
            binding.inpacName.text = "IUPAC Name : ${it.inpacName ?: "UnKnown"}"
            binding.molecularFormula.text = "Molecular Formula : ${it.molecularFormula ?: "N/A"}"
            binding.molecularWeight.text = "Molecular Weight : ${it.molecularWeight ?: "N/A"}"
            binding.isomericSmlies.text = "Isomeric SMLIES : ${it.isomericSmiles ?: "N/A"}"
            binding.inchi.text = "InChI : ${it.inchi ?: "N/A"} "
            binding.inchikey.text = "InChIKey : ${it.inchiKey ?: "N/A"}"
            binding.canonicalSmlies.text = "Canonical SMLIES : ${it.canonicalSmiles ?: "N/A"}"
            binding.synonyms.text = it.synonyms?.joinToString(
                separator = ",\n",
                prefix = "Synonyms : ",
                transform = { synonym ->
                    if (it.synonyms?.firstOrNull() == synonym) {
                        "\"$synonym\""
                    } else {
                        "\t\t\t\t\t\t\t\"$synonym\""
                    }
                }
            ) ?: "Synonyms : Unknown"


            binding.description.text = "Description : ${it.description ?: "No description available"}"
            //즐겨찾기 여부 검사
            fetchBookmarkState(it.id)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.molecular_info)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 이미지뷰에 클릭 리스너 추가
    }



    //즐겨찾기 여부를 먼저 확인하고 화면에 띄움
    private fun fetchBookmarkState(moleculeId: String) {
        val bookmarkStateService = NetworkModule.bookmarkStateService
        getToken(this@MolecularInfoActivity) { token ->
            if (token != null) {
                val call = bookmarkStateService.getBookmark(token, moleculeId)
                call.enqueue(object : Callback<BookmarkState> {
                    override fun onResponse(call: Call<BookmarkState>, response: Response<BookmarkState>) {
                        if(response.isSuccessful){
                            val bookMark = response.body()
                            bookMark?.let {
                                if(it.state){
                                    isStarFilled = true
                                    binding.star.setImageResource(R.drawable.ic_star_filled)
                                }
                                else{
                                    isStarFilled = false
                                    binding.star.setImageResource(R.drawable.ic_star_empty)
                                }
                            }
                            currentStar= isStarFilled
                        }
                        else{
                            //서버통신 실패
                            Toast.makeText(this@MolecularInfoActivity, "즐겨찾기 정보를 가져오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<BookmarkState>, t: Throwable) {
                        Toast.makeText(this@MolecularInfoActivity, "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun onWebView(cid: String) {
        val webView: WebView = binding.Image3D
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        getToken(this@MolecularInfoActivity) { token ->
            if (token != null) {
                val url = "http://34.64.68.219:8000/render?user_token=$token&cid=$cid"
                webView.loadUrl(url)
            }
        }
    }

    private fun joinWebGLViewer(cid: String) {
        val intent = Intent(this, WebGLViewr::class.java).apply {
            putExtra("cid", cid)
        }
        startActivity(intent)
    }
    override fun onPause() {
        super.onPause()
        // currentStar와 isStarFilled 값이 다른 경우에만 서버에 통신
        if (currentStar != isStarFilled) {
            if(isStarFilled){
                //북마크 추가
                moleculeId?.let { fetchBookmark(this@MolecularInfoActivity, it) }
            }
            else{
                //북마크 해제
                moleculeId?.let { fetchBookmarkOff(this@MolecularInfoActivity, it) }
            }
        }
    }

    private fun setOnClick()
    {
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

        binding.backBt.setOnClickListener{
            onBackPressed()
        }

        binding.enlargement3D.setOnClickListener()
        {
            urlCid?.let { it1 -> joinWebGLViewer(it1) }
        }

        //메뉴
        binding.menuBtn.setOnClickListener{
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
        // 회원탈퇴 클릭시
        findViewById<TextView>(R.id.btnSignout).setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("탈퇴하실 경우, 모든 정보가 삭제됩니다.")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@MolecularInfoActivity, MainActivity::class.java)
                        intent.putExtra("function", "signout")
                        startActivity(intent)
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
            AlertDialog.Builder(this)
                .setTitle("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val intent = Intent(this@MolecularInfoActivity, MainActivity::class.java)
                        intent.putExtra("function", "logout")
                        startActivity(intent)
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
        }
        // 검색기록 클릭시
        findViewById<TextView>(R.id.btnSearchHistory).setOnClickListener{
            drawer.closeDrawer(Gravity.RIGHT)
        }
        // 홈버튼 클릭시
        findViewById<ImageView>(R.id.btnHome).setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.RIGHT)){
            drawer.closeDrawer(Gravity.RIGHT)
        }else{
            finish()
        }
    }
}