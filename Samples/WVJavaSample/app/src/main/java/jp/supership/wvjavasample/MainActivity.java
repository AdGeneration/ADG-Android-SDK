package jp.supership.wvjavasample;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private static final String LOAD_URL = "https://******/";
    private static final boolean USE_LOCAL = true;
    private static final String LOCAL_URL = "file:///android_asset/test.html";

    private WebView webView;
    private OnBackPressedCallback onBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        // cookieを利用できるようにする
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        // JavaScript有効化
        webView.getSettings().setJavaScriptEnabled(true);
        // LocalStorage有効化
        webView.getSettings().setDomStorageEnabled(true);
        // 動画の自動再生許可
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webView.addJavascriptInterface(new WebAppInterface(getApplicationContext()), "Android");

        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view, WebResourceRequest request) {
                        Log.d(TAG, "shouldOverrideUrlLoading");
                        if (request.getUrl().getHost() == null) {
                            return false;
                        }

                        // http-httpsではないものは別
                        if (!request.getUrl().getScheme().equals("http")
                                && !request.getUrl().getScheme().equals("https")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                            // If the URL cannot be opened, return early.
                            try {
                                Log.d(
                                        TAG,
                                        "Handle custom URL schemes "
                                                + request.getUrl().getScheme());
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            } catch (ActivityNotFoundException exception) {
                                Log.d(
                                        TAG,
                                        "Failed to load URL with scheme: "
                                                + request.getUrl().getScheme());
                            }
                            return true;
                        }

                        String currentDomain;
                        try {
                            currentDomain = new URL(view.getUrl()).getHost();
                        } catch (MalformedURLException exception) {
                            // 不正なURL
                            return false;
                        }
                        // 現在のドメインと遷移先のドメインが異なれえば外部のサイトだと判定する
                        String targetDomain = request.getUrl().getHost();
                        if (!currentDomain.equals(targetDomain)) {
                            // 外部のサイト
                            Log.d(TAG, "external Web Site " + request.getUrl().getScheme());
                            CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                            intent.launchUrl(MainActivity.this, request.getUrl());
                            return true;
                        }

                        // 同一サイト
                        return false;
                    }

                    @Override
                    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                        onBackPressedCallback.setEnabled(webView.canGoBack());
                    }
                });

        onBackPressedCallback =
                new OnBackPressedCallback(false) {
                    @Override
                    public void handleOnBackPressed() {
                        webView.goBack();
                    }
                };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        if (USE_LOCAL) {
            webView.loadUrl(LOCAL_URL);
        } else {
            webView.loadUrl(LOAD_URL);
        }
    }
}
