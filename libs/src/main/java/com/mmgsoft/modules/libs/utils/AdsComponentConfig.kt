package com.mmgsoft.modules.libs.utils

import android.content.Context
import com.mmgsoft.modules.libs.AdsApplication

const val DEFAULT_CURRENCY = "GOLD"
const val DEFAULT_EXCHANGE_RATE = 1.2
const val DEFAULT_ADS_ITEM_1 = "inapp.nonconsum.item1"
const val DEFAULT_ADS_ITEM_2 = "inapp.nonconsum.item2"
const val DEFAULT_ASSETS_PATH = "backgrounds"
const val ADS_PREFS_NAME = "ADS_PREFS_NAME"
const val PREFS_BILLING_BUY_ITEM_1 = "PREFS_BILLING_BUY_ITEM_1"
const val PREFS_BILLING_BUY_ITEM_2 = "PREFS_BILLING_BUY_ITEM_2"
const val PREFS_BACKGROUND_WAS_PAID = "PREFS_BACKGROUND_WAS_PAID"
const val PREFS_CURRENT_BACKGROUND_SELECTED = "PREFS_CURRENT_BACKGROUND_SELECTED"
const val PREFS_MONEY = "PREFS_MONEY"
object AdsComponentConfig {
    internal const val weightingPrice = 100
    internal var currency = DEFAULT_CURRENCY
    internal var exchangeRate = DEFAULT_EXCHANGE_RATE
    internal var item1 = DEFAULT_ADS_ITEM_1
    internal var item2 = DEFAULT_ADS_ITEM_2
    internal var otherAppContext: Context = AdsApplication.instance
    internal var assetsPath = DEFAULT_ASSETS_PATH
    internal val backgroundPrices = mutableListOf<String>()

    fun updateCurrency(newCurrency: String): AdsComponentConfig {
        this.currency = newCurrency
        return this
    }

    fun updateExchangeRate(newExchangeRate: Double): AdsComponentConfig {
        this.exchangeRate = newExchangeRate
        return this
    }

    fun updateItem1(newItem1: String): AdsComponentConfig {
        this.item1 = newItem1
        return this
    }

    fun updateItem2(newItem2: String): AdsComponentConfig {
        this.item2 = newItem2
        return this
    }

    fun loadAssetsFromMyApp(ctx: Context, path: String): AdsComponentConfig {
        this.otherAppContext = ctx
        this.assetsPath = path
        return this
    }

    fun addBackgroundPrice(vararg price: String): AdsComponentConfig {
        backgroundPrices.addAll(price)
        return this
    }
}