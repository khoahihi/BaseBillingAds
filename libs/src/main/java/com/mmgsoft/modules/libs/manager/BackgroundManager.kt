package com.mmgsoft.modules.libs.manager

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.PREFS_CURRENT_BACKGROUND_SELECTED
import kotlin.random.Random

object BackgroundManager {
    private val adsPref by lazy {
        AdsApplication.prefs
    }

    private val gson: Gson by lazy {
        Gson()
    }

    private val observableOneBackground: MutableLiveData<Background> by lazy {
        MutableLiveData<Background>()
    }

    fun addWasPaidBackground(background: Background): Boolean {
        return adsPref.addWasPaidBackground(background)
    }

    fun getWasPaidBackgrounds(): MutableList<Background> {
        return adsPref.getWasPaidBackgrounds()
    }

    fun saveBackgroundSelected(background: Background?): Boolean {
        return adsPref.putString(PREFS_CURRENT_BACKGROUND_SELECTED, gson.toJson(background)).apply {
            background?.let {
                observableOneBackground.postValue(background)
            }
        }
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
        val size = backgrounds.size
        if(backgrounds.isNotEmpty()) {
            doWork.invoke(backgrounds[random(size, size)])
        }
    }

    fun observableOneBackground() = observableOneBackground

    fun getBackground(doWork: (Background) -> Unit) {
        getBackgroundSelected()?.let {
            doWork.invoke(it)
        } ?: randomBackground(doWork)
    }

    private fun random(count: Int, end: Int): Int {
        val randoms = (0 until count).map {
            (0 until end).random()
        }

        return randoms[randoms.indices.random()]
    }
}