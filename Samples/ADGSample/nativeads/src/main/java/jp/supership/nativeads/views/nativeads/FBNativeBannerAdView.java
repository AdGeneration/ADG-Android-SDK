package jp.supership.nativeads.views.nativeads;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeBannerAd;


import java.util.ArrayList;
import java.util.List;

import jp.supership.nativeads.R;

public class FBNativeBannerAdView extends RelativeLayout{

    private Context mContext;
    private RelativeLayout mContainer;
    private AdIconView mIconImageView;
    private TextView mSocialContextLabel, mCTALabel, mTitleLabel;

    public FBNativeBannerAdView(Context context) {
        this(context, null);
    }

    public FBNativeBannerAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FBNativeBannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public FBNativeBannerAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;

        View layout         = LayoutInflater.from(context).inflate(R.layout.fb_native_bannerad_view, this);
        mContainer          = (RelativeLayout) layout.findViewById(R.id.fb_native_bannerad_view_container);
        mIconImageView      = (AdIconView) layout.findViewById(R.id.fb_nativead_view_icon);
        mTitleLabel         = (TextView) layout.findViewById(R.id.fb_nativead_view_title);
        mSocialContextLabel = (TextView) layout.findViewById(R.id.fb_nativead_view_social_context);
        mCTALabel           = (TextView) layout.findViewById(R.id.fb_nativead_view_cta);

        mTitleLabel.setText("");
        mCTALabel.setText("");
        mSocialContextLabel.setText("");

        if (mCTALabel.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) mCTALabel.getBackground();
            int colorCode = cd.getColor();
            GradientDrawable borders = new GradientDrawable();
            borders.setColor(colorCode);
            borders.setStroke(3, colorCode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mCTALabel.setBackground(borders);
            } else {
                mCTALabel.setBackgroundDrawable(borders);
            }
        }
    }

    public void apply(NativeBannerAd nativeAd) {

        // adChoices
        AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adChoicesView.getLayoutParams();
        layoutParams.addRule(ALIGN_PARENT_TOP);
        layoutParams.addRule(ALIGN_PARENT_LEFT);
        mContainer.addView(adChoicesView, layoutParams);

        // タイトル
        mTitleLabel.setText(nativeAd.getAdHeadline());


        // ソーシャルコンテキスト
        mSocialContextLabel.setText(nativeAd.getAdSocialContext());

        // CTA
        mCTALabel.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        mCTALabel.setText(nativeAd.getAdCallToAction());

        // クリックイベント
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(mTitleLabel);
        clickableViews.add(mCTALabel);
        clickableViews.add(mSocialContextLabel);

        nativeAd.registerViewForInteraction(mContainer, mIconImageView, clickableViews);
    }

}
