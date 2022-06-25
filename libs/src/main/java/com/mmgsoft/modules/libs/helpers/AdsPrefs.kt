package com.mmgsoft.modules.libs.helpers

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.ADS_PREFS_NAME
import com.mmgsoft.modules.libs.utils.PREFS_BACKGROUND_WAS_PAID
import com.mmgsoft.modules.libs.utils.PREFS_BILLING_BUY_ITEM_1
import com.mmgsoft.modules.libs.utils.PREFS_BILLING_BUY_ITEM_2

internal class AdsPrefs {
    private val ctx: Context by lazy {
        AdsApplication.instance
    }

    private fun getPrefs() =
        ctx.getSharedPreferences(ADS_PREFS_NAME, Context.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean) {
        getPrefs().edit().apply {
            putBoolean(key, value)
            apply()
        }
    }

    private fun getBoolean(key: String): Boolean {
        return getPrefs().getBoolean(key, false)
    }

    fun putDouble(key: String, value: Double): Boolean {
        return getPrefs().edit().putString(key, value.toString()).commit()
    }

    fun getDouble(key: String): Double {
        return getPrefs().getString(key, "0")?.toDouble() ?: 0.0
    }

    fun isBuyItem1(): Boolean {
        return getBoolean(PREFS_BILLING_BUY_ITEM_1)
    }

    fun isBuyItem2(): Boolean {
        return getBoolean(PREFS_BILLING_BUY_ITEM_2)
    }

    fun putString(key: String, value: String): Boolean {
        return getPrefs().edit().putString(key, value).commit()
    }

    fun getString(key: String): String {
        return getPrefs().getString(key, "") ?: ""
    }

    fun addWasPaidBackground(background: Background): Boolean {
        val newItems = getWasPaidBackgrounds()
        newItems.add(background)
        return saveWasPaidBackground(newItems)
    }

    fun getWasPaidBackgrounds(): MutableList<Background> {
        val backgroundsString = getString(PREFS_BACKGROUND_WAS_PAID)

        return if (backgroundsString.isBlank()) mutableListOf()
        else {
            val backgroundType = object : TypeToken<ArrayList<Background>>() {}.type
            Gson().fromJson(backgroundsString, backgroundType)
        }
    }

    fun saveWasPaidBackground(backgrounds: List<Background>): Boolean {
        return putString(PREFS_BACKGROUND_WAS_PAID, Gson().toJson(backgrounds))
    }
}