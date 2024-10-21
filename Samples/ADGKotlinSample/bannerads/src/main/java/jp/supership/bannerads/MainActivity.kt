package jp.supership.bannerads

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
import jp.supership.bannerads.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var adg: ADG
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // 管理画面から払い出された広告枠ID
    private val locationID = "48547"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout and set content view (only once)
        setContentView(binding.root)

        // デバッグログ出力設定。リリース時は[必ず]falseにしてください
        ADGSettings.setDebugLogging(true)

        ADGDebugUtils.logSDKDetails(this)

        binding.apply {
            textLocationID.text = getString(R.string.ad_unit_id_format, locationID)
            buttonInfo.setOnClickListener { showInfo() }
            buttonReload.setOnClickListener { reloadAd() }
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
        adg.setAdFrameSize(ADG.AdFrameSize.SP)

        // 配置
        binding.adContainer.addView(adg)
    }

    internal inner class AdListener : ADGListener() {
        override fun onReceiveAd() {
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

        override fun onClickAd() {
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
