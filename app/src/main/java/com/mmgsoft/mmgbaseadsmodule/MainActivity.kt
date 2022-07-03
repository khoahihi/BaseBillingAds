package com.mmgsoft.mmgbaseadsmodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mmgsoft.modules.libs.activity.ChangeBackgroundActivity
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.base.BaseAutoBackgroundActivity
import com.mmgsoft.modules.libs.helpers.UseCurrency
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.MoneyManager
import kotlinx.android.synthetic.main.activity_main.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
//    override val backgroundImageView: Int
//        get() = R.id.imBackground
//
//    override val isAutoLoadWhenRandomBackground: Boolean
//        get() = true
//
//    override val layoutResId: Int
//        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnBuyItem1.setOnClickListener {
//            MoneyManager.addMoney("100$")
            startActivity(Intent(this, TestBackgroundActivity::class.java))
        }

        btnChangeBackground.setOnClickListener {
            ChangeBackgroundActivity.open(this)
        }
    }

//    override fun initViews() {
//        btnBuyItem1.setOnClickListener {
////            MoneyManager.addMoney("100$")
//            startActivity(Intent(this, AmazonIapActivity::class.java))
//        }
//
//        btnChangeBackground.setOnClickListener {
//            ChangeBackgroundActivity.open(this)
//        }
//    }

    override fun onResume() {
        super.onResume()
        BackgroundManager.loadBackground(this)
    }
}