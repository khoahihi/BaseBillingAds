package com.mmgsoft.modules.libs.helpers

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.etx.findIndex
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.Background
import com.mmgsoft.modules.libs.utils.*

internal class AdsPrefs {
    private val ctx: Context by lazy {
        AdsApplication.instance
    }

    private fun getPrefs() =
        ctx.getSharedPreferences(ADS_PREFS_NAME, Context.MODE_PRIVATE)

    fun putBoolean(key: String, value: Boolean) {
        getPrefs().edit().apply {
            putBoolean(key, value)
            apply()
        }
    }

    private fun getBoolean(key: String): Boolean {
        return getPrefs().getBoolean(key, false)
    }

    fun putDouble(key: String, value: Double): Boolean {
        return getPrefs().edit().putString(key, value.toString()).commit()
    }

    fun getDouble(key: String): Double {
        return getPrefs().getString(key, "0")?.toDouble() ?: 0.0
    }

    fun isBuyItem1(): Boolean {
        return getBoolean(PREFS_BILLING_BUY_ITEM_1)
    }

    fun isBuyItem2(): Boolean {
        return getBoolean(PREFS_BILLING_BUY_ITEM_2)
    }

    fun putString(key: String, value: String): Boolean {
        return getPrefs().edit().putString(key, value).commit()
    }

    fun getString(key: String): String {
        return getPrefs().getString(key, "") ?: ""
    }

    fun addWasPaidBackground(background: Background): Boolean {
        val newItems = getWasPaidBackgrounds()
        newItems.add(background)
        return saveWasPaidBackground(newItems)
    }

    fun getWasPaidBackgrounds(): MutableList<Background> {
        val backgroundsString = getString(PREFS_BACKGROUND_WAS_PAID)

        return if (backgroundsString.isBlank()) mutableListOf()
        else convertStringToModels<Background>(backgroundsString).toMutableList()
    }

    private inline fun <reified T> convertStringToModels(str: String): List<T> {
        val type = object : TypeToken<ArrayList<T>>() {}.type
        return Gson().fromJson(str, type)
    }

    private fun saveWasPaidBackground(backgrounds: List<Background>): Boolean {
        return putString(PREFS_BACKGROUND_WAS_PAID, Gson().toJson(backgrounds))
    }

//    fun addProductsIdWasPaid(productsId: List<Pair<String, Boolean>>): Boolean {
//        val newItems = getWasPaidProductsId()
//        productsId.map { product ->
//            if(newItems.find { product.first == it.first } == null) {
//                newItems.add(product)
//            }
//        }
//        return saveWasPaidProductsId(newItems)
//    }

//    fun checkBillingOnAddMoney(productDetails: List<ProductDetails>) {
//        val currentPurchase = getWasPaidProductsId()
//
//        productDetails.map { productDetail ->
//            currentPurchase.find { it.first == productDetail.productId }?.let { itemWasPaid ->
//                if(!itemWasPaid.second) {
//                    if(productDetail.productType == BillingClient.ProductType.INAPP) {
//                        productDetail.oneTimePurchaseOfferDetails?.formattedPrice?.let { money ->
//                            MoneyManager.addMoney(money)
//                            itemWasPaid.second = true
//                        }
//                    } else {
//                        productDetail.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice?.let { money ->
//                            MoneyManager.addMoney(money)
//                            itemWasPaid.second = true
//                        }
//                    }
//                }
//            }
//        }
//
//        saveWasPaidProductsId(currentPurchase)
//    }

//    fun getWasPaidProductsId(): MutableList<Pair<String, Boolean>> {
//        val productsIdString = getString(PREFS_PRODUCTS_ID_WAS_PAID)
//
//        return if(productsIdString.isBlank()) mutableListOf()
//        else Gson().fromJson(productsIdString, getTokenType<Pair<String, Boolean>>())
//    }
//
//    fun saveWasPaidProductsId(productsId: List<Pair<String, Boolean>>): Boolean {
//        return putString(PREFS_PRODUCTS_ID_WAS_PAID, Gson().toJson(productsId))
//    }
//
//    data class Pair<F, S>(
//        var first: F,
//        var second: S
//    ) {
//        companion object {
//            fun<F, S> create(first: F, second: S) = Pair(first, second)
//        }
//    }
}