package com.mmgsoft.modules.libs.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mmgsoft.modules.libs.data.local.db.dao.EntitlementDao;
import com.mmgsoft.modules.libs.data.local.db.dao.SubscriptionDao;
import com.mmgsoft.modules.libs.data.local.db.dao.UserDao;
import com.mmgsoft.modules.libs.data.model.db.EntitlementModel;
import com.mmgsoft.modules.libs.data.model.db.SubscriptionModel;
import com.mmgsoft.modules.libs.data.model.db.User;


/**
 * Created by KhoaND32 on 9/6/20.
 */
@Database(entities = {User.class, EntitlementModel.class, SubscriptionModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract SubscriptionDao subscriptionDao();

    public abstract EntitlementDao entitlementDao();
}
