package com.mmgsoft.modules.libs.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.scale
import com.bumptech.glide.Glide
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.models.Background
import kotlinx.coroutines.*

object AssetManager {
    fun loadListFilesOfAsset(ctx: Context, path: String): List<String> {
        val items = ctx.assets.list(path)
        return items?.toList() ?: listOf()
    }

    fun loadBitmap(path: String, doWork: (Bitmap) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        val ims = AdsApplication.instance.assets.open(path)
        val d = Drawable.createFromStream(ims, null)
        val b = (d as BitmapDrawable).bitmap
        val br = b.scale(300, 533)
        withContext(Dispatchers.Main) {
            doWork.invoke(br)
        }
    }
}