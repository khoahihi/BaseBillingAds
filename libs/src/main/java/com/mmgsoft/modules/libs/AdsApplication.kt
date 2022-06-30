package com.mmgsoft.modules.libs

import android.app.Application
import androidx.room.Room
import com.mmgsoft.modules.libs.ads.AdsManager
import com.mmgsoft.modules.libs.amzbiling.AppConstant
import com.mmgsoft.modules.libs.billing.BillingManager
import com.mmgsoft.modules.libs.data.local.db.AppDatabase
import com.mmgsoft.modules.libs.data.local.db.AppDbHelper
import com.mmgsoft.modules.libs.data.local.db.DbHelper
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.helpers.StateAfterBuy

abstract class AdsApplication : Application() {
    val adsManager by lazy {
        AdsManager().initAdsManager(this, testDevices.toMutableList())
    }

    protected abstract val testDevices: List<String>
    protected abstract val prodInAppIds: List<String>
    protected abstract val prodSubsIds: List<String>
    protected abstract val billingType: BillingType

    protected abstract fun onCreated()
    protected open fun getStateBilling() = StateAfterBuy.DISABLE

    /**
     * Thực hiện thêm cấu hình cho Library
     * AdsComponentConfig.(...)
     */
    protected open fun addConfig() {}
    var dbHelper: DbHelper? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = AdsPrefs()
        addConfig()
        if (billingType == BillingType.GOOGLE)
            BillingManager.init(this, prodInAppIds, prodSubsIds, getStateBilling())
        else {
            val appDatabase: AppDatabase = Room.databaseBuilder(
                this,
                AppDatabase::class.java, AppConstant.DB_NAME
            ).fallbackToDestructiveMigration()
                .build()
            dbHelper = AppDbHelper(appDatabase)
        }
        onCreated()
    }

    companion object {
        lateinit var instance: AdsApplication
        internal lateinit var prefs: AdsPrefs
    }
}