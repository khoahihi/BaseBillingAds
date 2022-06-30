package com.mmgsoft.modules.libs.data.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_entitlements")
public class EntitlementModel {
    public String userId;
    public String sku;
    public String receiptId;

    @ColumnInfo(name = "created_at")
    public String createdAt;

    public long purchaseDate;
    public long cancelDate;

    @PrimaryKey
    public Long id;
}
