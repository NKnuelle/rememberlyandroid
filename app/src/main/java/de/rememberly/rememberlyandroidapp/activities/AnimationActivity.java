package de.rememberly.rememberlyandroidapp.activities;

import android.arch.persistence.room.Room;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import de.rememberly.rememberlyandroidapp.local.RememberlyDatabase;


/**
 * Created by nilsk on 24.03.2019.
 */

public class AnimationActivity extends AppCompatActivity {
    RememberlyDatabase rememberlyDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void setupAnimation(ViewGroup viewGroup) {
        AnimationDrawable animationDrawable = (AnimationDrawable) viewGroup.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
    protected void setupDatabase() {
        final String DATABASE_NAME = "rememberly_db";
        rememberlyDatabase = Room.databaseBuilder(getApplicationContext(),
                RememberlyDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }
    public RememberlyDatabase getDatabase() {
        return rememberlyDatabase;
    }
}
