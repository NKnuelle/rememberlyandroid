package de.rememberly.rememberlyandroidapp.local;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * This class is an entity class holding all of the users categories and
 * represents them in the local database.
 */
@Entity
public class Category {

    @NonNull
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    @PrimaryKey
    private String categoryName;

    public Category() {

    }
}
