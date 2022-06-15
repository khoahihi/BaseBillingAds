package com.mmgsoft.modules.libs

import android.content.Context
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import com.mmgsoft.modules.libs.helpers.AdsPrefs.KEY_PREFS_IS_BILLING

object AdsConstant {
    var keyCloseAds = "inapp.nonconsum.item1"

    fun isLoadAds(ctx: Context): Boolean {
        return !AdsPrefs.getBoolean(ctx, KEY_PREFS_IS_BILLING)
    }
}