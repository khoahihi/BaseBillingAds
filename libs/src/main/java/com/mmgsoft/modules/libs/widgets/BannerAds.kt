package com.mmgsoft.modules.libs.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.mmgsoft.modules.libs.AdsConstant
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.ads.AdsManager

class BannerAds @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var adsUnitId: String? = null
    private var isAutoLoad = false
    private var isCloseAd = false

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.BannerAds)
        adsUnitId = typeArray.getString(R.styleable.BannerAds_ba_adsUnitId)
        isAutoLoad = typeArray.getBoolean(R.styleable.BannerAds_ba_autoLoad, false)
        typeArray.recycle()
        View.inflate(context, R.layout.view_banner_ads, this)
        initViews()
    }

    private fun initViews() {
        if(!adsUnitId.isNullOrBlank() && isAutoLoad && !isCloseAd) {
            loadBanner(adsUnitId!!)
        }
    }

    fun load(adsUnitId: String) {
        if(!isAutoLoad && !isCloseAd) loadBanner(adsUnitId)
    }

    fun load() {
        if(!isAutoLoad && !adsUnitId.isNullOrBlank() && !isCloseAd) {
            loadBanner(adsUnitId!!)
        }
    }

    fun close() {
        isCloseAd = true
        this.visibility = View.GONE
    }

    private fun loadBanner(adsUnitId: String) {
        if(!AdsConstant.isLoadAds(context)) {
            close()
            return
        }
        AdsManager().showAdModBanner(context, adsUnitId, findViewById(R.id.bannerContainer), findViewById(R.id.shimmerContainerBanner)) {
            this.visibility = View.GONE
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if(!AdsConstant.isLoadAds(context)) {
            close()
        }
    }
}