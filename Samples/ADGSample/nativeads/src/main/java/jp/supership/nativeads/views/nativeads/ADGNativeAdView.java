package jp.supership.nativeads.views.nativeads;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socdm.d.adgeneration.nativead.ADGInformationIconView;
import com.socdm.d.adgeneration.nativead.ADGMediaView;
import com.socdm.d.adgeneration.nativead.ADGNativeAd;

import java.net.URL;

import jp.supership.nativeads.R;

public class ADGNativeAdView extends RelativeLayout {

    private Activity mActivity;
    private RelativeLayout mContainer;
    private ImageView mIconImageView;
    private TextView mTitleLabel;
    private TextView mDescLabel;
    private FrameLayout mMediaViewContainer;
    private TextView mSponsoredLabel;
    private TextView mCTALabel;

    public ADGNativeAdView(Context context) {
        this(context, null);
    }

    public ADGNativeAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ADGNativeAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ADGNativeAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
        View layout = LayoutInflater.from(context).inflate(R.layout.adg_nativead_view, this);
        mContainer = (RelativeLayout) layout.findViewById(R.id.adg_nativead_view_container);
        mIconImageView = (ImageView) layout.findViewById(R.id.adg_nativead_view_icon);
        mTitleLabel = (TextView) layout.findViewById(R.id.adg_nativead_view_title);
        mTitleLabel.setText("");
        mDescLabel = (TextView) layout.findViewById(R.id.adg_nativead_view_desc);
        mDescLabel.setText("");
        mMediaViewContainer = (FrameLayout) layout.findViewById(R.id.adg_nativead_view_mediaview_container);
        mSponsoredLabel = (TextView) layout.findViewById(R.id.adg_nativead_view_sponsored);
        mCTALabel = (TextView) layout.findViewById(R.id.adg_nativead_view_cta);
        mCTALabel.setText("");

        GradientDrawable borders = new GradientDrawable();
        borders.setColor(Color.WHITE);
        borders.setCornerRadius(10);
        borders.setStroke(3, mCTALabel.getTextColors().getDefaultColor());
        mCTALabel.setBackground(borders);
    }

    public void apply(ADGNativeAd nativeAd) {

        // アイコン画像
        if (nativeAd.getIconImage() != null) {
            String url = nativeAd.getIconImage().getUrl();
            new DownloadImageAsync(mIconImageView).execute(url);
        }

        // タイトル
        if (nativeAd.getTitle() != null) {
            mTitleLabel.setText(nativeAd.getTitle().getText());
        }

        // リード文
        if (nativeAd.getDesc() != null) {
            String desc = nativeAd.getDesc().getValue();
            mDescLabel.setText(desc);
        }

        // メイン画像・動画
        if (nativeAd.canLoadMedia()) {
            ADGMediaView mediaView = new ADGMediaView(mActivity);
            mediaView.setAdgNativeAd(nativeAd);
            mMediaViewContainer.addView(mediaView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mediaView.load();
        }

        // インフォメーションアイコン
        ADGInformationIconView infoIcon = new ADGInformationIconView(getContext(), nativeAd);
        mMediaViewContainer.addView(infoIcon);

        // 広告主
        if (nativeAd.getSponsored() != null) {
            mSponsoredLabel.setText(nativeAd.getSponsored().getValue());
        } else {
            mSponsoredLabel.setText("sponsored");
        }

        // CTA
        if (nativeAd.getCtatext() != null) {
            mCTALabel.setText(nativeAd.getCtatext().getValue());
        } else {
            mCTALabel.setText("詳しくはこちら");
        }

        // クリックイベント
        nativeAd.setClickEvent(getContext(), mContainer, null);
    }

    /**
     * 画像をロードします(方法については任意で行ってください)
     */
    private class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageAsync(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String imageUrl = params[0];
                return BitmapFactory.decodeStream(new URL(imageUrl).openStream());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.imageView.setImageBitmap(bitmap);
        }
    }
}
