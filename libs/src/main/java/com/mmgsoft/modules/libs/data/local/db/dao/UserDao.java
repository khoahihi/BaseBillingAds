package com.mmgsoft.modules.libs.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.mmgsoft.modules.libs.data.model.db.User;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by KhoaND32 on 9/6/20.
 */
@Dao
public interface UserDao {

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE name LIKE :name LIMIT 1")
    Single<User> findByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<User> users);


    @Query("SELECT * FROM users")
    List<User> loadAll();

    @Query("SELECT * FROM users WHERE id IN (:userIds)")
    List<User> loadAllByIds(List<Integer> userIds);

    @Query("SELECT * FROM users WHERE id =:id")
    User getById(int id);

    @Update
    void update(User user);

}
