package de.rememberly.rememberlyandroidapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Note;

public class MainMenu extends RememberlyStdMenuActivity {
    TextView todolists;
    TextView notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        todolists = findViewById(R.id.todolists);
        notes = findViewById(R.id.notes);
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(linearLayout);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        super.setupStdToolbar(mainToolbar);
        super.disableBackButton();

        todolists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, TodolistActivity.class);
                startActivity(intent);
            }
        });
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, NoteOverviewActivity.class);
                startActivity(intent);
            }
        });

    }
}
