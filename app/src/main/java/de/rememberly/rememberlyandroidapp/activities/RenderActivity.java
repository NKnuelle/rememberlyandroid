package de.rememberly.rememberlyandroidapp.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import org.kobjects.nativehtml.android.AndroidContainerElement;
import org.kobjects.nativehtml.android.AndroidInputElement;
import org.kobjects.nativehtml.android.HtmlView;
import org.kobjects.nativehtml.dom.HtmlCollection;

import java.io.FileOutputStream;
import java.net.URI;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.fragments.HtmlViewFragment;

/**
 * Created by nilsk on 22.11.2018.
 */

public class RenderActivity extends AppCompatActivity{
    URI indexUrl;
    HtmlView htmlViewUndone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.renderlayout);
        WebView webView = findViewById(R.id.webview);


//        setContentView(R.layout.todolist_layout);
//
//        ScrollView scrollView = findViewById(R.id.AnimationRootLayout);
//
//        LinearLayout linearLayoutUndone = findViewById(R.id.checkedtodolayout);
//        htmlViewUndone = new HtmlView(this);
//        linearLayoutUndone.addView(htmlViewUndone, 0);
//
//        AnimationDrawable animationDrawable = (AnimationDrawable) scrollView.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();

//        Intent intent = this.getIntent();
//        String html = intent.getStringExtra("content");
        String html = "<input type=\"checkbox\">test text" + "<br>"
                + "<input type=\"radio\" checked=\"checked\">test text" + "<br>"
                + "<input type=\"file\" >test text" + "<br>"
                + "<input type=\"range\" >test text" + "<br>"
                + "<input type=\"time\" >test text" + "<br>"
                + "<table><tr><th>firstname</th></tr><tr><td>Jill</td></tr></table>";
        String filename = "test.html";
        webView.loadData(html, "text/html", "utf-8");
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(html.getBytes());
            outputStream.close();
            indexUrl = getFileStreamPath("test.html").toURI();

            Log.i("URI: ", indexUrl.toString());
            String prefix = indexUrl.toString();
            int cut = prefix.lastIndexOf('/');
            prefix = prefix.substring(0, cut + 1);
            // htmlViewUndone.addInternalLinkPrefix(prefix);
            /*
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            htmlViewUndone.loadHtml(URI.create("https://typo3.nils-kretschmer.de"));
            */
            // htmlViewUndone.loadHtml(indexUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        AndroidContainerElement container  =  (AndroidContainerElement) htmlViewUndone.getChildAt(0);
        HtmlCollection htmlCollection = container.getChildren();
        Log.i("Collection length: ", String.valueOf(htmlCollection.getLength()));
        AndroidInputElement androidInputElement = (AndroidInputElement) htmlCollection.item(0);
        Drawable checkBoxBackground = getResources().getDrawable(R.drawable.dialogrounded);
        CheckBox checkBox = ((CheckBox) androidInputElement.getChildAt(0));
        checkBox.setBackground(checkBoxBackground);
        Log.i("Class 1: " , htmlCollection.item(0).getClass().toString());
        */

    }
}






