package jp.supership.nativesample.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

import jp.supership.nativesample.utilities.Utilities;

public class FANNativeAdHelper {

    /**
     * ネイティブ広告を作成します
     *
     * @param nativeAd com.facebook.ads.NativeAd
     * @param context  Context
     * @return ad
     */
    public static FrameLayout createAdView(NativeAd nativeAd, Context context) {
        final int FMP = FrameLayout.LayoutParams.MATCH_PARENT;
        final int LMP = LinearLayout.LayoutParams.MATCH_PARENT;
        final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;
        List<View> clickableViews = new ArrayList<>();

        // 広告枠の設定
        FrameLayout layout = new FrameLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(FMP, FMP));
        layout.setBackgroundColor(Color.WHITE);

        FrameLayout nativeAdContainer = new FrameLayout(context);
        nativeAdContainer.setLayoutParams(new FrameLayout.LayoutParams(
                Utilities.convertDpToPixel(context, 300),
                Utilities.convertDpToPixel(context, 250)));
        layout.addView(nativeAdContainer);

        // 広告枠のレイアウト
        LinearLayout nativeAdFrame = new LinearLayout(context);
        nativeAdFrame.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP));
        nativeAdFrame.setOrientation(LinearLayout.VERTICAL);
        nativeAdContainer.addView(nativeAdFrame);

        // Headerの設定
        LinearLayout headerWrapper = new LinearLayout(context);
        headerWrapper.setLayoutParams(new LinearLayout.LayoutParams(LMP, Utilities.convertDpToPixel(context, 30)));
        headerWrapper.setOrientation(LinearLayout.HORIZONTAL);
        headerWrapper.setPadding(0, 0, 0, Utilities.convertDpToPixel(context, 5));
        headerWrapper.setGravity(Gravity.LEFT);
        nativeAdFrame.addView(headerWrapper);

        // アイコンの設定
        ImageView nativeIcon = new ImageView(context);
        NativeAd.Image icon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(icon, nativeIcon);
        headerWrapper.addView(nativeIcon, new LinearLayout.LayoutParams(Utilities.convertDpToPixel(context, 30), LWC));
        clickableViews.add(nativeIcon);

        // タイトルの設定
        LinearLayout nativeAdTitle = new LinearLayout(context);
        nativeAdTitle.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP));
        TextView titleView = new TextView(context);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(16);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP));
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(Utilities.convertDpToPixel(context, 3), 0, 0, Utilities.convertDpToPixel(context, 3));
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        String title = nativeAd.getAdTitle();
        titleView.setText(title);
        clickableViews.add(titleView);

        nativeAdTitle.addView(titleView);
        headerWrapper.addView(nativeAdTitle);

        // 画像または動画のViewを設定
        FrameLayout nativeAdImage = new FrameLayout(context);
        nativeAdImage.setLayoutParams(new FrameLayout.LayoutParams(
                Utilities.convertDpToPixel(context, 300),
                Utilities.convertDpToPixel(context, 157)));
        nativeAdFrame.addView(nativeAdImage);
        MediaView mediaView = new MediaView(context);
        // 動画/静止画兼用のとき
        mediaView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mediaView.setNativeAd(nativeAd);
        nativeAdImage.addView(mediaView, new LinearLayout.LayoutParams(
                Utilities.convertDpToPixel(context, 300),
                Utilities.convertDpToPixel(context, 157)));

        // 本文をセット
        TextView nativeAdBody = new TextView(context);
        nativeAdBody.setLayoutParams(new LinearLayout.LayoutParams(LMP, Utilities.convertDpToPixel(context, 32)));
        nativeAdBody.setTextSize(12);
        nativeAdBody.setTextColor(Color.BLACK);
        nativeAdBody.setText(nativeAd.getAdBody());
        nativeAdBody.setLines(2);
        nativeAdBody.setEllipsize(TextUtils.TruncateAt.END);
        nativeAdFrame.addView(nativeAdBody);
        clickableViews.add(nativeAdBody);

        // footerの設定
        LinearLayout footerWrapper = new LinearLayout(context);
        footerWrapper.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP));
        footerWrapper.setOrientation(LinearLayout.HORIZONTAL);
        nativeAdFrame.addView(footerWrapper);

        LinearLayout footerLeftWrapper = new LinearLayout(context);
        footerLeftWrapper.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP, 1.0f));
        footerLeftWrapper.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        footerWrapper.addView(footerLeftWrapper);

        LinearLayout footerRightWrapper = new LinearLayout(context);
        footerRightWrapper.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP, 1.0f));
        footerRightWrapper.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        footerWrapper.addView(footerRightWrapper);

        // 広告マークの設定
        TextView prTextView = new TextView(context);
        prTextView.setText("AD");
        prTextView.setTextColor(Color.WHITE);
        prTextView.setTextSize(12);
        prTextView.setLayoutParams(new ViewGroup.LayoutParams(
                Utilities.convertDpToPixel(context, 37),
                LWC));
        prTextView.setBackgroundColor(Color.rgb(254, 144, 0));
        prTextView.setGravity(Gravity.CENTER);
        footerLeftWrapper.addView(prTextView);

        // ソーシャルコンテキストの設定
        TextView nativeSocialContext = new TextView(context);
        nativeSocialContext.setTextColor(Color.BLACK);
        nativeSocialContext.setWidth(Utilities.convertDpToPixel(context, 130));
        nativeSocialContext.setTextSize(11);
        nativeSocialContext.setPadding(Utilities.convertDpToPixel(context, 3), 0, 0, 0);
        Utilities.setOneLineAndEllipsisForTextView(nativeSocialContext);
        //〇〇人が利用中ですorドメインが表示される
        nativeSocialContext.setText(nativeAd.getAdSocialContext() != null ? nativeAd.getAdSocialContext() : "");
        footerLeftWrapper.addView(nativeSocialContext);
        clickableViews.add(nativeSocialContext);

        // ボタンの設定
        LinearLayout nativeAdButtonArea = new LinearLayout(context);
        nativeAdButtonArea.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP));
        nativeAdButtonArea.setGravity(Gravity.CENTER);

        TextView nativeAdButton = new TextView(context);
        nativeAdButton.setLayoutParams(new LinearLayout.LayoutParams(
                Utilities.convertDpToPixel(context, 130),
                Utilities.convertDpToPixel(context, 25)));
        nativeAdButton.setTextColor(Color.WHITE);
        nativeAdButton.setTextSize(10);
        nativeAdButton.setBackgroundColor(Color.rgb(51, 204, 102));
        nativeAdButton.setGravity(Gravity.CENTER);

        // ボタンにborderや角丸を設定
        GradientDrawable borders = new GradientDrawable();
        borders.setColor(Color.rgb(51, 204, 102));
        borders.setCornerRadius(10);
        borders.setStroke(3, Color.rgb(51, 204, 102));
        // setBackgroundDrawableはDeprecatedですが、古いバージョンの端末サポートのため使用しています
        nativeAdButton.setBackgroundDrawable(borders);
        Utilities.setOneLineAndEllipsisForTextView(nativeAdButton);
        nativeAdButton.setText(nativeAd.getAdCallToAction());
        nativeAdButtonArea.addView(nativeAdButton);
        footerRightWrapper.addView(nativeAdButtonArea);
        clickableViews.add(nativeAdButton);

        // AdChoiceの設定
        LinearLayout adChoiceContainer = new LinearLayout(context);
        adChoiceContainer.setGravity(Gravity.RIGHT);
        nativeAdImage.addView(adChoiceContainer, new LinearLayout.LayoutParams(LMP, LWC));
        AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
        adChoiceContainer.addView(adChoicesView);

        //広告をclickした時のイベントを追加
        nativeAd.registerViewForInteraction(layout, clickableViews);

        return layout;
    }
}
