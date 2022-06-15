package com.mmgsoft.modules.libs.etx

import android.view.View

internal fun View.visible() {
    this.visibility = View.VISIBLE
}

internal fun View.gone() {
    this.visibility = View.GONE
}

internal fun View.inVisible() {
    this.visibility = View.INVISIBLE
}