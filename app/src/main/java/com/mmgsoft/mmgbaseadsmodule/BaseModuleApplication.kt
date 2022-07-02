package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.helpers.UseCurrency
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class BaseModuleApplication : AdsApplication() {
    override val prodInAppIds: List<String>
        get() = listOf("sub.pkg.interstitial", "sub.pkg.interstitial", "sub.pkg.interstitial", "sub.pkg.interstitial")

    override val prodSubsIds: List<String>
        get() = listOf()

    override val billingType: BillingType
        get() = BillingType.AMAZON

    override fun onCreated() {
        instance = this
    }

    override fun addConfig() {
        AdsComponentConfig
            .updateCurrency("GOLD")
            .loadAssetsFromMyApp(this, "")
            .addBackgroundPrice("10", "100", "50", "20", "15", "60")
            .updateItem1("lasd.asdals")
            .updateItem2("asdalsk.asd")
    }

    companion object {
        lateinit var instance: BaseModuleApplication
    }
}