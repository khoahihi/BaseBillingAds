package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class BaseModuleApplication : AdsApplication() {
    override val testDevices: List<String>
        get() = listOf()

    override val prodInAppIds: List<String>
        get() = listOf()

    override val prodSubsIds: List<String>
        get() = listOf()
    override val billingType: BillingType
        get() = BillingType.GOOGLE

    override fun onCreated() {
        instance = this
    }

    override fun addConfig() {
        AdsComponentConfig
            .updateCurrency("GOLD")
            .loadAssetsFromMyApp(this, "")
            .addBackgroundPrice("10", "100", "50", "20", "15", "60")
    }

    companion object {
        lateinit var instance: BaseModuleApplication
    }
}