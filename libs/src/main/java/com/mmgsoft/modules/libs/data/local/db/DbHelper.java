package com.mmgsoft.modules.libs.data.local.db;

import com.amazon.device.iap.model.Receipt;
import com.mmgsoft.modules.libs.data.model.db.EntitlementModel;
import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel;

public interface DbHelper {
    long TO_DATE_NOT_SET = -1;

    public boolean insertEntitlementRecord(EntitlementModel model);

    public boolean insertSubscriptionRecord(SubscriptionModel model);

    void revokeSubscription(final Receipt receipt);
}
