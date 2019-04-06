package de.rememberly.rememberlyandroidapp.controller;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import de.rememberly.rememberlyandroidapp.activities.NoteOverviewActivity;

/**
 * This controller controls categories. Usually they are shown as menu items (for example as spinners).
 */

public class CategoryListener implements AdapterView.OnItemSelectedListener {


    NoteOverviewActivity noteOverviewActivity;
    public CategoryListener(NoteOverviewActivity noteOverviewActivity) {
        this.noteOverviewActivity = noteOverviewActivity;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getItemAtPosition(pos).toString() != "+ New Category") {
            Log.e("Item to String: ", parent.getItemAtPosition(pos).toString());
            noteOverviewActivity.filterByCategory(parent.getItemAtPosition(pos).toString());
        } else {
            Log.e("Aufruf: ", "Ja!");
            noteOverviewActivity.showNewCategoryDialog();
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.e("Noting ", "Selected!");
    }
}
