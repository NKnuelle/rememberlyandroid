package de.rememberly.rememberlyandroidapp.controller;

import android.app.AlertDialog;
import android.widget.EditText;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.activities.AnimationActivity;
import de.rememberly.rememberlyandroidapp.apputils.ThreadedDatabaseAccess;
import de.rememberly.rememberlyandroidapp.local.Category;

/**
 * This class builds Dialogs for the apps views.
 */

public class DialogController {

    public void showNewCategoryDialog(AnimationActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final EditText categoryEdit = new EditText(activity);

        builder.setView(categoryEdit);

        builder.setMessage(R.string.newCategoryMessage)
                .setTitle(R.string.newCategoryTitle)
                .setPositiveButton(R.string.addCategory, (dialog, which) -> {
                    Category category = new Category();
                    category.setCategoryName(categoryEdit.getText().toString());
                    ThreadedDatabaseAccess.insertSingleCategory(activity.getDatabase(), category);
                })
                .setNegativeButton(R.string.cancelButton, (dialog, which) -> {
                    dialog.cancel();
                });
        builder.show();
    }
}
