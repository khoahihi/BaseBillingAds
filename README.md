### Use ADS library
## Gradle project
```sh
allprojects {
	repositories {
			...
		maven { url 'https://jitpack.io' }
	}
}
```
## Gradle app
```sh
implementation 'com.github.User:Repo:$lasted_version'
```

## Base Production ID
```sh
interface Google {
    String PROD_IN_APP_INTERSTITIAL = "pkg.inapp.nonconsum.rminitial";
    String PROD_IN_APP_BANNER = "pkg.inapp.nonconsum.rmbanner";
    String PROD_IN_APP_CONSUME_1 = "pkg.inapp.consume.item1";
    String PROD_IN_APP_CONSUME_2 = "pkg.inapp.consume.item2";
    String PROD_IN_APP_CONSUME_3 = "pkg.inapp.consume.item3";
    String PROD_SUBS_1 = "pkg.subs.nonconsum.item1";
    String PROD_COMPARE_CONSUME = "inapp.consume";
}

interface Amazon {
    String PROD_CONSUME_GOLD_1 = "pkg.amziap.consum.buygold1";
    String PROD_CONSUME_GOLD_2 = "pkg.amziap.consum.buygold2";
    String PROD_CONSUME_GOLD_3 = "pkg.amziap.consum.buygold3";
    String PROD_ENTITLE_GOLD_DISCOUNT_1 = "pkg.amziap.entitle.buygold.discount1";
    String PROD_SUBS_BANNER = "pkg.amziap.subs.rmbanner";
    String PROD_SUBS_INTERSTITIAL = "pkg.amziap.subs.rminitial";
}
```
Sau Khi thêm 2 interface này vào dự án, thay đổi `pkg` thành `PackageName` của dự án, Lưu ý, bỏ `com.` ở đằng trước

## Google Billing
### I, Extend `AdsApplication`
1. Override prodInAppIds
Trả về danh sách product ID của loại `IN_APP`
2. Override prodSubsIds
Trả về danh sách product ID của loại `SUBS`
3. Override billingType
Trả về `BillingType.AMAZON`
4. Override function `onCreated`
Nếu cần app cần config tác vụ thì override lại hàm này
5. Override function `addConfig`
VD:
```sh
override fun addConfig() {
    AdsComponentConfig
        .updateInterstitialKey(Google.PROD_IN_APP_INTERSTITIAL)
        .updateBannerKey(Google.PROD_IN_APP_BANNER)
        .updateBillingMapper(
            Google.PROD_IN_APP_CONSUME_1 mapping "1000",
            Google.PROD_IN_APP_CONSUME_2 mapping "3000",
            Google.PROD_IN_APP_CONSUME_3 mapping "5000",
            Google.PROD_SUBS_1 mapping "5000"
        ).updateConsumeKey(Google.PROD_COMPARE_CONSUME)
        .addActivitiesNonLoadBackground(MainActivity::class.java.name)

    BackgroundManager.attach(this)
}
```
`updateInterstitialKey()`: Gửi vào ProductID khi billing thành công sẽ loại bỏ Interstitial.
`updateBannerKey()`: Gửi vào ProductID khi billing thành công sẽ loại bỏ Banner.
`updateBillingMapper()`: Hàm này để định nghĩa ra ProductID so với số tiền (Khi billing loại ProductID nào sẽ cộng GOLD vào đúng với số đã config)
`updateConsumeKey()`: Hàm này chỉ áp dụng đối với BillingType = Google, Vì loại INAPP và CONSUME được Google gom vào là 1, nên cần đặt ProductID khác
so với loại INAPP, gửi vào 1 cấu trúc có thể so sánh (`Contains`)
`BackgroundManager.attach(this)`: Dùng để attach background vào tất cả các activity (Với điều kiện các màn hình đó có background transparent)

### II, Use Class `AdsComponents`
1. Khi application của app đã extend 1 library khác mà không thể chỉnh sửa, thì sử dụng class này thay cho việc extend `AdsApplication`
2. Cấu hình:
```sh
val adsComponents by lazy {
        AdsComponents.inject(this)
            .withProdInApp("")  
            .withProdSubs("")       
            .withProdAmazon("")      
            .withBillingState(StateAfterBuy.DISABLE)
            .withBilling(BillingType.AMAZON)        
            .build(::addConfigs)                    
}
private fun addConfigs() {
    AdsComponentConfig
        .updateInterstitialKey(ExampleAdMobID.ADS_CLEAR_INTERSTITIAL)
        .updateBannerKey(ExampleAdMobID.ADS_CLEAR_BANNER)
        .updateBillingMapper(
            Google.PROD_IN_APP_CONSUME_1 mapping "1000",
            Google.PROD_IN_APP_CONSUME_2 mapping "3000",
            Google.PROD_IN_APP_CONSUME_3 mapping "5000",
            Google.PROD_IN_APP_INTERSTITIAL mapping "5000",  
            Google.PROD_IN_APP_BANNER mapping "5000",  
            Google.PROD_SUBS_1 mapping "5000"   
        ).addActivitiesNonLoadBackground(AmazonIapActivity::class.java.name)
          
    BackgroundManager.attach(this)
}
```
### Note: Đối với kotlin, khi sử dụng lazy, còn gọi lại adsComponents ở onCreate() của application để thực hiện init biến
VD:
```shell
override fun onCreate() {
    super.onCreate()
    adsComponents
}
```

## Amazon Billing
Tương tự như Google, Amazon cũng cần config các thông số trên.
### Lưu ý
Đối với trường hợp extend từ `AdsApplication`: add toàn bộ productID vào productInApp
VD:
```shell
override val prodInAppIds: List<String>
    get() = listOf(
    Amazon.PROD_IN_APP_CONSUME_1,
    Amazon.PROD_IN_APP_CONSUME_2,
    Amazon.PROD_IN_APP_CONSUME_3,
    Amazon.PROD_ENTITLE_GOLD_DISCOUNT_1,
    Amazon.PROD_SUBS_INTERSTITIAL,
    Amazon.PROD_SUBS_BANNER
)
```

Trả về BillingType là `BillingType.AMAZON`
```shell
override val billingType: BillingType
    get() = BillingType.AMAZON
```

## Show Interstitial, Banner
## Manifest
```shell
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="@string/admob_appid" />
```
## Interstitial 
### 1. Case extend AdsApplication
Gọi application.adsManager.forceShowInterstitial()
VD:
```shell
ExampleAdsComponentsApplication.instance.adsComponents.adsManager.forceShowInterstitial(this, getString(R.string.splash_id)) {
  // TODO: do something
}
```
### 2. Case sử dụng class `AdsComponents`
Gọi đến adsManager trong clz
VD:
```shell
adsComponents.adsManager.forceShowInterstitial(...)
```
### Banner
Sử dụng BannerAds widget
VD:
```shell
<com.mmgsoft.modules.libs.widgets.BannerAds
    android:id="@+id/bannerAds"
    app:layout_constraintBottom_toBottomOf="parent"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:ba_adsUnitId="@string/banner_id"
    app:ba_autoLoad="true"/>
```
## Billing Screen
## Đối với Billing Google
Sử dụng PurchaseActivity
VD:
```shell
PurchaseActivity.open(...)
```
Trong open có thể truyền vào các thuộc tính cho phù hợp với app
### theme = ActionBarTheme (Hiển thị màu chữ trên thanh status bar)
```shell
enum class ActionBarTheme {
    DARK_MODE,
    LIGHT_MODE
}
```
### colorTitle: Đặt màu chữ cho toolbar
### ColorHeader: Đặt màu nền cho toolbar
## Đối với Billing Amazon
Sử dụng AmazonIapActivity
VD:
```shell
AmazonIapActivity.open(this, AmazonScreenType.SUBSCRIPTION)
```

```shell
enum class AmazonScreenType {
    BUY_GOLD,
    SUBSCRIPTION
}
```
AmazonScreenType này để tách loại subscription và loại buy Gold ra làm 2
Đối với app, **Chỉ sử dụng loại SUBSCRIPTION**
## Đối với Background
```shell
ChangeBackgroundActivity.open(context)
```
## Quy trình sản xuất app
