package com.mmgsoft.modules.libs

import android.app.Application
import com.mmgsoft.modules.libs.ads.AdsManager
import com.mmgsoft.modules.libs.billing.BillingManager
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.StateAfterBuy

abstract class AdsApplication : Application() {
    val adsManager by lazy {
        AdsManager().initAdsManager(this, testDevices.toMutableList())
    }

    protected abstract val testDevices: List<String>
    protected abstract val prodInAppIds: List<String>
    protected abstract val prodSubsIds: List<String>

    protected abstract fun onCreated()

    protected open fun getStateBilling() = StateAfterBuy.DISABLE

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = AdsPrefs()
        BillingManager.init(this, prodInAppIds, prodSubsIds, getStateBilling())
        onCreated()
    }

    companion object {
        lateinit var instance: AdsApplication
        internal lateinit var prefs: AdsPrefs
    }
}