package com.mmgsoft.modules.libs.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.adapters.PurchaseAdapter
import com.mmgsoft.modules.libs.models.PurchaseProductDetails
import kotlinx.android.synthetic.main.view_purchase.view.*

class PurchaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private val rvPurchase by lazy {
        findViewById<RecyclerView>(R.id.rvPurchase)
    }
    private var mAdapter: PurchaseAdapter? = null
    private var subsLayoutId: Int? = null
    private var inAppLayoutResId: Int? = null

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.PurchaseView)
        val type = typeArray.getInteger(R.styleable.PurchaseView_pv_layoutManager, 1)
        val spanCount = typeArray.getInteger(R.styleable.PurchaseView_pv_spanCount, 1)
        val orientation = typeArray.getInteger(R.styleable.PurchaseView_pv_orientation, 1)
        subsLayoutId = typeArray.getInt(R.styleable.PurchaseView_subsLayoutResId, R.layout.item_purchase_subs_default)
        inAppLayoutResId = typeArray.getInt(R.styleable.PurchaseView_inAppLayoutResId, R.layout.item_purchase_inapp_default)
        typeArray.recycle()
        inflate(context, R.layout.view_purchase, this)
        initLayoutManager(type, orientation, spanCount)
        setupResLayout(subsLayoutId!!, inAppLayoutResId!!)
    }

    fun setupResLayout(subsResId: Int, inAppResId: Int) {
        mAdapter = PurchaseAdapter(subsResId, inAppResId)

        rvPurchase.adapter = mAdapter
    }

    private fun initLayoutManager(type: Int, orientation: Int, spanCount: Int) {
        rvPurchase.layoutManager = if (type == 1) { // LinearLayout
            getLinearManager(orientation)
        } else getGridManager(spanCount)
    }

    private fun getLinearManager(orientation: Int): LinearLayoutManager {
        return if (orientation == 1) LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        else LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    private fun getGridManager(spanCount: Int): GridLayoutManager {
        return GridLayoutManager(context, spanCount)
    }

    fun setup(newData: List<PurchaseProductDetails>, onClick: (ProductDetails) -> Unit = {}) {
        mAdapter?.setData(newData)
        mAdapter?.onItemClicked = onClick
    }
}