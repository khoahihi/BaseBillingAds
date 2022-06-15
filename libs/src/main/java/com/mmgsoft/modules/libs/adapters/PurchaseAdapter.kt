package com.mmgsoft.modules.libs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.google.android.material.card.MaterialCardView
import com.mmgsoft.modules.libs.R
import com.mmgsoft.modules.libs.base.BaseAdapterAutoViewHolder
import com.mmgsoft.modules.libs.etx.gone
import com.mmgsoft.modules.libs.etx.visible
import com.mmgsoft.modules.libs.models.PurchaseProductDetails

const val HOLDER_SUBS_TYPE = 1
const val HOLDER_IN_APP_TYPE = 2
class PurchaseAdapter(private val subsLayoutResId: Int, private val inAppLayoutResId: Int) : BaseAdapterAutoViewHolder<PurchaseProductDetails>() {

    var onItemClicked: (ProductDetails) -> Unit = {}

    override val layoutResId: Int
        get() = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return onHandleCreateViewHolder(
            LayoutInflater.from(parent.context).inflate(
                (if (viewType == HOLDER_SUBS_TYPE) subsLayoutResId
                else inAppLayoutResId), parent, false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).productDetails.productType == BillingClient.ProductType.SUBS) HOLDER_SUBS_TYPE else HOLDER_IN_APP_TYPE
    }

    override fun onBindView(itemView: View, item: PurchaseProductDetails, position: Int) {
        itemView.apply {
            val cardParent = findViewById<MaterialCardView>(R.id.cardParent)
            val tvName = findViewById<TextView>(R.id.tvTitle)
            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            val tvPrice = findViewById<TextView>(R.id.tvPrice)
            val viewBlur = findViewById<View>(R.id.viewBlur)

            val productDetails = item.productDetails
            tvName.text = productDetails.name
            cardParent.isEnabled = !item.isBuy
            viewBlur.visibility = if(item.isBuy) View.VISIBLE else View.GONE
            if(productDetails.productType == BillingClient.ProductType.INAPP) {
                tvDescription.text = productDetails.description
                tvPrice.text = productDetails.oneTimePurchaseOfferDetails?.formattedPrice
            } else {
                tvDescription.text = productDetails.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.billingPeriod
                tvPrice.text = productDetails.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
            }

            setOnClickListener {
                onItemClicked.invoke(productDetails)
            }
        }
    }
}