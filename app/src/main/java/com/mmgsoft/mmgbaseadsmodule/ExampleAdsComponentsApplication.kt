package com.mmgsoft.mmgbaseadsmodule

import android.app.Application
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.helpers.StateAfterBuy
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.models.BillingMapper.Companion.mapping
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.AdsComponentConfig.addActivitiesNonLoadBackground

/**
 * Loại components này thay thế cho AdsApplication trong trường hợp cần thiết
 */
class ExampleAdsComponentsApplication : Application() {
    val adsComponents by lazy {
        AdsComponents.inject(this)
            .withProdInApp("")         /** optional **/
            .withProdSubs("")             /** optional **/
            .withProdAmazon("")           /** optional **/
            .withBillingState(StateAfterBuy.DISABLE)  /** optional **/
            .withBilling(BillingType.GOOGLE)          /** required **/
            .build(::addConfigs)                      /** required **/
    }

    private fun addConfigs() {
        AdsComponentConfig
            .updateInterstitialKey(ExampleAdMobID.ADS_CLEAR_INTERSTITIAL)
            .updateBannerKey(ExampleAdMobID.ADS_CLEAR_BANNER)
            .updateBillingMapper(
                ExampleAdMobID.ADS_IN_APP_1 mapping "1000",  /** example **/
                ExampleAdMobID.ADS_IN_APP_2 mapping "3000",  /** example **/
                ExampleAdMobID.ADS_IN_APP_3 mapping "5000",  /** example **/
                ExampleAdMobID.ADS_SUBS_1 mapping "5000",    /** example **/
                ExampleAdMobID.ADS_SUBS_2 mapping "5000",    /** example **/
                ExampleAdMobID.ADS_SUBS_3 mapping "5000"     /** example **/
            ).addActivitiesNonLoadBackground(AmazonIapActivity::class.java.name)

        BackgroundManager.attach(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        /**
         * Nếu sử dụng lazy giống example thì cần gọi adsComponents để biến thực hiện gọi init
         * Ngược lại nếu dùng JAVA và init ở onCreate thì không cần gọi lại
         */
        adsComponents
    }

    companion object {
        lateinit var instance: ExampleAdsComponentsApplication
    }
}