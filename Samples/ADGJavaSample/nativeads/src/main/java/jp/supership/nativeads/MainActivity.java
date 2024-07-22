package jp.supership.nativeads;

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
import com.socdm.d.adgeneration.nativead.ADGNativeAd;

import jp.supership.nativeads.databinding.ActivityMainBinding;
import jp.supership.nativeads.views.nativeads.ADGNativeAdView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Nullable private ADG adg;
    @NonNull private final String locationID = "48636";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // デバッグログ出力設定。リリース時は[必ず]falseにしてください
        ADGSettings.setDebugLogging(true);

        ADGDebugUtils.logSDKDetails();

        binding.btnStopstart.setOnClickListener(v -> reloadAd());
        binding.textLocationID.setText(getString(R.string.ad_unit_id_format, this.locationID));
        binding.buttonInfo.setOnClickListener(v -> this.showInfo());

        initAd();
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
        adg = new ADG(this);

        // 管理画面から払い出された広告枠ID
        adg.setLocationId(locationID);

        // テストモードを有効化。リリース時は[必ず]falseにしてください
        adg.setTestModeEnabled(true);

        /*
         枠サイズ AdFrameSize.SP：320x50, AdFrameSize.Large:320x100, AdFrameSize.Rect:300x250,
         AdFrameSize.Tablet:728x90, ADG.AdFrameSize.FREE.setSize(xxx, xxx):自由設定
        */
        adg.setAdFrameSize(ADG.AdFrameSize.FREE.setSize(300, 250));

        // ネイティブ広告パーツ取得を有効
        adg.setUsePartsResponse(true);

        // インフォメーションアイコンのデフォルト表示
        // デフォルト表示しない場合は必ずADGInformationIconViewの設置を実装してください
        adg.setInformationIconViewDefault(false);

        // Listenerの設定
        adg.setAdListener(new AdListener());

        // HTMLテンプレートを使用したネイティブ広告を表示のためにはaddViewする必要があります
        binding.adContainer.addView(adg);
    }

    class AdListener extends ADGListener {
        @Override
        public void onReceiveAd() {}

        @Override
        public void onReceiveAd(Object o) {
            View view = null;
            if (o instanceof ADGNativeAd) {
                ADGNativeAdView nativeAdView = new ADGNativeAdView(MainActivity.this);
                nativeAdView.apply((ADGNativeAd) o);
                view = nativeAdView;
            }

            if (view != null) {
                // ローテーション時に自動的にViewを削除します
                if (adg != null) {
                    adg.setAutomaticallyRemoveOnReload(view);
                }

                binding.adContainer.addView(view);
            }
        }

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
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
