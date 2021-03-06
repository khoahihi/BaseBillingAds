package com.mmgsoft.modules.libs.utils

import android.content.Context
import com.mmgsoft.modules.libs.AdsApplication
import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.helpers.BackgroundLoadOn
import com.mmgsoft.modules.libs.helpers.BillingType
import com.mmgsoft.modules.libs.manager.MoneyManager
import com.mmgsoft.modules.libs.models.BillingMapper

const val DEFAULT_CURRENCY = "GOLD"
const val DEFAULT_EXCHANGE_RATE = 1.2
const val DEFAULT_EXCHANGE_RATE_OTHER = 1.6
const val DEFAULT_ADS_INTERSTITIAL_KEY = "sub.pkg.interstitial"
const val DEFAULT_ADS_BANNER_KEY = "sub.pkg.banner"
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
    internal var packageNameLoadBackground = ""
    internal val activitiesNonLoadBackground = mutableListOf<String>()
    internal val testDevices = mutableListOf<String>()
    internal const val weightingPrice = 100
    internal var currency = DEFAULT_CURRENCY
    internal var exchangeRate = DEFAULT_EXCHANGE_RATE
    internal var interstitialKey = DEFAULT_ADS_INTERSTITIAL_KEY
    internal var bannerKey = DEFAULT_ADS_BANNER_KEY
    internal var consumeKey = DEFAULT_ADS_CONSUME
    internal var otherAppContext: Context = AdsApplication.application
    internal var assetsPath = DEFAULT_ASSETS_PATH
    internal var billingType = BillingType.AMAZON
    internal var loadBackgroundOn = BackgroundLoadOn.ON_RESUME
    internal val backgroundPrices = mutableListOf<String>()
    internal val billingMappers = mutableListOf(
        BillingMapper(BILLING_MAPPER_KEY_1, "US$5000"),
        BillingMapper(BILLING_MAPPER_KEY_2, "US$10000"),
        BillingMapper(BILLING_MAPPER_KEY_3, "US$15000"),
    )

    /**
     * @param newCurrency
     * T??n lo???i ti???n c???a app (GOLD, POINT,...)
     */
    fun updateCurrency(newCurrency: String): AdsComponentConfig {
        this.currency = newCurrency
        return this
    }

    /**
     * @param newExchangeRate
     * T??? l??? quy ?????i t??? ti???n n???p sang ti???n app
     * (default 1.2 => 1$ = 1.2 {@link $currency}
     */
    fun updateExchangeRate(newExchangeRate: Double): AdsComponentConfig {
        this.exchangeRate = newExchangeRate
        return this
    }

    /**
     * @param newItem1
     * Endpoint ki???m tra ???? mua g??i ????? ???n interstitial
     */
    fun updateInterstitialKey(newItem1: String): AdsComponentConfig {
        this.interstitialKey = newItem1
        return this
    }

    /**
     * @param newItem2
     * Endpoint ki???m tra ???? mua g??i ????? ???n banner
     */
    fun updateBannerKey(newItem2: String): AdsComponentConfig {
        this.bannerKey = newItem2
        return this
    }

    /**
     * @param newConsume Path consumeID ????? ph??n bi???t v???i INAPP c???a Google Billing
     */
    fun updateConsumeKey(newConsume: String): AdsComponentConfig {
        this.consumeKey = newConsume
        return this
    }

    /**
     * Load assets images
     * @param path: ???????ng d???n c???a danh s??ch ???nh n???n trong th?? m???c assets
     * N???u ?????t ??? ngo??i c??ng th?? truy???n r???ng ("")
     * C??c lo???i file ???nh ph?? h???p:
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
     * Th??m gi?? ti???n cho t???ng lo???i ???nh
     * @param price: danh s??ch gi?? ti???n
     * _______________________________________
     * Khi gi?? ti???n truy???n v??o th???a ho???c thi???u (so v???i s??? l?????ng ???nh trong assets c???a myApp):
     * - N???u s??? l?????ng prices th??m thi???u so v???i ???nh trong assets c???a app, s??? l???y default (position * weightingPrice)
     * - Danh s??ch ti???n n??y s??? ???????c map v???i danh s??ch ???nh trong assets, ch??? l???y ????? s??? l?????ng
     */
    fun addBackgroundPrice(vararg price: String): AdsComponentConfig {
        backgroundPrices.addAll(price)
        return this
    }

    /**
     * @param devices danh s??ch deviceID th??m v??o Admob, tr??nh tr?????ng h???p b??? leak v?? ??n g???y t??? Google
     */
    fun addTestDevices(vararg devices: String): AdsComponentConfig {
        testDevices.addAll(devices)
        return this
    }

    /**
     * C???p nh???t lo???i Billing
     * @param billingType: [GOOGLE, AMAZON]
     */
    fun updateBillingType(billingType: BillingType): AdsComponentConfig {
        this.billingType = billingType
        return this
    }

    /**
     * Fix s??? ti???n c???ng v??o sau khi thanh to??n c??c g??i
     */
    fun updateBillingMapper(mappers: List<BillingMapper>): AdsComponentConfig {
        this.billingMappers.clear()
        this.billingMappers.addAll(mappers.map(::standardizedData))
        return this
    }

    /**
     * Fix s??? ti???n c???ng v??o sau khi thanh to??n c??c g??i
     * @param mappers
     */
    fun updateBillingMapper(vararg mappers: BillingMapper): AdsComponentConfig {
        updateBillingMapper(mappers.toMutableList())
        return this
    }

    /**
     * @param packageName: PackageName so s??nh ????? load background
     */
    fun updatePackageNameLoadBackground(packageName: String): AdsComponentConfig{
        this.packageNameLoadBackground = packageName
        return this
    }

    /**
     * @param activitiesName: Danh s??ch t??n activity c???u h??nh ????? lo???i b??? background
     */
    fun addActivitiesNonLoadBackground(vararg activitiesName: String): AdsComponentConfig {
        this.activitiesNonLoadBackground.addAll(activitiesName)
        return this
    }

    /**
     * @param backgroundLoadOn: C???u h??nh background ???????c load ??? function n??o [ON_CREATED, ON_RESUME]
     * Default: ON_RESUME
     */
    fun updateLoadBackgroundOn(backgroundLoadOn: BackgroundLoadOn): AdsComponentConfig {
        this.loadBackgroundOn = backgroundLoadOn
        return this
    }

    /**
     * C???p nh???t danh s??ch ProductID ????? l???y c??c g??i t??? AmazonBilling
     * @param amazonItem
     */
    internal fun addAmazonItem(amazonItem: List<String>): AdsComponentConfig {
        this.amazonProdId.clear()
        this.amazonProdId.addAll(amazonItem)
        return this
    }

    /**
     * @version: 0.1.5
     * @param billingMapper L?? d??? li???u ch??a ???????c chu???n h??a
     * @return D??? li???u ???? ???????c chu???n h??a s??? ti???n nh???n sau
     *          khi ???????c ch???nh s???a t??? danh s??ch {@link AdsComponentConfig#updateBillingMapper}
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