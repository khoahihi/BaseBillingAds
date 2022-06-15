package com.mmgsoft.modules.libs.helpers

import android.content.Context

object AdsPrefs {
    const val ADS_PREFS_NAME = "ADS_PREFS_NAME"
    const val KEY_PREFS_IS_BILLING = "KEY_PREFS_IS_BILLING"

    private fun getPrefs(ctx: Context) = ctx.getSharedPreferences(ADS_PREFS_NAME, Context.MODE_PRIVATE)

    fun putBoolean(ctx: Context, key: String, value: Boolean) {
        getPrefs(ctx).edit().apply {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(ctx: Context, key: String): Boolean {
        return getPrefs(ctx).getBoolean(key, false)
    }
}