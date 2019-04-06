package de.rememberly.rememberlyandroidapp.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

/**
 * This interface declares methods for local database abstraction.
 */
@Dao
public interface DaoAccess {

    @Insert
    void insertSingleCategory(Category category);
    @Delete
    void deleteCategory(Category category);
    @Query("SELECT categoryName FROM category")
    public String[] getAllCategoryNames();
}
