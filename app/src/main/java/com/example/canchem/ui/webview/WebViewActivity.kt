package com.example.canchem.ui.webview

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.canchem.R
import com.example.canchem.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // WebView 설정 가져오기
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
//
        binding.webView.loadUrl("https://threejs.org/examples/css3d_molecules.html")
    }
}