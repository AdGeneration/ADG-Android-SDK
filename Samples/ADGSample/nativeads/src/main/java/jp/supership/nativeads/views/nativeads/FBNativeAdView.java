package jp.supership.nativeads.views.nativeads;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.Arrays;

import jp.supership.nativeads.R;

public class FBNativeAdView extends RelativeLayout {

    private Context mContext;
    private RelativeLayout mContainer;
    private ImageView mIconImageView;
    private TextView mTitleLabel;
    private TextView mBodyLabel;
    private RelativeLayout mMediaViewContainer;
    private TextView mSocialContextLabel;
    private TextView mCTALabel;

    public FBNativeAdView(Context context) {
        this(context, null);
    }

    public FBNativeAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FBNativeAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public FBNativeAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        View layout = LayoutInflater.from(context).inflate(R.layout.fb_nativead_view, this);
        mContainer = (RelativeLayout) layout.findViewById(R.id.fb_nativead_view_container);
        mIconImageView = (ImageView) layout.findViewById(R.id.fb_nativead_view_icon);
        mTitleLabel = (TextView) layout.findViewById(R.id.fb_nativead_view_title);
        mTitleLabel.setText("");
        mBodyLabel = (TextView) layout.findViewById(R.id.fb_nativead_view_body);
        mBodyLabel.setText("");
        mMediaViewContainer = (RelativeLayout) layout.findViewById(R.id.fb_nativead_view_media_view_container);
        mSocialContextLabel = (TextView) layout.findViewById(R.id.fb_nativead_view_social_context);
        mCTALabel = (TextView) layout.findViewById(R.id.fb_nativead_view_cta);
        mCTALabel.setText("");

        if (mCTALabel.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) mCTALabel.getBackground();
            int colorCode = cd.getColor();
            GradientDrawable borders = new GradientDrawable();
            borders.setColor(colorCode);
            borders.setCornerRadius(10);
            borders.setStroke(3, colorCode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mCTALabel.setBackground(borders);
            } else {
                mCTALabel.setBackgroundDrawable(borders);
            }
        }
    }

    public void apply(NativeAd nativeAd) {

        // アイコン画像
        NativeAd.Image icon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(icon, mIconImageView);

        // タイトル
        mTitleLabel.setText(nativeAd.getAdTitle());

        // MediaView
        MediaView mediaView = new MediaView(mContext);
        mediaView.setNativeAd(nativeAd);
        mMediaViewContainer.addView(mediaView);

        // 本文
        mBodyLabel.setText(nativeAd.getAdBody());

        // ソーシャルコンテキスト
        mSocialContextLabel.setText(nativeAd.getAdSocialContext());

        // CTA
        mCTALabel.setText(nativeAd.getAdCallToAction());

        // AdChoice
        AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adChoicesView.getLayoutParams();
        layoutParams.addRule(ALIGN_PARENT_TOP);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        mMediaViewContainer.addView(adChoicesView, layoutParams);

        // クリックイベント
        nativeAd.registerViewForInteraction(mContainer,
                Arrays.asList(mIconImageView, mTitleLabel, mMediaViewContainer, mBodyLabel, mSocialContextLabel, mCTALabel));
    }
}
