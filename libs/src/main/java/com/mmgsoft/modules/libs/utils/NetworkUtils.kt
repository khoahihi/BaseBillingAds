package com.mmgsoft.modules.libs.utils

import android.content.Context
import android.net.ConnectivityManager


object NetworkUtils {
    fun isNetworkAvailable(ctx: Context): Boolean {
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}