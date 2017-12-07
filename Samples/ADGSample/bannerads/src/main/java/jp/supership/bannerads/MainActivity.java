package jp.supership.bannerads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.socdm.d.adgeneration.ADG;
import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.ADGListener;

public class MainActivity extends AppCompatActivity {

    private ADG adg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adg = new ADG(this);

        // 管理画面から払い出された広告枠ID
        adg.setLocationId("48547");

        /**
         * 枠サイズ
         * AdFrameSize.SP：320x50, AdFrameSize.Large:320x100,
         * AdFrameSize.Rect:300x250, AdFrameSize.Tablet:728x90,
         * ADG.AdFrameSize.FREE.setSize(xxx, xxx):自由設定
         */
        adg.setAdFrameSize(ADG.AdFrameSize.SP);

        // Listenerの設定
        adg.setAdListener(new AdListener());

        // 広告の配置
        FrameLayout ad_container = (FrameLayout) findViewById(R.id.ad_container);
        ad_container.addView(adg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adg != null) {
            // 広告表示/ローテーション再開
            adg.start();
        }
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
