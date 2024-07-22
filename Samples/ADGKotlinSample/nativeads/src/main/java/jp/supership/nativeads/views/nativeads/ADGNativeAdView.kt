package jp.supership.nativeads.views.nativeads

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.socdm.d.adgeneration.nativead.ADGInformationIconView
import com.socdm.d.adgeneration.nativead.ADGMediaView
import com.socdm.d.adgeneration.nativead.ADGNativeAd
import jp.supership.nativeads.R
import java.net.URL

class ADGNativeAdView : RelativeLayout {

    private var activity: Activity? = null
    private lateinit var container: RelativeLayout
    private lateinit var iconImageView: ImageView
    private lateinit var titleLabel: TextView
    private lateinit var descLabel: TextView
    private lateinit var mediaViewContainer: FrameLayout
    private lateinit var sponsoredLabel: TextView
    private lateinit var CTALabel: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (context is Activity) {
            activity = context
        }
        val layout = LayoutInflater.from(context).inflate(R.layout.adg_nativead_view, this)
        container = layout.findViewById(R.id.adg_nativead_view_container)
        iconImageView = layout.findViewById(R.id.adg_nativead_view_icon)
        titleLabel = layout.findViewById(R.id.adg_nativead_view_title)
        titleLabel.text = ""
        descLabel = layout.findViewById(R.id.adg_nativead_view_desc)
        descLabel.text = ""
        mediaViewContainer = layout.findViewById(R.id.adg_nativead_view_mediaview_container)
        sponsoredLabel = layout.findViewById(R.id.adg_nativead_view_sponsored)
        CTALabel = layout.findViewById(R.id.adg_nativead_view_cta)
        CTALabel.text = ""

        val borders = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 10f
            setStroke(3, CTALabel.textColors.defaultColor)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            CTALabel.background = borders
        } else {
            CTALabel.setBackgroundDrawable(borders)
        }
    }

    fun apply(nativeAd: ADGNativeAd) {
        nativeAd.iconImage?.url?.let {
            DownloadImageAsync(iconImageView).execute(it)
        }

        nativeAd.title?.text?.let {
            titleLabel.text = it
        }

        nativeAd.desc?.value?.let {
            descLabel.text = it
        }

        if (nativeAd.canLoadMedia()) {
            val mediaView = ADGMediaView(activity)
            mediaView.setAdgNativeAd(nativeAd)
            mediaViewContainer.addView(mediaView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            mediaView.load()
        }

        val infoIcon = ADGInformationIconView(context, nativeAd)
        mediaViewContainer.addView(infoIcon)

        sponsoredLabel.text = nativeAd.sponsored?.value ?: "sponsored"

        CTALabel.text = nativeAd.ctatext?.value ?: "詳しくはこちら"

        nativeAd.setClickEvent(context, container, null)
    }

    private inner class DownloadImageAsync(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg params: String): Bitmap? {
            return try {
                val imageUrl = params[0]
                BitmapFactory.decodeStream(URL(imageUrl).openStream())
            } catch (e: Exception) {
                Log.e("Error", e.message.orEmpty())
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            imageView.setImageBitmap(bitmap)
        }
    }
}
