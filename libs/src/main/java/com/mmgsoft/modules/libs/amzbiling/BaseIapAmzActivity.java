package com.mmgsoft.modules.libs.amzbiling;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;
import com.mmgsoft.modules.libs.AdsApplication;
import com.mmgsoft.modules.libs.data.local.db.AppDbHelper;
import com.mmgsoft.modules.libs.data.model.db.EntitlementModel;
import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseIapAmzActivity extends AppCompatActivity implements PurchasingListener {
    private Set<String> allProductSkus = new HashSet<>();
    private String currentUserId;
    private String currentMarketPlace;

    public abstract Set<String> getAllSkus();

    public abstract Integer getResLayout();

    public abstract void initData();

    public abstract void notifyUpdateListView();

    private Unbinder mUnbinder;
    protected List<ProductItem> productItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allProductSkus.clear();
        allProductSkus.addAll(getAllSkus());
        setContentView(getResLayout());
        mUnbinder = ButterKnife.bind(this);
        PurchasingService.registerListener(this, this);
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PurchasingService.getProductData(allProductSkus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PurchasingService.getUserData();
        PurchasingService.getPurchaseUpdates(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null)
            mUnbinder.unbind();
    }

    public void onUserDataResponse(UserDataResponse userDataResponse) {
        final UserDataResponse.RequestStatus status = userDataResponse.getRequestStatus();
        switch (status) {
            case SUCCESSFUL:
                currentUserId = userDataResponse.getUserData().getUserId();
                currentMarketPlace = userDataResponse.getUserData().getMarketplace();
                break;
            case FAILED:
                break;
            case NOT_SUPPORTED:
                break;
        }
    }

    public void onProductDataResponse(ProductDataResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:

                final Map<String, Product> products = response.getProductData();
                Set<String> skuUnavailables = response.getUnavailableSkus();
                productItems.clear();
                for (final String key : products.keySet()) {
                    Product product = products.get(key);
                    ProductItem productItem = new ProductItem();
                    productItem.title = product.getTitle();
                    productItem.description = product.getDescription();
                    productItem.sku = product.getSku();
                    productItem.price = product.getPrice();
                    productItem.iapViewType = product.getProductType() == ProductType.SUBSCRIPTION ? IapViewType.SUB : IapViewType.IN_APP;
                    if (product.getProductType() == ProductType.CONSUMABLE)
                        productItem.isBuy = false;
                    else {
                        productItem.isBuy = skuUnavailables.contains(product.getSku());
                    }
                    productItems.add(productItem);
                }
                notifyUpdateListView();
                break;

            case FAILED:
                notifyUpdateListView();
                break;
        }
    }

    public void onPurchaseResponse(PurchaseResponse purchaseResponse) {
        switch (purchaseResponse.getRequestStatus()) {
            case SUCCESSFUL:
                currentUserId = purchaseResponse.getUserData().getUserId();
                currentMarketPlace = purchaseResponse.getUserData().getMarketplace();
                PurchasingService.notifyFulfillment(purchaseResponse.getReceipt().getReceiptId(), FulfillmentResult.FULFILLED);
                handleReceiptData(purchaseResponse.getReceipt());
                break;
            case FAILED:
                break;
        }
    }

    public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse) {
        switch (purchaseUpdatesResponse.getRequestStatus()) {
            case SUCCESSFUL:
                currentUserId = purchaseUpdatesResponse.getUserData().getUserId();
                currentMarketPlace = purchaseUpdatesResponse.getUserData().getMarketplace();
                for (final Receipt receipt : purchaseUpdatesResponse.getReceipts()) {
                    // Grant Item to User
                    handleReceiptData(receipt);

                }
                if (purchaseUpdatesResponse.hasMore()) {
                    PurchasingService.getPurchaseUpdates(true);
                }
                break;
            case FAILED:
                break;
        }
    }

    private void handleReceiptData(Receipt receipt) {
        switch (receipt.getProductType()) {
            case CONSUMABLE:
                break;
            case SUBSCRIPTION:
                SubscriptionModel subscriptionModel = new SubscriptionModel();
                subscriptionModel.userId = currentUserId;
                subscriptionModel.sku = receipt.getSku();
                subscriptionModel.receiptId = receipt.getReceiptId();
                subscriptionModel.fromDate = receipt.getPurchaseDate().getTime();
                subscriptionModel.toDate = receipt.getCancelDate().getTime();
                AdsApplication.instance.getDbHelper().insertSubscriptionRecord(subscriptionModel);
                break;
            case ENTITLED:
                EntitlementModel entitlementModel = new EntitlementModel();
                entitlementModel.userId = currentUserId;
                entitlementModel.sku = receipt.getSku();
                entitlementModel.receiptId = receipt.getReceiptId();
                entitlementModel.purchaseDate = receipt.getPurchaseDate().getTime();
                entitlementModel.cancelDate = receipt.getCancelDate() == null ? AppDbHelper.TO_DATE_NOT_SET : receipt.getCancelDate().getTime();
                AdsApplication.instance.getDbHelper().insertEntitlementRecord(entitlementModel);
                break;
        }
    }


    public Set<String> getAllProductSkus() {
        return allProductSkus;
    }

    public void setAllProductSkus(Set<String> allProductSkus) {
        this.allProductSkus = allProductSkus;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentMarketPlace() {
        return currentMarketPlace;
    }

    public void setCurrentMarketPlace(String currentMarketPlace) {
        this.currentMarketPlace = currentMarketPlace;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }
}
