package com.mmgsoft.modules.libs.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.base.BaseActivity
import com.mmgsoft.modules.libs.billing.BillingManager
import com.mmgsoft.modules.libs.etx.setStatusBarColor
import com.mmgsoft.modules.libs.etx.setStatusBarTextColorDark
import com.mmgsoft.modules.libs.helpers.ActionBarTheme
import com.mmgsoft.modules.libs.helpers.BillingLoadingState
import com.mmgsoft.modules.libs.helpers.BillingLoadingStateEvent
import com.mmgsoft.modules.libs.helpers.StateAfterBuy
import com.mmgsoft.modules.libs.widgets.PurchaseView
import kotlinx.android.synthetic.main.view_purchase.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class PurchaseActivity : BaseActivity() {
    private val tvTitle by lazy {
        findViewById<TextView>(R.id.tvPurchaseTitle)
    }

    private val imBack by lazy {
        findViewById<ImageView>(R.id.imBack)
    }

    private val rlHeader by lazy {
        findViewById<RelativeLayout>(R.id.rlHeader)
    }

    private val purchaseView by lazy {
        findViewById<PurchaseView>(R.id.purchaseView)
    }

    private val frBillingLoading by lazy {
        findViewById<FrameLayout>(R.id.frBillingLoading)
    }

    companion object {
        const val EXTRAS_COLOR_TOOLBAR = "EXTRAS_COLOR_TOOLBAR"
        const val EXTRAS_COLOR_TITLE_TOOLBAR = "EXTRAS_COLOR_TITLE_TOOLBAR"
        const val EXTRAS_THEME_STYLE = "EXTRAS_THEME_STYLE"
        const val EXTRAS_HEADER_TITLE = "EXTRAS_HEADER_TITLE"
        const val EXTRAS_LAYOUT_SUBS = "EXTRAS_LAYOUT_SUBS"
        const val EXTRAS_LAYOUT_IN_APP = "EXTRAS_LAYOUT_IN_APP"

        fun open(ctx: Context,
                 theme: ActionBarTheme,
                 @ColorRes colorToolbar: Int?,
                 @ColorRes colorTitleToolbar: Int?,
                 @LayoutRes layoutSubs: Int,
                 @LayoutRes layoutInApp: Int,
                 headerTitle: String?) {
            val i = Intent(ctx, PurchaseActivity::class.java)
            colorToolbar?.let { i.putExtra(EXTRAS_COLOR_TOOLBAR, it) }
            colorTitleToolbar?.let { i.putExtra(EXTRAS_COLOR_TITLE_TOOLBAR, it) }
            headerTitle?.let { i.putExtra(EXTRAS_HEADER_TITLE, it) }
            i.putExtra(EXTRAS_THEME_STYLE, theme.s)
            i.putExtra(EXTRAS_LAYOUT_SUBS, layoutSubs)
            i.putExtra(EXTRAS_LAYOUT_IN_APP, layoutInApp)
            ctx.startActivity(i)
        }

        fun open(ctx: Context,
                 @LayoutRes layoutSubs: Int,
                 @LayoutRes layoutInApp: Int,
                 headerTitle: String) {
            open(ctx, ActionBarTheme.LIGHT_MODE, null, null, layoutSubs, layoutInApp, headerTitle)
        }

        fun open(ctx: Context,
                 theme: ActionBarTheme,
                 @ColorRes colorHeader: Int,
                 @ColorRes colorTitle: Int,
                 @LayoutRes layoutSubs: Int,
                 @LayoutRes layoutInApp: Int) {
            open(ctx, theme, colorHeader, colorTitle, layoutSubs, layoutInApp, null)
        }

        fun open(ctx: Context,
                 headerTitle: String) {
            open(ctx, R.layout.item_purchase_subs_default, R.layout.item_purchase_inapp_default, headerTitle)
        }

        fun open(ctx: Context,
                 theme: ActionBarTheme,
                 @ColorRes colorHeader: Int,
                 @ColorRes colorTitle: Int) {
            open(ctx, theme, colorHeader, colorTitle, R.layout.item_purchase_subs_default, R.layout.item_purchase_inapp_default)
        }

        fun open(ctx: Context,
                 theme: ActionBarTheme,
                 @ColorRes colorHeader: Int,
                 @ColorRes colorTitle: Int,
                 headerTitle: String) {
            open(ctx, theme, colorHeader, colorTitle, R.layout.item_purchase_subs_default, R.layout.item_purchase_inapp_default, headerTitle)
        }
    }

    override val layoutResId: Int
        get() = R.layout.activity_libs_purchase

    override fun initViews() {
        registerEvents()
        setupHeaderColor()
        setupHeaderTitle()
        setupPurchaseLayout()
        observablePurchase()
        initActions()
        reInitPurchaseItems()
    }

    private fun observablePurchase() {
        BillingManager.listAvailableObserver.observe(this, Observer {
            reInitPurchaseItems()
            if(BillingManager.state == StateAfterBuy.REMOVE) {
                rvPurchase.postInvalidate()
            }
        })
    }

    private fun reInitPurchaseItems() {
        purchaseView.setup(BillingManager.listAvailable) { productDetails ->
            BillingManager.launchBillingFlow(this@PurchaseActivity, productDetails)
        }
    }

    private fun initActions() {
        imBack.setOnClickListener {
            finish()
        }
    }

    private fun setupHeaderColor() {
        val color = intent?.getIntExtra(EXTRAS_COLOR_TOOLBAR, R.color.colorAds) ?: R.color.colorAds
        val colorTitle = intent?.getIntExtra(EXTRAS_COLOR_TITLE_TOOLBAR, R.color.white) ?: R.color.white
        val theme = intent?.getStringExtra(EXTRAS_THEME_STYLE) ?: ActionBarTheme.LIGHT_MODE.s
        setStatusBarColor(color)
        rlHeader.setBackgroundColor(ContextCompat.getColor(this, color))
        tvTitle.setTextColor(ContextCompat.getColor(this, colorTitle))
        imBack.setColorFilter(ContextCompat.getColor(this, colorTitle))

        if(theme == ActionBarTheme.DARK_MODE.s) {
            setStatusBarTextColorDark()
        }
    }

    private fun setupHeaderTitle() {
        val title = intent?.getStringExtra(EXTRAS_HEADER_TITLE) ?: getString(R.string.purchase_title)
        tvTitle.text = title
    }

    private fun setupPurchaseLayout() {
        val resLayoutSubsId = intent?.getIntExtra(EXTRAS_LAYOUT_SUBS, R.layout.item_purchase_subs_default) ?: R.layout.item_purchase_subs_default
        val resLayoutInAppId = intent?.getIntExtra(EXTRAS_LAYOUT_IN_APP, R.layout.item_purchase_inapp_default) ?: R.layout.item_purchase_inapp_default

        purchaseView.setupResLayout(resLayoutSubsId, resLayoutInAppId)
    }

    @Subscribe
    fun onBillingLoadingStateChanged(event: BillingLoadingStateEvent) {
        runOnUiThread {
            frBillingLoading.visibility =
                if(event.state == BillingLoadingState.SHOW_LOADING) View.VISIBLE
                else View.GONE
        }
    }

    private fun registerEvents() {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun unRegisterEvents() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        unRegisterEvents()
        super.onDestroy()
    }
}