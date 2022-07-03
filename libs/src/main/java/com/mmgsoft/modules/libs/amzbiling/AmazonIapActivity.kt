package com.mmgsoft.modules.libs.amzbiling

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazon.device.iap.PurchasingService
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.customview.SpacesItemDecoration
import com.mmgsoft.modules.libs.helpers.AmazonScreenType
import com.mmgsoft.modules.libs.utils.AdsComponentConfig.amazonProdId

class AmazonIapActivity : BaseIapAmzActivity(), OnItemClickListener<ProductItem> {
    private val imBack: AppCompatImageView by lazy {
        findViewById(R.id.imBack)
    }

    private val rcvSkuAvai: RecyclerView by lazy {
        findViewById(R.id.rcvSkuAvai)
    }

    private val mSkuAdapter: ProductSkuAdapter by lazy {
        ProductSkuAdapter(this)
    }

    override val allSkus: Set<String>
        get() = HashSet(amazonProdId)

    override val resLayout: Int
        get() = R.layout.purchase_product_layout

    companion object {
        const val EXTRA_SCREEN_TYPE = "EXTRA_SCREEN_TYPE"

        fun open(ctx: Context, screenType: AmazonScreenType = AmazonScreenType.SUBSCRIPTION) =
            Intent(ctx, AmazonIapActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_TYPE, screenType.type)
                ctx.startActivity(this)
            }
    }

    override val screenType: AmazonScreenType
        get() = AmazonScreenType.map(
            intent.getStringExtra(EXTRA_SCREEN_TYPE) ?: AmazonScreenType.BUY_GOLD.type
        )

    override fun initData() {
        imBack.setOnClickListener { v: View? -> super.onBackPressed() }
        initRecycler()
    }

    override fun notifyUpdateListView() {
        mSkuAdapter.notifyDataSetChanged()
    }

    private fun initRecycler() {
        rcvSkuAvai.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        rcvSkuAvai.itemAnimator = DefaultItemAnimator()
        rcvSkuAvai.addItemDecoration(SpacesItemDecoration(10, true))
        rcvSkuAvai.setHasFixedSize(true)
        rcvSkuAvai.adapter = mSkuAdapter
        mSkuAdapter.updateAllData(productItems)
    }

    override fun onItemClicked(item: ProductItem) {
        PurchasingService.purchase(item.sku)
    }
}