package com.mmgsoft.modules.libs.data.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by KhoaND32 on 9/6/20.
 */
@Entity(tableName = "users")
public class User {

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @PrimaryKey
    public Long id;

    public String name;

    @ColumnInfo(name = "updated_at")
    public String updatedAt;
}
