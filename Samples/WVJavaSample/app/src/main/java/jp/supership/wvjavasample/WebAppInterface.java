package jp.supership.wvjavasample;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

public class WebAppInterface {

    private static final String TAG = "WebAppInterface";
    private String advertising_id = "";
    private boolean isLAT = false;
    private Context context;

    public WebAppInterface(Context ct) {
        this.context = ct;
        this.initAdIdThread();
    }

    private void initAdIdThread() {
        Thread adIdThread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                AdvertisingIdClient.Info adInfo = null;
                                try {
                                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                                    advertising_id = adInfo.getId();
                                    isLAT = adInfo.isLimitAdTrackingEnabled();
                                } catch (Exception e) {
                                    Log.d(TAG, "getAdId failed");
                                }
                            }
                        });
        adIdThread.start();
    }

    @JavascriptInterface
    public String getAppBundle() {
        return context.getPackageName();
    }

    @JavascriptInterface
    public String getAdvertisingId() {
        String adid = advertising_id;
        return !isLAT ? adid : "";
    }
}
