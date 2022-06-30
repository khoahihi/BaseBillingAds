package com.mmgsoft.modules.libs.manager

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.PREFS_MONEY
import java.text.NumberFormat
import java.util.*

object MoneyManager {
    private val prefs by lazy {
        AdsApplication.prefs
    }

    /**
     * Chuyển từ tiền DOLLAR sang loại tiền của app
     */
    private fun exchange(money: String, rate: Double = AdsComponentConfig.exchangeRate): Double {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0
        numberFormat.currency = Currency.getInstance(Locale.US)
        return numberFormat.parse(money)?.let {
            it.toDouble() * rate
        } ?: 0.0
    }

    /**
     * Lấy số tiền hiện tại
     */
    fun getCurrentGoldStr(): String {
        val money = prefs.getDouble(PREFS_MONEY)
        return "$money ${AdsComponentConfig.currency}"
    }

    /**
     * Lấy số tiền hiện tại đang có
     */
    fun getCurrentGold(): Double {
        return prefs.getDouble(PREFS_MONEY)
    }

    /**
     * Thực hiện cộng tiền khi billing thành công
     */
    fun addMoney(money: String, rate: Double = AdsComponentConfig.exchangeRate): Boolean {
        val newMoney = exchange(money, rate)
        return prefs.putDouble(PREFS_MONEY, newMoney + getCurrentGold())
    }

    /**
     * Thực hiện trừ tiền khi mua backgrounds
     */
    fun buyBackground(background: Background): Boolean {
        val currentGold = getCurrentGold()
        return if(currentGold < background.price.toDouble()) {
            false
        } else prefs.putDouble(PREFS_MONEY, getCurrentGold() - background.price.toDouble())
    }
}