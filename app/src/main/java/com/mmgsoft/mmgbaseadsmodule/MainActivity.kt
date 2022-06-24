package com.mmgsoft.mmgbaseadsmodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.AdsConstant
import com.mmgsoft.modules.libs.billing.BillingManager
import com.mmgsoft.modules.libs.helpers.AdsPrefs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBuyItem1.setOnClickListener {
            AdsApplication.instance?.let {
                AdsPrefs.putBoolean(it, AdsPrefs.PREFS_BILLING_BUY_ITEM_1, true)
            }
        }

        btnBuyItem2.setOnClickListener {
            AdsApplication.instance?.let {
                AdsPrefs.putBoolean(it, AdsPrefs.PREFS_BILLING_BUY_ITEM_2, true)
            }
        }
    }
}