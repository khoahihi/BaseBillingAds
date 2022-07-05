package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.BillingMapper.Companion.mapping
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class BaseModuleApplication : AdsApplication() {
    override val prodInAppIds: List<String>
        get() = listOf("sub.pkg.interstitial", "sub.pkg.interstitial", "sub.pkg.interstitial", "sub.pkg.interstitial")

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
        MoneyManager.addMoney("US$100000000")
        BackgroundManager.attach(this)
    }

    override fun addConfig() {
        AdsComponentConfig
            .updateCurrency("GOLD")
            .updateInterstitialKey("")
            .updateBannerKey("")
            .updateBillingMapper(
                "abcd" mapping "5000",
                "bcde" mapping "10000",
            ).addActivitiesNonLoadBackground(MainActivity::class.java.name)
    }

    companion object {
        lateinit var instance: BaseModuleApplication
    }
}