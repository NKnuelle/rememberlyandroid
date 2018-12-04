package de.rememberly.rememberlyandroidapp.activities;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.rememberly.rememberlyandroidapp.R;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.view.MarkwonViewCompat;

public class NoticeActivity extends AppCompatActivity {

    private boolean boldClicked = false;
    private boolean renderClicked = false;
    String markdown = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        final MarkwonViewCompat markwonView = new MarkwonViewCompat(this);
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        final EditText editText = (EditText) findViewById(R.id.xmltestview);

        final Button button = (Button) findViewById(R.id.boldButton);

        final Button renderButton = (Button) findViewById(R.id.renderButton);

        final MarkwonViewCompat markwonViewCompat = findViewById(R.id.markwonview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (boldClicked) {
//                    plaintext = markwonView.getMarkdown();
//                    Log.i("Original Markdown is: ", plaintext);
//                    markwonView.setText(plaintext);
                    boldClicked = false;
                } else {
                    int cursorPosition = editText.getSelectionStart();
                    editText.getText().insert(cursorPosition, "****");
                    editText.setSelection(editText.getText().length()-2);
                    markdown = editText.getText().toString();
                    // save Markdown
                    markwonView.setMarkdown(markdown);
                    Markwon.setMarkdown(editText, markdown);

                    boldClicked = true;
                }

            }
        });
        renderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (renderClicked) {
                    editText.setText(markdown);
                    renderClicked = false;
                    renderButton.setText("Render");
                } else {
                    markdown = editText.getText().toString();
                    Markwon.setMarkdown(editText, markdown);
                    renderClicked = true;
                    renderButton.setText("Plain");
                    markwonViewCompat.setMarkdown(markdown);
                }
            }
        });
    }
}
