package com.mmgsoft.modules.libs.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.billing.RetryPolicies.connectionRetryPolicy
import com.mmgsoft.modules.libs.billing.RetryPolicies.resetConnectionRetryPolicyCounter
import com.mmgsoft.modules.libs.billing.RetryPolicies.taskExecutionRetryPolicy
import com.mmgsoft.modules.libs.helpers.BillingLoadingState
import com.mmgsoft.modules.libs.helpers.BillingLoadingStateEvent
import com.mmgsoft.modules.libs.helpers.StateAfterBuy
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.PurchaseProductDetails
import com.mmgsoft.modules.libs.utils.AdsComponentConfig
import com.mmgsoft.modules.libs.utils.PREFS_BILLING_BUY_ITEM_1
import com.mmgsoft.modules.libs.utils.PREFS_BILLING_BUY_ITEM_2
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

@SuppressLint("LogNotTimber")
object BillingManager {
    private val mAllProductDetails = mutableListOf<ProductDetails>()
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val mProductInAppIds = mutableListOf<String>()
    private val mProductSubsIds = mutableListOf<String>()
    var onKnowledgeSuccess: (Purchase, BillingResult) -> Unit = { _: Purchase, _: BillingResult -> }
    var onConsumeSuccess: (Purchase, BillingResult, String) -> Unit = { _: Purchase, _: BillingResult, _: String -> }
    var listAvailable = mutableListOf<PurchaseProductDetails>()
    val listAvailableObserver : MutableLiveData<MutableList<PurchaseProductDetails>> by lazy {
        MutableLiveData<MutableList<PurchaseProductDetails>>(mutableListOf())
    }
    var validPurchases = HashSet<Purchase>()
    var skuHistoryList = listOf<PurchaseHistoryRecord>()
    var state = StateAfterBuy.DISABLE

    private val prefs by lazy {
        AdsApplication.prefs
    }

    private const val TAG = "BillingManager"

    private fun getSkuListInApp() = mProductInAppIds.map {
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId(it)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
    }

    private fun getSkuListSubs() = mProductSubsIds.map {
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId(it)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
    }

    private lateinit var mBillingClient: BillingClient

    private val billingConnectListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            // The BillingClient is setup successfully
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    scope.launch {
                        Log.d(TAG, "onBillingSetupFinished(): Successfully")
                        resetConnectionRetryPolicyCounter()
                        mAllProductDetails.clear()
                        listAvailable.clear()
                        if (getSkuListInApp().isNotEmpty()) querySkuDetailsAsync(getSkuListInApp())
                        if (getSkuListSubs().isNotEmpty()) querySkuDetailsAsync(getSkuListSubs())
//                        listAvailable.addAll(skuDetailsList)
                        queryPurchasesAsync()
                        // L???y l???ch s??? mua h??ng
//                    queryPurchaseHistoryAsync()
                    }
                }
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                    Log.d(TAG, "onBillingSetupFinished(): ${billingResult.debugMessage}")
                }
                else -> {
                    Log.d(TAG, "onBillingSetupFinished(): ${billingResult.debugMessage}")
                }
            }
        }

        override fun onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            connectionRetryPolicy { connectToGooglePlayBillingService() }
        }
    }

    /**
     * Kh???i t???o cho billingClient v?? set gi?? tr??? cho 2 th???ng l?? SUBS v?? INAPP list
     */
    fun init(context: Context,
             productInAppIds: List<String>,
             productSubsIds: List<String>,
             state: StateAfterBuy = StateAfterBuy.DISABLE) {
        this.state = state
        this.mProductInAppIds.clear()
        this.mProductInAppIds.addAll(productInAppIds)
        this.mProductSubsIds.clear()
        this.mProductSubsIds.addAll(productSubsIds)
        mBillingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
//            .setChildDirected(BillingClient.ChildDirected.CHILD_DIRECTED) /*For use Reward Product*/
//            .setUnderAgeOfConsent(BillingClient.UnderAgeOfConsent.UNDER_AGE_OF_CONSENT) /*For use Reward Product*/
            .build()
        connectToGooglePlayBillingService()
    }

    fun init(context: Context,
             productInAppIds: List<String>,
             productSubsIds: List<String>,
             state: StateAfterBuy = StateAfterBuy.DISABLE,
             item1: String, item2: String) {
        AdsComponentConfig.updateInterstitialKey(item1).updateBannerKey(item2)
        init(context, productInAppIds, productSubsIds, state)
    }

    private fun connectToGooglePlayBillingService() {
        Log.d(TAG, "connectToGooglePlayBillingService()")
        if (!mBillingClient.isReady) mBillingClient.startConnection(billingConnectListener)
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) = scope.launch {
        // Get the offerToken of the selected offer
        val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""
        val billingFlowParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()
        val productDetailsParamsList = listOf(billingFlowParams)
        val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build()

        taskExecutionRetryPolicy(mBillingClient, billingConnectListener) {
            Log.d(TAG, "launchBillingFlow()")
            mBillingClient.launchBillingFlow(activity, params)
        }
    }

    /**
     * Tr??? l???i chi ti???t mua h??ng cho c??c m???t h??ng hi???n thu???c s??? h???u ???? mua trong ???ng d???ng c???a b???n.
     * Ch??? c??c ????ng k?? ??ang ho???t ?????ng v?? c??c giao d???ch mua m???t l???n kh??ng s??? d???ng ???????c tr??? l???i.
     * Ph????ng ph??p n??y s??? d???ng b??? nh??? cache c???a ???ng d???ng C???a h??ng Google Play m?? kh??ng c???n th???c hi???n y??u c???u m???ng.
     */
    fun queryPurchasesAsync() {
        fun task() = scope.launch {
            Log.d(TAG, "queryPurchasesAsync()")
            val purchasesResult = hashSetOf<Purchase>()
            val queryPurchasesParamsINAPP = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            val queryPurchasesParamsSUBS = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            val resultInApp = mBillingClient.queryPurchasesAsync(queryPurchasesParamsINAPP)
            purchasesResult.addAll(resultInApp.purchasesList)

            if (isSubscriptionSupported()) {
                val resultSubs = mBillingClient.queryPurchasesAsync(queryPurchasesParamsSUBS)
                purchasesResult.addAll(resultSubs.purchasesList)
            }

            handlePurchases(purchasesResult)
            purchasesResult.map { purchase ->
                clearItemsWhenProdIds(purchase.products)
            }
        }
        taskExecutionRetryPolicy(mBillingClient, billingConnectListener) { task() }
    }

    private fun clearItemsWhenProdIds(productIds: List<String>) = scope.launch {
        productIds.map { productId ->
            val product = listAvailable.firstOrNull { it.productDetails.productId == productId }
            if(state == StateAfterBuy.DISABLE) {
                product?.apply {
                    isBuy = true
                }
            } else {
                product?.let { productDetail ->
                    listAvailable.remove(productDetail)
                }
            }
        }

        checkOnClearTheSameItems()
        checkIsBilling()
        withContext(Dispatchers.Main) {
            listAvailableObserver.postValue(listAvailable)
        }
    }

    private fun checkIsBilling() {
        mAllProductDetails.map {
            if(it.productId.contains(AdsComponentConfig.interstitialKey)) {
                putIsBilling(PREFS_BILLING_BUY_ITEM_1)
            }

            if(it.productId.contains(AdsComponentConfig.bannerKey)) {
                putIsBilling(PREFS_BILLING_BUY_ITEM_2)
            }
        }
    }

    fun putIsBilling(key: String) {
        AdsApplication.prefs.putBoolean(key, true)
    }

    /**
     * Tr??? l???i giao d???ch mua g???n ????y nh???t do ng?????i d??ng th???c hi???n cho m???i SKU,
     * ngay c??? khi giao d???ch mua ???? ???? h???t h???n, b??? h???y ho???c ???? s??? d???ng.
     */
//    fun queryPurchaseHistoryAsync() {
//        fun task() {
//            Log.d(TAG, "queryPurchaseHistoryAsync()")
//            mBillingClient.queryPurchaseHistoryAsync(
//                BillingClient.SkuType.INAPP,
//                purchaseHistoryResponseListener
//            )
//            mBillingClient.queryPurchaseHistoryAsync(
//                BillingClient.SkuType.SUBS,
//                purchaseHistoryResponseListener
//            )
//        }
//        taskExecutionRetryPolicy(mBillingClient, billingConnectListener) { task() }
//    }

    private val purchaseHistoryResponseListener =
        PurchaseHistoryResponseListener { billingResult, listPurchaseHistory ->
            /**
             * Tr??? l???i giao d???ch mua g???n ????y nh???t do ng?????i d??ng th???c hi???n cho m???i SKU,
             * ngay c??? khi giao d???ch mua ???? ???? h???t h???n, b??? h???y ho???c ???? s??? d???ng.
             */
            if (listPurchaseHistory != null) {
                skuHistoryList = listPurchaseHistory
            }
            Log.d(TAG, "onPurchasesUpdated(): ${billingResult.debugMessage}")
            Log.d(TAG, "onPurchasesUpdated(): ${listPurchaseHistory?.size}")
        }

    /**
     * Tr??? v??? k???t q???a sau khi mua h??ng
     */
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                /**
                 * Thanh to??n th??nh c??ng, purchases l?? th??ng tin ????n h??ng v???a thanh to??n
                 */
                purchases?.map { purchase ->
                    purchase.products.map { productId ->
                        if(productId.contains(AdsComponentConfig.consumeKey)) {
                            checkOnAddMoney(productId) {
                                mAllProductDetails.find { it.productId == productId }?.let { productDetail ->
                                    if(productDetail.productType == BillingClient.ProductType.INAPP) {
                                        productDetail.oneTimePurchaseOfferDetails?.formattedPrice?.let { money ->
                                            MoneyManager.addMoney(money)
                                        }
                                    } else {
                                        productDetail.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice?.let { money ->
                                            MoneyManager.addMoney(money)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                purchases?.apply { handlePurchases(this.toSet()) }
                EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.SHOW_LOADING))
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.HIDE_LOADING))
                // Handle response cancel
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // item already owned? call queryPurchasesAsync to verify and process all such items
                EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.HIDE_LOADING))
                Log.d(TAG, "onPurchasesUpdated(): ${billingResult.debugMessage}")
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.HIDE_LOADING))
                Log.d(TAG, "onPurchasesUpdated(): Service disconnected")
                connectToGooglePlayBillingService()
            }
            else -> {
                EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.HIDE_LOADING))
                Log.d(TAG, "onPurchasesUpdated(): ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * H??m n??y tr??? v??? 1 list c??c SKU m?? ???? ???????c c???u h??nh tr??n google console
     * v?? ???????c dev ?????nh ngh??a trong SKU list
     */
    private val listenerGetSKUSResponse =
        ProductDetailsResponseListener { billingResult, productDetails ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    Log.d(TAG, "onSkuDetailsResponse(): OK $productDetails")
                    mAllProductDetails.addAll(productDetails)
                    listAvailable.addAll(productDetails.mapToPurchaseProdDetails())
                    listAvailableObserver.postValue(listAvailable)
                }
                else -> {
                    Log.d(
                        TAG, "onSkuDetailsResponse(): responseCode: ${billingResult.responseCode}" +
                                " --- message:${billingResult.debugMessage}"
                    )
                }
            }
        }

    private fun handlePurchases(purchases: Set<Purchase>) {
        Log.d(TAG, "handlePurchases()")
        val validPurchases = HashSet<Purchase>(purchases.size)
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                // Valid purchases
                validPurchases.add(purchase)
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Log.d(TAG, "handlePurchases(): Receive a pending purchase of SKU: $purchase.sku")
                // handle pending purchases, e.g. confirm with users about the pending
                // purchases, prompt them to complete it, etc.
            }
            allowMultiplePurchase(purchase)
            acknowledgePurchase(purchase)

            // Send purchases to server and save to local database

//            val (consumables, nonConsumables) = validPurchases.partition {
//                PurchaseConfig.CONSUMABLE_SKUS.contains(it.sku)
//            }

//            Log.d(TAG, "handlePurchases(): Consumables content:$consumables")
//            Log.d(TAG, "handlePurchases(): non-consumables content:$nonConsumables")

//            handleConsumablePurchasesAsync(consumables)
//            acknowledgeNonConsumablePurchaseAsync(nonConsumables)
        }
        BillingManager.validPurchases = validPurchases
        Log.d(TAG, "handlePurchases() validPurchases" + validPurchases.size)
    }

    private fun allowMultiplePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        mBillingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
            EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.HIDE_LOADING))
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                clearItemsWhenProdIds(purchase.products)
                Log.d(TAG, "AllowMultiplePurchases success, responseCode: ${billingResult.responseCode}")
            } else {
                Log.d(TAG, "Can't allowMultiplePurchases, responseCode: ${billingResult.responseCode}")
            }
            onConsumeSuccess.invoke(purchase, billingResult, purchaseToken)
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                    EventBus.getDefault().postSticky(BillingLoadingStateEvent(BillingLoadingState.HIDE_LOADING))
                    // handle confirm purchase from the store success or not
                    Log.d("PURCHASE=====", "acknowledgePurchase")
                    clearItemsWhenProdIds(purchase.products)
                    onKnowledgeSuccess.invoke(purchase, it)
                }
            }
        }
    }

    private fun checkOnClearTheSameItems() {
        listAvailable = listAvailable.distinctBy { it.productDetails.productId }.toMutableList()
    }

    /**
     * Th???c hi???n truy v???n ????? l???y chi ti???t c??c SKU
     */
    fun querySkuDetailsAsync(skuList: List<QueryProductDetailsParams.Product>) {
        taskExecutionRetryPolicy(mBillingClient, billingConnectListener) {
            val params = QueryProductDetailsParams.newBuilder().setProductList(skuList)
            taskExecutionRetryPolicy(mBillingClient, billingConnectListener) {
                mBillingClient.queryProductDetailsAsync(params.build(), listenerGetSKUSResponse)
            }
        }
    }

    private fun isSubscriptionSupported(): Boolean {
        val billingResult = mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        var iSupported = false
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> iSupported = true
            // Reconnect to service if disconnected
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> connectToGooglePlayBillingService()
            else -> Log.d(TAG, "isSubscriptionSupported(): Error:${billingResult.debugMessage}")
        }
        return iSupported
    }

    private fun checkOnAddMoney(prodID: String, onNonContains: () -> Unit) {
        AdsComponentConfig.billingMappers.find { prodID.uppercase().contains(it.productId.uppercase()) }?.let {
            MoneyManager.addMoney(it.price, 1.0)
        } ?: onNonContains.invoke()
    }

    fun isBuyItem1() = prefs.isBuyItem1()

    fun isBuyItem2() = prefs.isBuyItem2()

    private fun List<ProductDetails>.mapToPurchaseProdDetails() = map {
        PurchaseProductDetails(false, it)
    }
}