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

## Đối Với Google Billing
### Step 1 Extend `AdsApplication`
1. Override prodInAppIds
    - Trả về danh sách product ID của loại `IN_APP`
2. Override prodSubsIds
    - Trả về danh sách product ID của loại `SUBS`
3. Override billingType
    - Trả về `BillingType.AMAZON`
4. Override function `onCreated`
    - Nếu cần app cần config tác vụ thì override lại hàm này
5. Override function `addConfig`
    - VD:
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
`updateInterstitialKey()`: Gửi vào ProductID khi billing thành công sẽ loại bỏ Interstitial
`updateBannerKey()`: Gửi vào ProductID khi billing thành công sẽ loại bỏ Banner
`updateBillingMapper()`: Hàm này để định nghĩa ra ProductID so với số tiền (Khi billing loại ProductID nào sẽ cộng GOLD vào đúng với số đã config)

## END