package com.mmgsoft.modules.libs.manager

import android.content.Context
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.activity.PurchaseActivity
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.helpers.ActionBarTheme
import com.mmgsoft.modules.libs.helpers.AmazonScreenType
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.utils.AdsComponentConfig

object PurchaseManager {
    fun open(ctx: Context) {
        if(AdsComponentConfig.billingType == BillingType.AMAZON) {
            AmazonIapActivity.open(ctx, AmazonScreenType.SUBSCRIPTION)
        } else {
            PurchaseActivity.open(ctx, ActionBarTheme.DARK_MODE, R.color.white, R.color.black)
        }
    }
}