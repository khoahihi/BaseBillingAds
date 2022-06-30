package com.mmgsoft.modules.libs.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Query;


import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel;

import java.util.List;

@Dao
public abstract class SubscriptionDao extends BaseDao<SubscriptionModel> {
    @Query("SELECT * FROM tbl_subscriptions")
    public abstract List<SubscriptionModel> loadAll();

    @Query("SELECT * FROM tbl_subscriptions where receiptId =:receiptId")
    public abstract SubscriptionModel findByReceiptId(String receiptId);

    @Query("SELECT * FROM tbl_subscriptions where userId =:userId AND sku =:sku")
    public abstract List<SubscriptionModel> findByUserIdAndSku(String userId, String sku);
}
