package de.rememberly.rememberlyandroidapp.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Database class for local data storage.
 */
@Database(entities = Category.class, version = 1, exportSchema = false)
public abstract class RememberlyDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}
