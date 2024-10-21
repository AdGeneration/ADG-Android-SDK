package jp.supership.bannerads;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGDebugUtils;
import com.socdm.d.adgeneration.ADGListener;
import com.socdm.d.adgeneration.ADGSettings;

import jp.supership.bannerads.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Nullable private ADG adg;
    @NonNull private final String locationID = "48547";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // デバッグログ出力設定。リリース時は[必ず]falseにしてください
        ADGSettings.setDebugLogging(true);

        ADGDebugUtils.logSDKDetails(this);

        binding.textLocationID.setText(getString(R.string.ad_unit_id_format, this.locationID));
        binding.buttonInfo.setOnClickListener(v -> this.showInfo());
        binding.buttonReload.setOnClickListener(v -> this.reloadAd());

        this.initAd();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adg != null) {
            // 広告の表示
            adg.start();
        }
    }

    @Override
    protected void onPause() {
        if (adg != null) {
            // 広告の破棄
            adg.stop();
        }

        super.onPause();
    }

    private void initAd() {
        // ADG
        adg = new ADG(this);

        // 管理画面から払い出された広告枠ID
        adg.setLocationId(this.locationID);

        // テストモードを有効化。リリース時は[必ず]falseにしてください
        adg.setTestModeEnabled(true);

        /*
         枠サイズ AdFrameSize.SP：320x50, AdFrameSize.Large:320x100, AdFrameSize.Rect:300x250,
         AdFrameSize.Tablet:728x90, ADG.AdFrameSize.FREE.setSize(xxx, xxx):自由設定
        */
        adg.setAdFrameSize(ADG.AdFrameSize.SP);

        // Listenerの設定
        adg.setAdListener(new AdListener());

        // 広告の配置
        binding.adContainer.addView(adg);
    }

    class AdListener extends ADGListener {
        @Override
        public void onReceiveAd() {}

        @Override
        public void onFailedToReceiveAd(@NonNull final ADGConsts.ADGErrorCode code) {
            // ネットワーク不通/エラー多発/広告レスポンスなし 以外はリトライしてください
            switch (code) {
                case EXCEED_LIMIT: // エラー多発
                case NEED_CONNECTION: // ネットワーク不通
                case NO_AD: // 広告レスポンスなし
                    break;
                default:
                    if (adg != null) {
                        adg.start();
                    }
                    break;
            }
        }

        @Override
        public void onClickAd() {}
    }

    @NonNull
    private String getInfoText() {
        String ret = "";
        ret += "Androidバージョン: " + Build.VERSION.RELEASE + "\n";
        ret += "API Level: " + Build.VERSION.SDK_INT + "\n";
        ret += "メーカー名: " + Build.MANUFACTURER + "\n";
        ret += "モデル番号: " + Build.MODEL + "\n";
        ret += "ブランド名: " + Build.BRAND + "\n";
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        ret += "国コード: " + tm.getNetworkCountryIso() + "\n";
        ret += "MCC+MNC: " + tm.getNetworkOperator() + "\n";
        ret += "サービスプロバイダの名前: " + tm.getNetworkOperatorName() + "\n";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ret += "NETWORKの状態: " + tm.getNetworkType() + "\n";
        } else {
            ret += "NETWORKの状態: ((表示されるアラートで許可して、再表示してください))\n";
            ActivityCompat.requestPermissions(
                    this, new String[] {Manifest.permission.READ_PHONE_STATE}, 0);
        }
        ret += "locale: " + getResources().getConfiguration().locale + "\n";
        ret += "density: " + getResources().getDisplayMetrics().density + "\n";
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        ret += "dimensions.x: " + point.x + "\n";
        ret += "dimensions.y: " + point.y + "\n";
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ret += "widthDips: " + (dm.widthPixels / dm.density) + "\n";
        ret += "heightDips: " + (dm.heightPixels / dm.density) + "\n";
        return ret;
    }

    private void showInfo() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Info")
                .setMessage(this.getInfoText())
                .show();
    }

    private void reloadAd() {
        if (adg != null) {
            adg.stop();
            adg.start();
        }
    }
}
