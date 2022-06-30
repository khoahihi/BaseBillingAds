package com.mmgsoft.modules.libs.data.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_subscriptions")
public class SubscriptionModel {
    public String userId;
    public String sku;
    public String receiptId;

    @ColumnInfo(name = "created_at")
    public String createdAt;

    public long fromDate;
    public long toDate;

    @PrimaryKey
    public Long id;

    public boolean isActive() {
        return System.currentTimeMillis() < toDate;
    }
}
