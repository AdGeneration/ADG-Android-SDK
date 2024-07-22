package jp.supership.nativeads

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.socdm.d.adgeneration.ADG
import com.socdm.d.adgeneration.ADGConsts
import com.socdm.d.adgeneration.ADGDebugUtils
import com.socdm.d.adgeneration.ADGListener
import com.socdm.d.adgeneration.ADGSettings
import com.socdm.d.adgeneration.nativead.ADGNativeAd
import jp.supership.nativeads.databinding.ActivityMainBinding
import jp.supership.nativeads.views.nativeads.ADGNativeAdView

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var adg: ADG

    // 管理画面から払い出された広告枠ID
    private val locationID = "48635"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout and set content view (only once)
        setContentView(binding.root)

        // デバッグログ出力設定。リリース時は[必ず]falseにしてください
        ADGSettings.setDebugLogging(true)

        ADGDebugUtils.logSDKDetails()

        binding.apply {
            textLocationID.text = getString(R.string.ad_unit_id_format, locationID)
            buttonInfo.setOnClickListener { showInfo() }
            buttonStartStop.setOnClickListener { reloadAd() }
        }

        initAd()
    }

    override fun onResume() {
        super.onResume()

        // 広告の表示
        adg.start()
    }

    override fun onPause() {
        // 広告の破棄
        adg.stop()

        super.onPause()
    }

    private fun initAd() {
        adg = ADG(this).apply {
            // 管理画面から払い出された広告枠ID
            locationId = locationID
            // Listenerの設定
            adListener = AdListener()
            // // テストモードを有効化。リリース時は[必ず]falseにしてください
            isTestModeEnabled = true
        }

        /**
         * 枠サイズ
         * AdFrameSize.SP：320x50, AdFrameSize.Large:320x100,
         * AdFrameSize.Rect:300x250, AdFrameSize.Tablet:728x90,
         * AdFrameSize.Free:自由設定
         */
        adg.setAdFrameSize(ADG.AdFrameSize.FREE.setSize(300, 250))

        // ネイティブ広告パーツ取得を有効
        adg.setUsePartsResponse(true)

        // インフォメーションアイコンのデフォルト表示
        // デフォルト表示しない場合は必ずADGInformationIconViewの設置を実装してください
        adg.setInformationIconViewDefault(false)

        // HTMLテンプレートを使用したネイティブ広告を表示のためにはaddViewする必要があります
        binding.adContainer.addView(adg)
    }

    internal inner class AdListener : ADGListener() {
        override fun onReceiveAd() {
        }

        override fun onReceiveAd(nativeAd: Any) {
            if (nativeAd is ADGNativeAd) {
                val nativeAdView = ADGNativeAdView(this@MainActivity)
                nativeAdView.apply(nativeAd)

                // ローテーション時に自動的にViewを削除します
                adg.setAutomaticallyRemoveOnReload(nativeAdView)

                binding.adContainer.addView(nativeAdView)
            }
        }

        override fun onFailedToReceiveAd(code: ADGConsts.ADGErrorCode) {
            // ネットワーク不通/エラー多発/広告レスポンスなし 以外はリトライしてください
            when (code) {
                ADGConsts.ADGErrorCode.EXCEED_LIMIT,
                ADGConsts.ADGErrorCode.NEED_CONNECTION,
                ADGConsts.ADGErrorCode.NO_AD,
                -> {}
                else -> adg.start()
            }
        }
    }

    private fun getInfoText(): String {
        var ret = ""
        ret += "Androidバージョン: " + Build.VERSION.RELEASE + "\n"
        ret += "API Level: " + Build.VERSION.SDK_INT + "\n"
        ret += "メーカー名: " + Build.MANUFACTURER + "\n"
        ret += "モデル番号: " + Build.MODEL + "\n"
        ret += "ブランド名: " + Build.BRAND + "\n"
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        ret += "国コード: " + tm.networkCountryIso + "\n"
        ret += "MCC+MNC: " + tm.networkOperator + "\n"
        ret += "サービスプロバイダの名前: " + tm.networkOperatorName + "\n"
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ret += "NETWORKの状態: " + tm.networkType + "\n"
        } else {
            ret += "NETWORKの状態: ((表示されるアラートで許可して、再表示してください))\n"
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                0,
            )
        }
        ret += "locale: " + resources.configuration.locale + "\n"
        ret += "density: " + resources.displayMetrics.density + "\n"
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        ret += "dimensions.x: " + point.x + "\n"
        ret += "dimensions.y: " + point.y + "\n"
        val dm = resources.displayMetrics
        ret += "widthDips: " + dm.widthPixels / dm.density + "\n"
        ret += "heightDips: " + dm.heightPixels / dm.density + "\n"
        return ret
    }

    private fun showInfo() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Info")
            .setMessage(getInfoText())
            .show()
    }

    private fun reloadAd() {
        adg.stop()
        adg.start()
    }
}
