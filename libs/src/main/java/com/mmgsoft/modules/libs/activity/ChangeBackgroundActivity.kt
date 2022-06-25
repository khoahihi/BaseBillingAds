package com.mmgsoft.modules.libs.activity

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.adapters.BackgroundAdapter
import com.mmgsoft.modules.libs.dialog.BuyBackgroundBottomSheet
import com.mmgsoft.modules.libs.etx.setStatusBarColor
import com.mmgsoft.modules.libs.etx.setStatusBarTextColorDark
import com.mmgsoft.modules.libs.manager.AssetManager
import com.mmgsoft.modules.libs.manager.BackgroundManager
import com.mmgsoft.modules.libs.manager.BackgroundManager.addWasPaidBackground
import com.mmgsoft.modules.libs.manager.BackgroundManager.getWasPaidBackgrounds
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.Background
import de.hdodenhof.circleimageview.CircleImageView

class ChangeBackgroundActivity : BaseActivity() {
    override val layoutResId: Int
        get() = R.layout.activity_change_background

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

    private val imBack: ImageView by lazy {
        findViewById(R.id.imBack)
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

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })

        imBack.setOnClickListener {
            finish()
        }

        updateCurrentMoney()
    }

    private fun onBackgroundItemClicked(background: Background) {
        if(BackgroundManager.isWasPaid(background)) {
            if(background.productId == BackgroundManager.getBackgroundSelected()?.productId) {
                BackgroundManager.saveBackgroundSelected(null)
                mBackgroundAdapter.updateSelected(background, false)
            } else if (BackgroundManager.saveBackgroundSelected(background)) {
                mBackgroundAdapter.updateSelected(background)
            } else showAlertMessage("Change Background Failed")
        } else {
            BuyBackgroundBottomSheet(background) {
                if(MoneyManager.buyBackground(background)) {
                    if(addWasPaidBackground(background)) {
                        mBackgroundAdapter.updateBilling(background)
                        updateCurrentMoney()
                        showToast("Buy success")
                    } else showAlertMessage("Not enough money")
                } else showAlertMessage("Not enough money")
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

    private fun updateCurrentMoney() {
        tvGold.text = MoneyManager.getCurrentGoldStr()
    }

    private fun getBackgrounds() = AssetManager.loadListFilesOfAsset(this, "backgrounds").mapIndexed { index, s ->
        val p = index + 1
        Background((p * 100).toString(), "item$p", "background $p", "backgrounds/$s", false)
    }
}