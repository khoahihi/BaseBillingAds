package com.mmgsoft.modules.libs.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Query;


import com.mmgsoft.modules.libs.data.model.db.EntitlementModel;

import java.util.List;

@Dao
public abstract class EntitlementDao extends BaseDao<EntitlementModel> {
    @Query("SELECT * FROM tbl_entitlements")
    public abstract List<EntitlementModel> loadAll();

    @Query("SELECT * FROM tbl_entitlements where receiptId =:receiptId")
    public abstract EntitlementModel findByReceiptId(String receiptId);

    @Query("SELECT * FROM tbl_entitlements where userId =:userId AND sku =:sku")
    public abstract List<EntitlementModel> findByUserIdAndSku(String userId, String sku);
}
