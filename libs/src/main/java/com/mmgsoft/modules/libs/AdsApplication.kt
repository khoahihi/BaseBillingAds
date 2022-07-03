package com.mmgsoft.modules.libs

import android.app.Application
import android.content.Intent
import android.os.Bundle
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
import com.mmgsoft.modules.libs.helpers.UseCurrency
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.AdsComponentConfig.testDevices
import com.mmgsoft.modules.libs.utils.AnnotationConverter

abstract class AdsApplication : Application() {
    val adsManager by lazy {
        AdsManager().initAdsManager(this, testDevices.toMutableList())
    }

    /**
     * Billing Type:
     *     - AMAZON: trả về toàn bộ ID vào prodInAppIds
     *     - GOOGLE: trả về danh sách inapp
     */
    abstract val prodInAppIds: List<String>

    /**
     * Trả về danh sách subs
     */
    abstract val prodSubsIds: List<String>

    /**
     * Trả về billing type để init trong application
     */
    abstract val billingType: BillingType

    /**
     * function được gọi trước khi init billing
     */
    protected abstract fun onCreated()
    protected open fun getStateBilling() = StateAfterBuy.DISABLE

    /**
     * Thực hiện thêm cấu hình cho Library
     * AdsComponentConfig.(...)
     */
    protected open fun addConfig() {}

    protected inline fun <reified T: AdsApplication> bind(clz: T) {
        AnnotationConverter.get(T::class.java)
    }

    var dbHelper: DbHelper? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = AdsPrefs()
        AdsComponentConfig.updateBillingType(billingType)
        addConfig()
        if (billingType == BillingType.GOOGLE)
            BillingManager.init(this, prodInAppIds, prodSubsIds, getStateBilling())
        else {
            AdsComponentConfig.addAmazonItem(prodInAppIds)
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