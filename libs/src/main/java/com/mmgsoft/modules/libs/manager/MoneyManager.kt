package com.mmgsoft.modules.libs.manager

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.Config
import com.mmgsoft.modules.libs.utils.PREFS_MONEY
import java.text.NumberFormat
import java.util.*

object MoneyManager {
    private val prefs by lazy {
        AdsApplication.prefs
    }

    private fun exchange(money: String): Double {
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0
        numberFormat.currency = Currency.getInstance(Locale.US)
        return numberFormat.parse(money)?.let {
            it.toDouble() * Config.exchangeRate
        } ?: 0.0
    }

    /**
     * Lấy số tiền hiện tại
     */
    fun getCurrentGoldStr(): String {
        val money = prefs.getDouble(PREFS_MONEY)
        return "$money ${Config.currency}"
    }

    /**
     * Lấy số tiền hiện tại đang có
     */
    private fun getCurrentGold(): Double {
        return prefs.getDouble(PREFS_MONEY)
    }

    /**
     * Thực hiện cộng tiền khi billing thành công
     */
    fun buyBilling(money: String, onBuySuccess: () -> Unit, onBuyFailed: () -> Unit) {
        val newMoney = exchange(money)
        if(prefs.putDouble(PREFS_MONEY, newMoney + getCurrentGold())) {
            onBuySuccess.invoke()
        } else onBuyFailed.invoke()
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