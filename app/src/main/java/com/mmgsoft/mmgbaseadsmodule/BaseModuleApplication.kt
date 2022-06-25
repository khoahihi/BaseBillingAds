package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.utils.Config

class BaseModuleApplication : AdsApplication() {
    override val testDevices: List<String>
        get() = listOf()

    override val prodInAppIds: List<String>
        get() = listOf()

    override val prodSubsIds: List<String>
        get() = listOf()

    override fun onCreated() {
        Config.updateCurrency("POINT")
        instance = this
    }

    companion object {
        lateinit var instance: BaseModuleApplication
    }
}