package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.BillingMapper.Companion.mapping
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class ExampleAdsApplication : AdsApplication() {
    override val prodInAppIds: List<String>
        get() = listOf(
            ExampleAdMobID.ADS_IN_APP_1,  /** example **/
            ExampleAdMobID.ADS_IN_APP_2,  /** example **/
            ExampleAdMobID.ADS_IN_APP_3   /** example **/
        )

    override val prodSubsIds: List<String>
        get() = listOf()

    override val billingType: BillingType
        get() = BillingType.AMAZON

    private val adsComponents by lazy {
        AdsComponents.inject(this)
            .withProdInApp()
            .withProdSubs()
            .build()
    }

    override fun onCreated() {
        instance = this
    }

    override fun addConfig() {
        AdsComponentConfig
            .updateCurrency("GOLD")
            .updateInterstitialKey(ExampleAdMobID.ADS_CLEAR_INTERSTITIAL)
            .updateBannerKey(ExampleAdMobID.ADS_CLEAR_BANNER)
            .updateBillingMapper(
                ExampleAdMobID.ADS_IN_APP_1 mapping "1000",  /** example **/
                ExampleAdMobID.ADS_IN_APP_2 mapping "3000",  /** example **/
                ExampleAdMobID.ADS_IN_APP_3 mapping "5000",  /** example **/
                ExampleAdMobID.ADS_SUBS_1 mapping "5000",    /** example **/
                ExampleAdMobID.ADS_SUBS_2 mapping "5000",    /** example **/
                ExampleAdMobID.ADS_SUBS_3 mapping "5000"     /** example **/
            ).addActivitiesNonLoadBackground(MainActivity::class.java.name)

        BackgroundManager.attach(this)
    }

    companion object {
        lateinit var instance: ExampleAdsApplication
    }
}