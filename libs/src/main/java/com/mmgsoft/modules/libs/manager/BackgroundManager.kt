package com.mmgsoft.modules.libs.manager

import com.google.gson.Gson
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.PREFS_CURRENT_BACKGROUND_SELECTED

object BackgroundManager {
    private val adsPref by lazy {
        AdsApplication.prefs
    }

    private val gson: Gson by lazy {
        Gson()
    }

    fun addWasPaidBackground(background: Background): Boolean {
        return adsPref.addWasPaidBackground(background)
    }

    fun getWasPaidBackgrounds(): MutableList<Background> {
        return adsPref.getWasPaidBackgrounds()
    }

    fun saveBackgroundSelected(background: Background?): Boolean {
        return adsPref.putString(PREFS_CURRENT_BACKGROUND_SELECTED, gson.toJson(background))
    }

    fun getBackgroundSelected(): Background? {
        val backgroundString = adsPref.getString(PREFS_CURRENT_BACKGROUND_SELECTED)
        return if(backgroundString.isBlank()) null else gson.fromJson(backgroundString, Background::class.java)
    }

    fun isWasPaid(background: Background): Boolean {
        return getWasPaidBackgrounds().find { it.productId == background.productId } != null
    }

    private fun randomBackground(doWork: (Background) -> Unit) {
        val backgrounds = getWasPaidBackgrounds()
        if(backgrounds.isNotEmpty()) {
            val next = (0 until backgrounds.size).random()
            doWork.invoke(backgrounds[next])
        }
    }

    fun getBackground(doWork: (Background) -> Unit) {
        getBackgroundSelected()?.let {
            doWork.invoke(it)
        } ?: randomBackground(doWork)
    }
}