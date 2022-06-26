package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.activity.ChangeBackgroundActivity
import com.mmgsoft.modules.libs.base.BaseAutoBackgroundActivity
import com.mmgsoft.modules.libs.manager.MoneyManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseAutoBackgroundActivity() {
    override val backgroundImageView: Int
        get() = R.id.imBackground

    override val isAutoLoadWhenRandomBackground: Boolean
        get() = true

    override val layoutResId: Int
        get() = R.layout.activity_main

    override fun initViews() {
        btnBuyItem1.setOnClickListener {
            MoneyManager.addMoney("100$")
        }

        btnChangeBackground.setOnClickListener {
            ChangeBackgroundActivity.open(this)
        }
    }
}