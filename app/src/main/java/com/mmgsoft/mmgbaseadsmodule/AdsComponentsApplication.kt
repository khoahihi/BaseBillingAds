package com.mmgsoft.mmgbaseadsmodule

import android.app.Application
import com.mmgsoft.modules.libs.AdsComponents
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.BillingMapper.Companion.mapping
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class AdsComponentsApplication : Application() {
    val adsComponents by lazy {
        AdsComponents.inject(this)
            .withProdAmazon("")
            .withBilling(BillingType.AMAZON)
            .build(::addConfigs)
    }

    private fun addConfigs() {
        AdsComponentConfig
            .updateInterstitialKey("")
            .updateBannerKey("")
            .updateBillingMapper(
                "subs.item1" mapping "1000",
                "subs.item2" mapping "3000",
                "subs.item3" mapping "5000"
            ).addActivitiesNonLoadBackground(AmazonIapActivity::class.java.name)

        BackgroundManager.attach(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        adsComponents
        MoneyManager.addMoney("US$1000", 100.0)
    }

    companion object {
        lateinit var instance: AdsComponentsApplication
    }
}