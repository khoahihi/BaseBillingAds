package com.mmgsoft.modules.libs.amzbiling

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.*
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.billing.BillingManager
import com.mmgsoft.modules.libs.data.local.db.AppDbHelper
import com.mmgsoft.modules.libs.data.model.db.EntitlementModel
import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.manager.MoneyManager.addMoney
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.DEFAULT_EXCHANGE_RATE_OTHER
import com.mmgsoft.modules.libs.utils.PREFS_BILLING_BUY_ITEM_1
import com.mmgsoft.modules.libs.utils.PREFS_BILLING_BUY_ITEM_2

abstract class BaseIapAmzActivity : AppCompatActivity(), PurchasingListener {
    private var allProductSkus: MutableSet<String> = HashSet()
    var currentUserId: String? = null
    var currentMarketPlace: String? = null
    abstract val allSkus: Set<String>
    abstract val resLayout: Int
    abstract fun initData()
    abstract fun notifyUpdateListView()
    private var mUnbinder: Unbinder? = null

    @JvmField
    protected var productItems: MutableList<ProductItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allProductSkus.clear()
        allProductSkus.addAll(allSkus)
        setContentView(resLayout)
        mUnbinder = ButterKnife.bind(this)
        PurchasingService.registerListener(this, this)
        initData()
    }

    override fun onStart() {
        super.onStart()
        PurchasingService.getProductData(allProductSkus)
    }

    override fun onResume() {
        super.onResume()
        PurchasingService.getUserData()
        PurchasingService.getPurchaseUpdates(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mUnbinder != null) mUnbinder!!.unbind()
    }

    override fun onUserDataResponse(userDataResponse: UserDataResponse) {
        userDataResponse.requestStatus?.let {
            when (it) {
                UserDataResponse.RequestStatus.SUCCESSFUL -> {
                    currentUserId = userDataResponse.userData.userId
                    currentMarketPlace = userDataResponse.userData.marketplace
                }
                UserDataResponse.RequestStatus.FAILED -> {}
                UserDataResponse.RequestStatus.NOT_SUPPORTED -> {}
            }
        }
    }

    override fun onProductDataResponse(response: ProductDataResponse) {
        when (response.requestStatus) {
            ProductDataResponse.RequestStatus.SUCCESSFUL -> {
                val products = response.productData
                val skuUnavailables = response.unavailableSkus
                productItems.clear()
                for (key in products.keys) {
                    val product = products[key]
                    val productItem = ProductItem()
                    productItem.title = product!!.title
                    productItem.description = product.description
                    productItem.sku = product.sku
                    productItem.price = product.price
                    productItem.iapViewType =
                        if (product.productType == ProductType.SUBSCRIPTION) IapViewType.SUB else IapViewType.IN_APP
                    if (product.productType == ProductType.CONSUMABLE) productItem.isBuy =
                        false else {
                        productItem.isBuy = skuUnavailables.contains(product.sku)
                    }
                    productItems.add(productItem)
                }
                notifyUpdateListView()
            }
            ProductDataResponse.RequestStatus.FAILED -> notifyUpdateListView()
        }
    }

    override fun onPurchaseResponse(purchaseResponse: PurchaseResponse) {
        when (purchaseResponse.requestStatus) {
            PurchaseResponse.RequestStatus.SUCCESSFUL -> {
                currentUserId = purchaseResponse.userData.userId
                currentMarketPlace = purchaseResponse.userData.marketplace
                PurchasingService.notifyFulfillment(
                    purchaseResponse.receipt.receiptId,
                    FulfillmentResult.FULFILLED
                )
                handleReceiptData(purchaseResponse.receipt)
                val receipt = purchaseResponse.receipt
                if (receipt.productType == ProductType.CONSUMABLE) {
                    productItems.map { prodItem ->
                        if (prodItem.sku == receipt.sku) {
                            addMoney(prodItem.price)
                            return@map
                        }
                    }
                } else{
                    productItems.map { prodItem ->
                        if (prodItem.sku == receipt.sku) {
                            if (prodItem.sku.contains(AdsComponentConfig.item1)) {
                                BillingManager.putIsBilling(PREFS_BILLING_BUY_ITEM_1)
                            } else if(prodItem.sku.contains(AdsComponentConfig.item2)) {
                                BillingManager.putIsBilling(PREFS_BILLING_BUY_ITEM_2)
                            } else addMoney(prodItem.price, DEFAULT_EXCHANGE_RATE_OTHER)
                            return@map
                        }
                    }
                }
            }
            PurchaseResponse.RequestStatus.FAILED -> {}
        }
    }

    override fun onPurchaseUpdatesResponse(purchaseUpdatesResponse: PurchaseUpdatesResponse) {
        when (purchaseUpdatesResponse.requestStatus) {
            PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL -> {
                currentUserId = purchaseUpdatesResponse.userData.userId
                currentMarketPlace = purchaseUpdatesResponse.userData.marketplace
                for (receipt in purchaseUpdatesResponse.receipts) {
                    // Grant Item to User
                    handleReceiptData(receipt)
                }
                if (purchaseUpdatesResponse.hasMore()) {
                    PurchasingService.getPurchaseUpdates(true)
                }
            }
            PurchaseUpdatesResponse.RequestStatus.FAILED -> {}
        }
    }

    private fun handleReceiptData(receipt: Receipt) {
        receipt.productType?.let {
            when (it) {
                ProductType.CONSUMABLE -> {}
                ProductType.SUBSCRIPTION -> {
                    val subscriptionModel = SubscriptionModel()
                    subscriptionModel.userId = currentUserId
                    subscriptionModel.sku = receipt.sku
                    subscriptionModel.receiptId = receipt.receiptId
                    subscriptionModel.fromDate = receipt.purchaseDate.time
                    subscriptionModel.toDate = receipt.cancelDate.time
                    AdsApplication.instance.dbHelper!!.insertSubscriptionRecord(subscriptionModel)
                }
                ProductType.ENTITLED -> {
                    val entitlementModel = EntitlementModel()
                    entitlementModel.userId = currentUserId
                    entitlementModel.sku = receipt.sku
                    entitlementModel.receiptId = receipt.receiptId
                    entitlementModel.purchaseDate = receipt.purchaseDate.time
                    entitlementModel.cancelDate =
                        if (receipt.cancelDate == null) AppDbHelper.TO_DATE_NOT_SET else receipt.cancelDate.time
                    AdsApplication.instance.dbHelper!!.insertEntitlementRecord(entitlementModel)
                }
            }
        }
    }

    fun getAllProductSkus(): Set<String> {
        return allProductSkus
    }

    fun setAllProductSkus(allProductSkus: MutableSet<String>) {
        this.allProductSkus = allProductSkus
    }

    fun getProductItems(): List<ProductItem> {
        return productItems
    }

    fun setProductItems(productItems: MutableList<ProductItem>) {
        this.productItems = productItems
    }
}