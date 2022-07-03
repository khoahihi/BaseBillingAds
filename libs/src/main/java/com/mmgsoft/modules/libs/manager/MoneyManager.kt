package com.mmgsoft.modules.libs.manager

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.etx.toCurrency
import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.PREFS_MONEY
import java.text.NumberFormat
import java.util.*

object MoneyManager {
    private val prefs by lazy {
        AdsApplication.prefs
    }

    internal val amazonCurrencies = listOf(
        AmazonCurrency.US.toCurrency(),
        AmazonCurrency.CA.toCurrency(),
        AmazonCurrency.BR.toCurrency(),
        AmazonCurrency.MX.toCurrency(),
        AmazonCurrency.GB.toCurrency(),
        AmazonCurrency.DE.toCurrency(),
        AmazonCurrency.ES.toCurrency(),
        AmazonCurrency.FR.toCurrency(),
        AmazonCurrency.IT.toCurrency(),
        AmazonCurrency.IN.toCurrency(),
        AmazonCurrency.JP.toCurrency(),
        AmazonCurrency.AU.toCurrency(),
    )

    /**
     * Chuyển từ tiền DOLLAR sang loại tiền của app
     */
    private fun exchange(money: String, rate: Double = AdsComponentConfig.exchangeRate): Double {
        val m = if(AdsComponentConfig.billingType == BillingType.GOOGLE) money else getMoneyWithAmazonCurrency(money)
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0
        numberFormat.currency = Currency.getInstance(Locale.US)
        return numberFormat.parse(m)?.let {
            it.toDouble() * rate
        } ?: 0.0
    }

    private fun getMoneyWithAmazonCurrency(money: String): String {
        var m = ""
        amazonCurrencies.map { currency ->
            if(money.contains(currency.symbol)) {
                m = money.substring(currency.symbol.length, money.length)
                return@map
            }
        }

        return m
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