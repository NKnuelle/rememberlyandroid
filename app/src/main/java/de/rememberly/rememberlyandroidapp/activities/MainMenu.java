package de.rememberly.rememberlyandroidapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import de.rememberly.rememberlyandroidapp.R;

public class MainMenu extends AppCompatActivity {
    TextView todolists;
    TextView notices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        todolists = findViewById(R.id.todolists);
        notices = findViewById(R.id.notices);


        todolists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, TodolistActivity.class);
                startActivity(intent);
            }
        });
        notices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, NoticesOverviewActivity.class);
                startActivity(intent);
            }
        });

    }
}
