package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.models.BillingMapper.Companion.mapping
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
            .updateItem1("")
            .updateItem2("")
            .updateBillingMapper(
                "abcd" mapping "5000",
                "bcde" mapping "10000",
            )
    }

    companion object {
        lateinit var instance: BaseModuleApplication
    }
}