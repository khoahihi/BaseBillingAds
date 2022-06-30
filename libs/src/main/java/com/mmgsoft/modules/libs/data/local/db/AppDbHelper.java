package com.mmgsoft.modules.libs.data.local.db;


import com.amazon.device.iap.model.Receipt;
import com.mmgsoft.modules.libs.data.model.db.EntitlementModel;
import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel;

/**
 * Created by KhoaND32 on 9/6/20.
 */
public class AppDbHelper implements DbHelper {

    private final AppDatabase mAppDatabase;

    public AppDbHelper(AppDatabase appDatabase) {
        this.mAppDatabase = appDatabase;
    }

    @Override
    public boolean insertEntitlementRecord(EntitlementModel model) {
        long result = mAppDatabase.entitlementDao().insert(model);
        if (result > 0)
            return true;
        return false;
    }

    @Override
    public boolean insertSubscriptionRecord(SubscriptionModel model) {
        long result = mAppDatabase.subscriptionDao().insert(model);
        if (result > 0)
            return true;
        return false;
    }

    @Override
    public void revokeSubscription(Receipt receipt) {
        String receiptId = receipt.getReceiptId();
        SubscriptionModel subscriptionModel = mAppDatabase.subscriptionDao().findByReceiptId(receiptId);
        subscriptionModel.toDate = receipt.getCancelDate().getTime();
        mAppDatabase.subscriptionDao().update(subscriptionModel);
    }
}
