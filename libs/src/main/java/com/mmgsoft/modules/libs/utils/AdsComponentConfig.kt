package com.mmgsoft.modules.libs.utils

import android.content.Context
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.etx.toCurrency
import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.BillingMapper

const val DEFAULT_CURRENCY = "GOLD"
const val DEFAULT_EXCHANGE_RATE = 1.2
const val DEFAULT_EXCHANGE_RATE_OTHER = 1.6
const val DEFAULT_ADS_ITEM_1 = "sub.pkg.interstitial"
const val DEFAULT_ADS_ITEM_2 = "sub.pkg.banner"
const val DEFAULT_ADS_CONSUME = "inapp.consum"
const val DEFAULT_ASSETS_PATH = "backgrounds"
const val ADS_PREFS_NAME = "ADS_PREFS_NAME"
const val PREFS_BILLING_BUY_ITEM_1 = "PREFS_BILLING_BUY_ITEM_1"
const val PREFS_BILLING_BUY_ITEM_2 = "PREFS_BILLING_BUY_ITEM_2"
const val PREFS_BACKGROUND_WAS_PAID = "PREFS_BACKGROUND_WAS_PAID"
const val PREFS_CURRENT_BACKGROUND_SELECTED = "PREFS_CURRENT_BACKGROUND_SELECTED"
const val PREFS_PRODUCTS_ID_WAS_PAID = "PREFS_PRODUCTS_ID_WAS_PAID"
const val START_WITH_PRODUCT_ID = "item_"
const val START_WITH_DESCRIPTION = "background "
const val PREFS_MONEY = "PREFS_MONEY"
const val BILLING_MAPPER_KEY_1 = ""
const val BILLING_MAPPER_KEY_2 = ""
const val BILLING_MAPPER_KEY_3 = ""
object AdsComponentConfig {
    val amazonProdId = arrayListOf<String>()
    internal val testDevices = mutableListOf<String>()
    internal const val weightingPrice = 100
    internal var currency = DEFAULT_CURRENCY
    internal var exchangeRate = DEFAULT_EXCHANGE_RATE
    internal var item1 = DEFAULT_ADS_ITEM_1
    internal var item2 = DEFAULT_ADS_ITEM_2
    internal var consumeKey = DEFAULT_ADS_CONSUME
    internal var otherAppContext: Context = AdsApplication.instance
    internal var assetsPath = DEFAULT_ASSETS_PATH
    internal var billingType = BillingType.AMAZON
    internal val backgroundPrices = mutableListOf<String>()
    internal val billingMappers = mutableListOf(
        BillingMapper(BILLING_MAPPER_KEY_1, "US$5000"),
        BillingMapper(BILLING_MAPPER_KEY_2, "US$10000"),
        BillingMapper(BILLING_MAPPER_KEY_3, "US$15000"),
    )

    /**
     * @param newCurrency
     * Tên loại tiền của app (GOLD, POINT,...)
     */
    fun updateCurrency(newCurrency: String): AdsComponentConfig {
        this.currency = newCurrency
        return this
    }

    /**
     * @param newExchangeRate
     * Tỉ lệ quy đổi từ tiền nạp sang tiền app
     * (default 1.2 => 1$ = 1.2 {@link $currency}
     */
    fun updateExchangeRate(newExchangeRate: Double): AdsComponentConfig {
        this.exchangeRate = newExchangeRate
        return this
    }

    /**
     * @param newItem1
     * Endpoint kiểm tra đã mua gói để ẩn interstitial
     */
    fun updateItem1(newItem1: String): AdsComponentConfig {
        this.item1 = newItem1
        return this
    }

    /**
     * @param newItem2
     * Endpoint kiểm tra đã mua gói để ẩn banner
     */
    fun updateItem2(newItem2: String): AdsComponentConfig {
        this.item2 = newItem2
        return this
    }

    /**
     * @param newConsume Path consumeID để phân biệt với INAPP của Google Billing
     */
    fun updateConsumeKey(newConsume: String): AdsComponentConfig {
        this.consumeKey = newConsume
        return this
    }

    /**
     * Load assets images
     * @param path: đường dẫn của danh sách ảnh nền trong thư mục assets
     * Nếu đặt ở ngoài cùng thì truyền rỗng ("")
     * Các loại file ảnh phù hợp:
     * ________________
     * PNG("png")    ||
     * JPG("jpg")    ||
     * JPEG("jpeg")  ||
     * WEBP("webp")  ||
     * ______________||
     */
    fun loadAssetsFromMyApp(ctx: Context, path: String): AdsComponentConfig {
        this.otherAppContext = ctx
        this.assetsPath = path
        return this
    }

    /**
     * Thêm giá tiền cho từng loại ảnh
     * @param price: danh sách giá tiền
     * _______________________________________
     * Khi giá tiền truyền vào thừa hoặc thiếu (so với số lượng ảnh trong assets của myApp):
     * - Nếu số lượng prices thêm thiếu so với ảnh trong assets của app, sẽ lấy default (position * weightingPrice)
     * - Danh sách tiền này sẽ được map với danh sách ảnh trong assets, chỉ lấy đủ số lượng
     */
    fun addBackgroundPrice(vararg price: String): AdsComponentConfig {
        backgroundPrices.addAll(price)
        return this
    }

    /**
     * @param devices danh sách deviceID thêm vào Admob, tránh trường hợp bị leak và ăn gậy từ Google
     */
    fun addTestDevices(vararg devices: String): AdsComponentConfig {
        testDevices.addAll(devices)
        return this
    }

    /**
     * Cập nhật loại Billing
     * @param billingType: [GOOGLE, AMAZON]
     */
    fun updateBillingType(billingType: BillingType): AdsComponentConfig {
        this.billingType = billingType
        return this
    }

    /**
     * Fix số tiền cộng vào sau khi thanh toán các gói
     */
    fun updateBillingMapper(mappers: List<BillingMapper>): AdsComponentConfig {
        this.billingMappers.clear()
        this.billingMappers.addAll(mappers.map(::standardizedData))
        return this
    }

    /**
     * Fix số tiền cộng vào sau khi thanh toán các gói
     * @param mappers
     */
    fun updateBillingMapper(vararg mappers: BillingMapper): AdsComponentConfig {
        updateBillingMapper(mappers.toMutableList())
        return this
    }

    /**
     * Cập nhật danh sách ProductID để lấy các gói từ AmazonBilling
     * @param amazonItem
     */
    internal fun addAmazonItem(amazonItem: List<String>): AdsComponentConfig {
        this.amazonProdId.clear()
        this.amazonProdId.addAll(amazonItem)
        return this
    }

    /**
     * @version: 0.1.5
     * @param billingMapper Là dữ liệu chưa được chuẩn hóa
     * @return Dữ liệu đã được chuẩn hóa số tiền nhận sau
     *          khi được chỉnh sửa từ danh sách {@link AdsComponentConfig#updateBillingMapper}
     */
    private fun standardizedData(billingMapper: BillingMapper): BillingMapper {
        var isNotStandardized = true
        var price = billingMapper.price
        MoneyManager.amazonCurrencies.map {
            if(price.contains(it.symbol)) {
                isNotStandardized = false
                return@map
            }
        }
        if(isNotStandardized) {
            price = "${AmazonCurrency.US.c}$price"
            billingMapper.price = price
        }
        return billingMapper
    }
}