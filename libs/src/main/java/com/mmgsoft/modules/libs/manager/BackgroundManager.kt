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
     * bi???n ????? ????ng k?? thay ?????i background khi ng?????i d??ng th???c hi???n thay ?????i
     * Ch??? tr??? v??? khi ng?????i d??ng ch???n 1 background duy nh???t l??m h??nh n???n
     */
    private val observableOneBackground: MutableLiveData<Background> by lazy {
        MutableLiveData<Background>()
    }

    /**
     * Th??m 1 background v??o prefs
     * Th???c hi???n khi ng?????i d??ng mua background
     */
    fun addWasPaidBackground(background: Background): Boolean {
        return adsPref.addWasPaidBackground(background)
    }

    /**
     * L???y v??? danh s??ch backgrounds ng?????i d??ng ???? mua
     */
    fun getWasPaidBackgrounds(): MutableList<Background> {
        return adsPref.getWasPaidBackgrounds()
    }

    /**
     * L??u l???i background ng?????i d??ng  ??ang ch???n
     * Khi ng?????i d??ng kh??ng ch???n, background s??? = null
     */
    fun saveBackgroundSelected(background: Background?): Boolean {
        return adsPref.putString(PREFS_CURRENT_BACKGROUND_SELECTED, gson.toJson(background)).apply {
            background?.let {
                observableOneBackground.postValue(background)
            }
        }
    }

    /**
     * L???y v??? background ng?????i d??ng ??ang ch???n
     * Khi ng?????i d??ng kh??ng ch???n, background s??? = null
     */
    fun getBackgroundSelected(): Background? {
        val backgroundString = adsPref.getString(PREFS_CURRENT_BACKGROUND_SELECTED)
        return if(backgroundString.isBlank()) null else gson.fromJson(backgroundString, Background::class.java)
    }

    /**
     * Ki???m tra background n??y ???? ???????c mua hay ch??a
     * @param background: ?????u v??o ????? ki???m tra
     * Tr??? v??? true khi ???? mua
     * Tr??? v??? false khi ch??a ???????c mua
     */
    fun isWasPaid(background: Background): Boolean {
        return getWasPaidBackgrounds().find { it.productId == background.productId } != null
    }

    /**
     * Th???c hi???n random backgrounds khi ng?????i d??ng kh??ng ch???n background n??o
     * N???u ng?????i d??ng ch??a mua background n??o, doWork s??? kh??ng ???????c g???i
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
     * T??? ?????ng ki???m tra ????? tr??? v??? oneTimeBackground ho???c randomBackground
     */
    fun getBackground(doWork: (Background) -> Unit) {
        getBackgroundSelected()?.let {
            doWork.invoke(it)
        } ?: randomBackground(doWork)
    }

    /**
     * Th???c hi???n random nhi???u l???n ????? tr??nh tr??ng l???p
     */
    private fun random(count: Int, end: Int): Int {
        val randoms = (0 until count).map {
            (0 until end).random()
        }

        return randoms[randoms.indices.random()]
    }

    /**
     * @param imageView: ImageView ???????c l???a ch???n tr??n layout
     * Th???c hi???n load ???nh tr??n imageView ???? ch???n
     */
    fun loadBackgroundToImageView(imageView: ImageView) {
        getBackground { background ->
            AssetManager.loadBitmap(background.backgroundPath) {
                imageView.setImageBitmap(it)
            }
        }
    }

    /**
     * @param act: Activity hi???n t???i
     * Th???c hi???n load ???nh v??o rootView c???a Activity
     */
    fun loadBackground(act: Activity) {
        val imageID = act.intent.getIntExtra(EXTRA_BACKGROUND_IMAGE, -1)

        val imageView = if(imageID == -1) {
            createBackgroundViewAndAddToRoot(act, ImageView.ScaleType.CENTER_CROP)
        } else act.findViewById(imageID)

        loadBackgroundToImageView(imageView)
    }

    /**
     * @param act: Activity hi???n t???i
     * @param scaleType: scaleType c???a ???nh
     * Th???c hi???n load ???nh v??o rootView c???a Activity
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
     * T???o ???nh, g??n id v?? l??u v??o intent c???a activity
     */
    private fun createBackgroundViewAndAddToRoot(act: Activity, st: ImageView.ScaleType) = ImageView(act).apply {
        id = View.generateViewId()
        scaleType = st
        act.intent.putExtra(EXTRA_BACKGROUND_IMAGE, id)
        act.window.decorView.findViewById<ViewGroup>(android.R.id.content).addView(this, 0)
    }
}