package jp.supership.nativeads;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;
import com.socdm.d.adgeneration.nativead.ADGNativeAd;
import jp.supership.nativeads.databinding.ActivityMainBinding;
import jp.supership.nativeads.views.nativeads.ADGNativeAdView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FrameLayout adContainer;
    private ADG adg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        adg = new ADG(this);

        // 管理画面から払い出された広告枠ID
        adg.setLocationId("48636");

        // テストモードを有効化
        //adg.setEnableTestMode(true);

        /**
         * 枠サイズ
         * AdFrameSize.SP：320x50, AdFrameSize.Large:320x100,
         * AdFrameSize.Rect:300x250, AdFrameSize.Tablet:728x90,
         * ADG.AdFrameSize.FREE.setSize(xxx, xxx):自由設定
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
        adContainer = binding.adContainer;
        adContainer.addView(adg);

        binding.btnStopstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adg != null) {
                    // 広告表示/ローテーション再開
                    adg.stop();
                    adg.start();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adg != null) {
            // ローテーション停止
            adg.pause();
        }
    }

    class AdListener extends ADGListener {
        private static final String TAG = "ADGListener";

        @Override
        public void onReceiveAd() {
            Log.d(TAG, "Received an ad.");
        }

        @Override
        public void onReceiveAd(Object o) {
            Log.d(TAG, "Received an ad.");

            View view = null;
            if (o instanceof ADGNativeAd) {
                ADGNativeAdView nativeAdView = new ADGNativeAdView(MainActivity.this);
                nativeAdView.apply((ADGNativeAd) o);
                view = nativeAdView;
            }

            if (view != null) {
                // ローテーション時に自動的にViewを削除します
                adg.setAutomaticallyRemoveOnReload(view);

                adContainer.addView(view);
            }
        }

        @Override
        public void onFailedToReceiveAd(ADGConsts.ADGErrorCode code) {
            Log.d(TAG, "Failed to receive an ad.");
            // ネットワーク不通/エラー多発/広告レスポンスなし 以外はリトライしてください
            switch (code) {
                case EXCEED_LIMIT:      // エラー多発
                case NEED_CONNECTION:   // ネットワーク不通
                case NO_AD:             // 広告レスポンスなし
                    break;
                default:
                    if (adg != null) {
                        adg.start();
                    }
                    break;
            }
        }

        @Override
        public void onClickAd() {
            Log.d(TAG, "Did click ad.");
        }
    }
}
