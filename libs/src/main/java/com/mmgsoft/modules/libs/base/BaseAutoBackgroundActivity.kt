package com.mmgsoft.modules.libs.base

import android.os.Bundle
import android.widget.ImageView
import com.mmgsoft.modules.libs.manager.AssetManager
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.models.Background

abstract class BaseAutoBackgroundActivity : BaseActivity() {
    /**
     * Cần trả về resourceID ảnh để load background
     */
    abstract val backgroundImageView: Int

    /**
     * Trả về true sẽ auto đổi ảnh khi vào resume
     * Trả về false chỉ load ảnh ở onCreate
     */
    abstract val isAutoLoadWhenRandomBackground: Boolean

    private val backgroundImage: ImageView by lazy {
        findViewById(backgroundImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BackgroundManager.getBackground {
            loadBackground(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if(isAutoLoadWhenRandomBackground) BackgroundManager.getBackground {
            loadBackground(it)
        }
    }

    protected fun loadBackground(background: Background) {
        AssetManager.loadBitmap(background.backgroundPath) {
            backgroundImage.setImageBitmap(it)
        }
    }
}