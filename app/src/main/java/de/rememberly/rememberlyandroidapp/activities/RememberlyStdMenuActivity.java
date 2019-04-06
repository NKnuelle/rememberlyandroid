package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;

/**
 * Created by nilsk on 25.03.2019.
 */

public class RememberlyStdMenuActivity extends AnimationActivity {
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Adding standard menu to all activities
    }
    protected void setupStdToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        this.actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    protected void disableBackButton() {
        actionBar.setDisplayHomeAsUpEnabled(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogout:
                // User chose the "Settings" item, show the app settings UI...
                PreferencesManager.clearCredentials(this);
                Intent intent = new Intent(RememberlyStdMenuActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
