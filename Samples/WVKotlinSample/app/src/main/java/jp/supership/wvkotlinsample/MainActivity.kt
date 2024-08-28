package jp.supership.wvkotlinsample

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import jp.supership.wvkotlinsample.databinding.ActivityMainBinding
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "WebViewActivity"
        private const val LOAD_URL = "https://******/"
        private const val USE_LOCAL = true
        private const val LOCAL_URL = "file:///android_asset/test.html"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webview
        // cookieを利用できるようにする
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        // JavaScript有効化
        webView.settings.javaScriptEnabled = true
        // LocalStorage有効化
        webView.settings.domStorageEnabled = true
        // 動画の自動再生許可
        webView.settings.mediaPlaybackRequiresUserGesture = false

        val context = applicationContext
        webView.addJavascriptInterface(WebAppInterface(context), "Android")

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading")
                if (request?.url?.host == null) {
                    return false
                }

                // http-httpsではないものは別
                if (!request.url.scheme.equals("http") &&
                    !request.url.scheme.equals("https")
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, request.url)
                    // If the URL cannot be opened, return early.
                    try {
                        Log.d(TAG, "Handle custom URL schemes ${request.url.scheme}")
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        }
                    } catch (exception: ActivityNotFoundException) {
                        Log.d("TAG", "Failed to load URL with scheme: ${request.url.scheme}")
                    }
                    return true
                }

                // 現在のドメインと遷移先のドメインが異なれえば外部のサイトだと判定する
                val currentDomain = URL(view?.url).host
                val targetDomain = request?.url?.host
                if (!currentDomain.equals(targetDomain)) {
                    // 外部のサイト
                    Log.d(TAG, "external Web Site ${request.url.scheme}")
                    val intent: CustomTabsIntent = CustomTabsIntent.Builder().build()
                    intent.launchUrl(this@MainActivity, request.url)
                    return true
                }

                // 同一サイト
                return false
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                onBackPressedCallback.isEnabled = webView.canGoBack()
            }
        }

        onBackPressedDispatcher.addCallback(this.onBackPressedCallback)

        if (USE_LOCAL) {
            webView.loadUrl(LOCAL_URL)
        } else {
            webView.loadUrl(LOAD_URL)
        }
    }

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                webView.goBack()
            }
        }
}
