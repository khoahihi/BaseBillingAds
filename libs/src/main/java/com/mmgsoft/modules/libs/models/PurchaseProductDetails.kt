package com.mmgsoft.modules.libs.models

import com.android.billingclient.api.ProductDetails

data class PurchaseProductDetails (
    var isBuy: Boolean,
    val productDetails: ProductDetails
)
