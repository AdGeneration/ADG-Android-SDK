package jp.supership.interstitialads;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.socdm.d.adgeneration.ADGConsts;
import com.socdm.d.adgeneration.interstitial.ADGInterstitial;
import com.socdm.d.adgeneration.interstitial.ADGInterstitialListener;

import jp.supership.interstitialads.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ADGInterstitial adgInterstitial = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // showボタンを非活性にしておく
        binding.btnShow.setEnabled(false);

        adgInterstitial = new ADGInterstitial(this);

        // 管理画面から払い出された広告枠ID
        adgInterstitial.setLocationId("48549");

        // テストモードを有効化
        //adgInterstitial.setEnableTestMode(true);

        // Listenerの設定
        adgInterstitial.setAdListener(new AdListener());

        binding.btnPreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 広告リクエスト
                adgInterstitial.preload();
            }
        });

        binding.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 広告表示
                adgInterstitial.show();
                // showボタンを非活性にしておく
                binding.btnShow.setEnabled(false);
            }
        });
    }

    @Override
    protected void onStop() {
        // 広告非表示
        adgInterstitial.dismiss();
        super.onStop();
    }

    class AdListener extends ADGInterstitialListener {
        private static final String TAG = "ADGListener";

        @Override
        public void onReceiveAd() {
            Log.d(TAG, "Received an ad.");
            binding.btnShow.setEnabled(true);
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
                    if (adgInterstitial != null) {
                        adgInterstitial.preload();
                    }
                    break;
            }
        }

        @Override
        public void onClickAd() {
            Log.d(TAG, "Did click ad.");
        }

        @Override
        public void onCloseInterstitial() {
            Log.d(TAG, "Did close interstitial ads.");
        }
    }
}
