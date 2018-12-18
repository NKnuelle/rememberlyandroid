package de.rememberly.rememberlyandroidapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.irshulx.Editor;

import de.rememberly.rememberlyandroidapp.R;

public class RenderActivity extends AppCompatActivity {
    Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editorlayout);

        editor = (Editor) findViewById(R.id.editor);
        String html = getIntent().getStringExtra("content");

        editor.render(html);

    }
}