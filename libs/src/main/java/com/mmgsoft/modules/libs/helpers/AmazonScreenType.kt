package com.mmgsoft.modules.libs.helpers

enum class AmazonScreenType(val type: String) {
    BUY_GOLD("BUY_GOLD"),
    SUBSCRIPTION("SUBSCRIPTION");

    companion object {
        fun map(type: String) = when(type) {
            BUY_GOLD.type -> BUY_GOLD
            else -> SUBSCRIPTION
        }
    }
}