package com.example.canchem.ui.webView

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.databinding.ActivityWebGlviewrBinding
import com.example.canchem.ui.home.getToken

class WebGLViewr : AppCompatActivity() {
    private lateinit var binding: ActivityWebGlviewrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWebGlviewrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webview.webViewClient = WebViewClient()
        binding.webview.settings.javaScriptEnabled = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cid = intent.getStringExtra("cid")
        if (cid != null) {
            loadWebView(cid)
        }
    }

    private fun loadWebView(cid: String) {
        getToken(this@WebGLViewr) { token ->
            if (token != null) {
                val url = "http://34.64.68.219:8000/render?user_token=$token&cid=$cid"
                binding.webview.loadUrl(url)
            }
        }
    }
}
