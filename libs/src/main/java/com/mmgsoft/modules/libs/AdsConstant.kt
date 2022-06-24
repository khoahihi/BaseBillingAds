package com.mmgsoft.modules.libs

import android.content.Context
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.AdsPrefs.PREFS_BILLING_BUY_ITEM_1
import com.mmgsoft.modules.libs.helpers.AdsPrefs.PREFS_BILLING_BUY_ITEM_2

object AdsConstant {
    var item1 = "inapp.nonconsum.item1"
    var item2 = "inapp.nonconsum.item2"

    fun isLoadAds(ctx: Context): Boolean {
        return !AdsPrefs.getBoolean(ctx, PREFS_BILLING_BUY_ITEM_1)
    }

    fun isBuyItem1(ctx: Context): Boolean {
        return AdsPrefs.getBoolean(ctx, PREFS_BILLING_BUY_ITEM_1)
    }

    fun isBuyItem2(ctx: Context): Boolean {
        return AdsPrefs.getBoolean(ctx, PREFS_BILLING_BUY_ITEM_2)
    }
}