package com.dantepaulxd.lightbrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlBar = findViewById(R.id.urlBar)
        progressBar = findViewById(R.id.progressBar)

        setupWebView()
        setupControls()

        webView.loadUrl("https://www.google.com")
    }

    private fun setupWebView() {

        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        settings.loadsImagesAutomatically = true

        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        settings.mediaPlaybackRequiresUserGesture = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {

                return false
            }

            override fun onPageStarted(
                view: WebView,
                url: String?,
                favicon: Bitmap?
            ) {

                super.onPageStarted(view, url, favicon)

                urlBar.setText(url ?: "")

                progressBar.visibility = ProgressBar.VISIBLE
            }

            override fun onPageFinished(
                view: WebView,
                url: String?
            ) {

                super.onPageFinished(view, url)

                urlBar.setText(url ?: "")

                progressBar.visibility = ProgressBar.GONE
            }
        }

        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {

                progressBar.progress = newProgress

                if (newProgress >= 100) {
                    progressBar.visibility = ProgressBar.GONE
                } else {
                    progressBar.visibility = ProgressBar.VISIBLE
                }
            }
        }
    }

    private fun setupControls() {

        val backButton: ImageButton =
            findViewById(R.id.backButton)

        val menuButton: ImageButton =
            findViewById(R.id.menuButton)

        backButton.setOnClickListener {

            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        menuButton.setOnClickListener {

            webView.reload()
        }

        urlBar.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_GO) {

                navigate(urlBar.text.toString())

                true

            } else {

                false
            }
        }
    }

    private fun navigate(input: String) {

        var url = input.trim()

        if (url.isEmpty()) {
            return
        }

        if (!url.startsWith("http://") &&
            !url.startsWith("https://")) {

            url = if (url.contains(" ")) {
                "https://www.google.com/search?q=" +
                        java.net.URLEncoder.encode(
                            url,
                            "UTF-8"
                        )
            } else {
                "https://$url"
            }
        }

        webView.loadUrl(url)
    }

    override fun onBackPressed() {

        if (webView.canGoBack()) {

            webView.goBack()

        } else {

            super.onBackPressed()
        }
    }

    override fun onDestroy() {

        webView.stopLoading()
        webView.webViewClient = null
        webView.webChromeClient = null

        webView.destroy()

        super.onDestroy()
    }
}
