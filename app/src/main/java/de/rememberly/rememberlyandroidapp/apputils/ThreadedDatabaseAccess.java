package de.rememberly.rememberlyandroidapp.apputils;

import de.rememberly.rememberlyandroidapp.activities.NoteOverviewActivity;
import de.rememberly.rememberlyandroidapp.local.Category;
import de.rememberly.rememberlyandroidapp.local.RememberlyDatabase;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;

/**
 * This class has methods to interact with the app's database.
 */

public class ThreadedDatabaseAccess {
    private static final ThreadedDatabaseAccess ourInstance = new ThreadedDatabaseAccess();

    public static ThreadedDatabaseAccess getInstance() {
        return ourInstance;
    }

    private ThreadedDatabaseAccess() {

    }
    public static void insertSingleCategory(RememberlyDatabase rememberlyDatabase, Category category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rememberlyDatabase.daoAccess () . insertSingleCategory (category);
            }
        }) .start();
    }
    public static void getAllCategoryNames(RememberlyDatabase rememberlyDatabase,
                                           NoteOverviewActivity noteOverviewActivity) {
        new Thread(() -> {
                String[] categoryNames = rememberlyDatabase.daoAccess().getAllCategoryNames();
                noteOverviewActivity.updateCategories(categoryNames);
            }
        ).start();
    }
}
