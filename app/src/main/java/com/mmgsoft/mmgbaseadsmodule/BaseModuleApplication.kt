package com.mmgsoft.mmgbaseadsmodule

import com.mmgsoft.modules.libs.AdsApplication

class BaseModuleApplication : AdsApplication() {
    override val testDevices: List<String>
        get() = listOf()

    override val prodInAppIds: List<String>
        get() = listOf()

    override val prodSubsIds: List<String>
        get() = listOf()

    override fun onCreated() {
        instance = this
    }

    companion object {
        lateinit var instance: BaseModuleApplication
    }
}