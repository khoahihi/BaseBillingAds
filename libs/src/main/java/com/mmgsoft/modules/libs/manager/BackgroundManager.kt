package com.mmgsoft.modules.libs.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.helpers.BackgroundLoadOn
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.PREFS_CURRENT_BACKGROUND_SELECTED

object BackgroundManager {
    private const val EXTRA_BACKGROUND_IMAGE = "EXTRA_BACKGROUND_IMAGE"

    fun attach(application: Application) {
        val customPackageName = AdsComponentConfig.packageNameLoadBackground
        val pkgCompare = customPackageName.ifBlank { application.packageName }
        val activitiesNonLoad = AdsComponentConfig.activitiesNonLoadBackground
        val loadBackgroundOn = AdsComponentConfig.loadBackgroundOn
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if(loadBackgroundOn == BackgroundLoadOn.ON_CREATED) {
                    onLoadBackground(activitiesNonLoad, pkgCompare, activity)
                }
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                if(loadBackgroundOn == BackgroundLoadOn.ON_RESUME) {
                    onLoadBackground(activitiesNonLoad, pkgCompare, activity)
                }
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    private fun onLoadBackground(activitiesNonLoad: List<String>, pkgCompare: String, act: Activity) {
        if(activitiesNonLoad.find { it.contains(act.localClassName) } == null) {
            if(act.packageName.contains(pkgCompare)) {
                loadBackground(act)
            }
        }
    }

    private val adsPref by lazy {
        AdsApplication.prefs
    }

    private val gson: Gson by lazy {
        Gson()
    }

    /**
     * biến để đăng ký thay đổi background khi người dùng thực hiện thay đổi
     * Chỉ trả về khi người dùng chọn 1 background duy nhất làm hình nền
     */
    private val observableOneBackground: MutableLiveData<Background> by lazy {
        MutableLiveData<Background>()
    }

    /**
     * Thêm 1 background vào prefs
     * Thực hiện khi người dùng mua background
     */
    fun addWasPaidBackground(background: Background): Boolean {
        return adsPref.addWasPaidBackground(background)
    }

    /**
     * Lấy về danh sách backgrounds người dùng đã mua
     */
    fun getWasPaidBackgrounds(): MutableList<Background> {
        return adsPref.getWasPaidBackgrounds()
    }

    /**
     * Lưu lại background người dùng  đang chọn
     * Khi người dùng không chọn, background sẽ = null
     */
    fun saveBackgroundSelected(background: Background?): Boolean {
        return adsPref.putString(PREFS_CURRENT_BACKGROUND_SELECTED, gson.toJson(background)).apply {
            background?.let {
                observableOneBackground.postValue(background)
            }
        }
    }

    /**
     * Lấy về background người dùng đang chọn
     * Khi người dùng không chọn, background sẽ = null
     */
    fun getBackgroundSelected(): Background? {
        val backgroundString = adsPref.getString(PREFS_CURRENT_BACKGROUND_SELECTED)
        return if(backgroundString.isBlank()) null else gson.fromJson(backgroundString, Background::class.java)
    }

    /**
     * Kiểm tra background này đã được mua hay chưa
     * @param background: đầu vào để kiểm tra
     * Trả về true khi đã mua
     * Trả về false khi chưa được mua
     */
    fun isWasPaid(background: Background): Boolean {
        return getWasPaidBackgrounds().find { it.productId == background.productId } != null
    }

    /**
     * Thực hiện random backgrounds khi người dùng không chọn background nào
     * Nếu người dùng chưa mua background nào, doWork sẽ không được gọi
     */
    private fun randomBackground(doWork: (Background) -> Unit) {
        val backgrounds = getWasPaidBackgrounds()
        val size = backgrounds.size
        if(backgrounds.isNotEmpty()) {
            doWork.invoke(backgrounds[random(size, size)])
        }
    }

    fun observableOneBackground() = observableOneBackground

    /**
     * Tự động kiểm tra để trả về oneTimeBackground hoặc randomBackground
     */
    fun getBackground(doWork: (Background) -> Unit) {
        getBackgroundSelected()?.let {
            doWork.invoke(it)
        } ?: randomBackground(doWork)
    }

    /**
     * Thực hiện random nhiều lần để tránh trùng lặp
     */
    private fun random(count: Int, end: Int): Int {
        val randoms = (0 until count).map {
            (0 until end).random()
        }

        return randoms[randoms.indices.random()]
    }

    /**
     * @param imageView: ImageView được lựa chọn trên layout
     * Thực hiện load ảnh trên imageView đã chọn
     */
    fun loadBackgroundToImageView(imageView: ImageView) {
        getBackground { background ->
            AssetManager.loadBitmap(background.backgroundPath) {
                imageView.setImageBitmap(it)
            }
        }
    }

    /**
     * @param act: Activity hiện tại
     * Thực hiện load ảnh vào rootView của Activity
     */
    fun loadBackground(act: Activity) {
        val imageID = act.intent.getIntExtra(EXTRA_BACKGROUND_IMAGE, -1)

        val imageView = if(imageID == -1) {
            createBackgroundViewAndAddToRoot(act, ImageView.ScaleType.CENTER_CROP)
        } else act.findViewById(imageID)

        loadBackgroundToImageView(imageView)
    }

    /**
     * @param act: Activity hiện tại
     * @param scaleType: scaleType của ảnh
     * Thực hiện load ảnh vào rootView của Activity
     */
    fun loadBackground(act: Activity, scaleType: ImageView.ScaleType) {
        val imageID = act.intent.getIntExtra(EXTRA_BACKGROUND_IMAGE, -1)

        val imageView = if(imageID == -1) {
            createBackgroundViewAndAddToRoot(act, scaleType)
        } else act.findViewById(imageID)

        loadBackgroundToImageView(imageView)
    }

    /**
     * @param act
     * @param st
     * Tạo ảnh, gán id và lưu vào intent của activity
     */
    private fun createBackgroundViewAndAddToRoot(act: Activity, st: ImageView.ScaleType) = ImageView(act).apply {
        id = View.generateViewId()
        scaleType = st
        act.intent.putExtra(EXTRA_BACKGROUND_IMAGE, id)
        act.window.decorView.findViewById<ViewGroup>(android.R.id.content).addView(this, 0)
    }
}