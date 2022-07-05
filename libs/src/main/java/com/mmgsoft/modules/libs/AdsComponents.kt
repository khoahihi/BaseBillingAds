package com.mmgsoft.modules.libs

import android.app.Application
import androidx.room.Room
import com.mmgsoft.modules.libs.ads.AdsManager
import com.mmgsoft.modules.libs.amzbiling.AppConstant
import com.mmgsoft.modules.libs.billing.BillingManager
import com.mmgsoft.modules.libs.data.local.db.AppDatabase
import com.mmgsoft.modules.libs.data.local.db.AppDbHelper
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.helpers.StateAfterBuy
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

class AdsComponents(private val setup: Setup,
                    private val addConfig: () -> Unit = {}) {
    val adsManager by lazy {
        AdsManager().initAdsManager(setup.application, AdsComponentConfig.testDevices.toMutableList())
    }

    private val dbHelper by lazy {
        val appDatabase: AppDatabase = Room.databaseBuilder(
            setup.application,
            AppDatabase::class.java, AppConstant.DB_NAME
        ).fallbackToDestructiveMigration().build()
        AppDbHelper(appDatabase)
    }

    init {
        setup.apply {
            AdsApplication.prefs = AdsPrefs()
            AdsApplication.dbHelper = dbHelper
            AdsApplication.application = application
            AdsComponentConfig.updateBillingType(billingType)
            addConfig.invoke()
            if(setup.billingType == BillingType.GOOGLE) {
                BillingManager.init(application, prodInAppIds, prodSubsIds, stateBilling)
            } else {
                AdsComponentConfig.addAmazonItem(prodInAppIds)
            }
        }
    }

    class Setup(internal val application: Application) {
        internal val prodAmazonIds = mutableListOf<String>()
        internal val prodInAppIds = mutableListOf<String>()
        internal val prodSubsIds = mutableListOf<String>()
        internal var billingType = BillingType.GOOGLE
        internal var stateBilling = StateAfterBuy.DISABLE

        fun withProdInApp(newProdInAppIds: List<String>): Setup {
            prodInAppIds.clear()
            prodInAppIds.addAll(newProdInAppIds)
            return this
        }

        fun withProdInApp(vararg newProdInAppIds: String): Setup {
            prodInAppIds.clear()
            prodInAppIds.addAll(newProdInAppIds)
            return this
        }

        fun withProdSubs(newProdSubs: List<String>): Setup {
            prodSubsIds.clear()
            prodSubsIds.addAll(newProdSubs)
            return this
        }

        fun withProdSubs(vararg newProdSubs: String): Setup {
            prodSubsIds.clear()
            prodSubsIds.addAll(newProdSubs)
            return this
        }

        fun withProdAmazon(newProdSubs: List<String>): Setup {
            prodAmazonIds.clear()
            prodAmazonIds.addAll(newProdSubs)
            return this
        }

        fun withProdAmazon(vararg newProdSubs: String): Setup {
            prodAmazonIds.clear()
            prodAmazonIds.addAll(newProdSubs)
            return this
        }

        fun withBilling(type: BillingType): Setup {
            this.billingType = type
            return this
        }

        fun withBillingState(stateAfterBuy: StateAfterBuy): Setup {
            this.stateBilling = stateAfterBuy
            return this
        }

        fun build(): AdsComponents {
            return AdsComponents(this)
        }

        fun build(addConfig: () -> Unit): AdsComponents {
            return AdsComponents(this, addConfig)
        }
    }

    private fun setup() {

    }

    companion object {
        fun inject(application: Application): Setup {
            return Setup(application)
        }
    }
}