package com.mmgsoft.modules.libs.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.scale
import com.mmgsoft.modules.libs.AdsApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

object AssetManager {
    /**
     * Thực hiện lấy danh sách đường dẫn ảnh trong folder
     * @param path: Đường dẫn của danh sách ảnh
     */
    fun loadListFilesOfAsset(ctx: Context, path: String): List<String> {
        val items = ctx.assets.list(path)
        val newItems = mutableListOf<String>()
        items?.map {
            if (it.endsWith(ImageFileType.PNG.type) || it.endsWith(ImageFileType.JPG.type)
                || it.endsWith(ImageFileType.JPEG.type) || it.endsWith(ImageFileType.WEBP.type)
            ) {
                newItems.add(it)
            }
        }
        return newItems.toList()
    }

    /**
     * Thực hiện load ảnh từ assets file
     * resize ảnh để tăng performance
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    fun loadBitmap(path: String, doWork: (Bitmap) -> Unit) = CoroutineScope(Dispatchers.IO).launch {
        AdsApplication.application.assets.open(path).use {
            val d = Drawable.createFromStream(it, null)
            val b = (d as BitmapDrawable).bitmap
            b.scale(300, 533)
        }.also {
            withContext(Dispatchers.Main) {
                doWork.invoke(it)
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun loadBitmap(
        path: String,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        return AdsApplication.application.assets.open(path).use {
            decodeSampledBitmapFromStream(it, reqWidth, reqHeight)
        }
    }

    private fun decodeSampledBitmapFromStream(
        `is`: InputStream,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(`is`, null, this)
            if (`is`.markSupported()) {
                `is`.reset()
            }
            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeStream(`is`, null, this)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Các loại ảnh được support hiện tại
     */
    private enum class ImageFileType(val type: String) {
        PNG("png"),
        JPG("jpg"),
        JPEG("jpeg"),
        WEBP("webp")
    }
}