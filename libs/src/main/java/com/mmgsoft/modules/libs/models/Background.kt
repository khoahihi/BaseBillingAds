package com.mmgsoft.modules.libs.models

import android.graphics.Bitmap

data class Background(
    val price: String,
    val productId: String,
    val description: String,
    val backgroundPath: String,
    var isBuy: Boolean,
    var isSelected: Boolean = false,
    var isTriggerLoadBitmap: Boolean = false,
    var bm: Bitmap? = null
) {
    override fun equals(other: Any?): Boolean {
        return (other as? Background)?.let {
            productId == productId
        } ?: false
    }
}