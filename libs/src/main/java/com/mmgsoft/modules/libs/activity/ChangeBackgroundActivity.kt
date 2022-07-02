package com.mmgsoft.modules.libs.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.adapters.BackgroundAdapter
import com.mmgsoft.modules.libs.amzbiling.AmazonIapActivity
import com.mmgsoft.modules.libs.base.BaseActivity
import com.mmgsoft.modules.libs.dialog.BuyBackgroundBottomSheet
import com.mmgsoft.modules.libs.etx.setStatusBarColor
import com.mmgsoft.modules.libs.etx.setStatusBarTextColorDark
import com.mmgsoft.modules.libs.helpers.AmazonScreenType
import com.mmgsoft.modules.libs.manager.AssetManager
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.BackgroundManager.addWasPaidBackground
import com.mmgsoft.modules.libs.manager.BackgroundManager.getWasPaidBackgrounds
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.START_WITH_DESCRIPTION
import com.mmgsoft.modules.libs.utils.START_WITH_PRODUCT_ID
import de.hdodenhof.circleimageview.CircleImageView

class ChangeBackgroundActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_change_background

    companion object {
        fun open(ctx: Context) {
            ctx.startActivity(Intent(ctx, ChangeBackgroundActivity::class.java))
        }
    }

    private val lnBound: LinearLayout by lazy {
        findViewById(R.id.lnBound)
    }

    private val backgroundPrices: List<String> by lazy {
        AdsComponentConfig.backgroundPrices
    }

    private val weightingPrice: Int by lazy {
        AdsComponentConfig.weightingPrice
    }

    private val rvBackground: RecyclerView by lazy {
        findViewById(R.id.rvBackgrounds)
    }

    private val mBackgroundAdapter: BackgroundAdapter by lazy {
        BackgroundAdapter(::onBackgroundItemClicked)
    }

    private val imGold: CircleImageView by lazy {
        findViewById(R.id.imGold)
    }

    private val backgroundMotion: MotionLayout by lazy {
        findViewById(R.id.backgroundMotion)
    }

    private val tvGold: TextView by lazy {
        findViewById(R.id.tvGold)
    }

    private val tvBuyGold: TextView by lazy {
        findViewById(R.id.tvBuyGold)
    }

    private val imBack: ImageView by lazy {
        findViewById(R.id.imBack)
    }

    private val tvCurrency: TextView by lazy {
        findViewById(R.id.tvCurrency)
    }

    override fun initViews() {
        setStatusBarColor(R.color.white)
        setStatusBarTextColorDark()
        rvBackground.adapter = mBackgroundAdapter
        val backgrounds = getBackgrounds()
        updateBillingStatus(backgrounds)
        updateSelectBackground(backgrounds)
        mBackgroundAdapter.setData(backgrounds)
        imGold.setOnClickListener {
            if(backgroundMotion.currentState == R.id.start) {
                backgroundMotion.transitionToEnd()
            } else backgroundMotion.transitionToStart()
        }

        backgroundMotion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if(progress in 45f..55f) {
                    updateCurrentMoney()
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })

        imBack.setOnClickListener {
            finish()
        }

        lnBound.setOnClickListener {
            AmazonIapActivity.open(this, AmazonScreenType.BUY_GOLD)
        }

        updateCurrentMoney()
    }

    override fun onResume() {
        super.onResume()
        updateCurrentMoney()
    }

    private fun onBackgroundItemClicked(background: Background) {
        if(BackgroundManager.isWasPaid(background)) {
            if(background.productId == BackgroundManager.getBackgroundSelected()?.productId) {
                BackgroundManager.saveBackgroundSelected(null)
                mBackgroundAdapter.updateSelected(background, false)
            } else if (BackgroundManager.saveBackgroundSelected(background)) {
                mBackgroundAdapter.updateSelected(background)
            } else showAlertMessage(getString(R.string.change_background_failed))
        } else {
            BuyBackgroundBottomSheet(background) {
                if(MoneyManager.buyBackground(background)) {
                    if(addWasPaidBackground(background)) {
                        mBackgroundAdapter.updateBilling(background)
                        updateCurrentMoney()
                        showToast(getString(R.string.buy_success))
                    } else showAlertMessage(getString(R.string.not_enough_money))
                } else showAlertMessage(getString(R.string.not_enough_money))
            }.show(supportFragmentManager, null)
        }
    }

    private fun updateBillingStatus(prev: List<Background>) {
        val backgroundsWasPaid = getWasPaidBackgrounds()

        prev.map { background ->
            backgroundsWasPaid.find { it.productId == background.productId }?.let {
                background.isBuy = true
            }
        }
    }

    private fun updateSelectBackground(prev: List<Background>) {
        BackgroundManager.getBackgroundSelected()?.let { background ->
            prev.map {
                if(it.productId == background.productId) {
                    it.isSelected = true
                    return@map
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentMoney() {
        val isShowBuy = backgroundMotion.currentState == R.id.end

        tvGold.text = MoneyManager.getCurrentGold().toString()
        tvCurrency.text = AdsComponentConfig.currency
        tvBuyGold.text = "Buy ${AdsComponentConfig.currency}"

        tvGold.visibility = if(isShowBuy) View.GONE else View.VISIBLE
        tvCurrency.visibility = if(isShowBuy) View.GONE else View.VISIBLE
        tvBuyGold.visibility = if(isShowBuy) View.VISIBLE else View.GONE
    }

    private fun getBackgrounds() = AssetManager.loadListFilesOfAsset(
        AdsComponentConfig.otherAppContext, AdsComponentConfig.assetsPath).mapIndexed { index, s ->
        val p = index + 1
        Background(
            getBackgroundPrice(index),
            "$START_WITH_PRODUCT_ID$s",
            "$START_WITH_DESCRIPTION$p",
            getBackgroundPath(AdsComponentConfig.assetsPath, s), false)
    }

    private fun getBackgroundPrice(index: Int): String {
        return if(backgroundPrices.size > index) {
            backgroundPrices[index]
        } else {
            val next = index + 1
            (next * weightingPrice).toString()
        }
    }

    private fun getBackgroundPath(assetPath: String, fileName: String): String {
        return if(assetPath.isBlank()) fileName else "$assetPath/$fileName"
    }
}