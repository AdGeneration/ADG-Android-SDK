package jp.supership.wvkotlinsample

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.android.gms.ads.identifier.AdvertisingIdClient

class WebAppInterface {

    companion object {
        private const val TAG = "WebAppInterface"
    }

    private var advertising_id: String? = ""
    private var isLAT: Boolean = false
    private lateinit var context: Context

    constructor(ct: Context) {
        this.context = ct
        this.initAdIdThread()
    }

    private fun initAdIdThread() {
        val adIdThread = Thread {
            var adInfo: AdvertisingIdClient.Info? = null
            try {
                adInfo = AdvertisingIdClient
                    .getAdvertisingIdInfo(this.context)
                advertising_id = adInfo.id
                isLAT = adInfo.isLimitAdTrackingEnabled
            } catch (e: Exception) {
                Log.d(TAG, "getAdId failed")
            }
        }
        adIdThread.start()
    }

    @JavascriptInterface
    open fun getAppBundle(): String {
        return this.context.packageName ?: ""
    }

    @JavascriptInterface
    open fun getAdvertisingId(): String {
        val adid = advertising_id ?: ""
        return if (!isLAT) adid else ""
    }

    @JavascriptInterface
    open fun getPlatformV(): String {
        return Build.VERSION.RELEASE
    }
}
