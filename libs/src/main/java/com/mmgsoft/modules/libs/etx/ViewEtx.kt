package com.mmgsoft.modules.libs.etx

import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.doOnLayout

internal fun View.visible() {
    this.visibility = View.VISIBLE
}

internal fun View.gone() {
    this.visibility = View.GONE
}

internal fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

inline fun View.afterMeasured(crossinline callback: View.() -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        callback()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback()
                }
            }
        })
    }
}