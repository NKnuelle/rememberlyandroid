package de.rememberly.rememberlyandroidapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.remote.NextCloudManager;
import top.defaults.colorpicker.ColorPickerPopup;

public class EditorActivity extends AppCompatActivity {
    Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editorlayout);

        setupEditor();

    }


    private void setupEditor() {
            editor = (Editor) findViewById(R.id.editor);
            findViewById(R.id.action_h1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.H1);
                }
            });

            findViewById(R.id.action_h2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.H2);
                }
            });

            findViewById(R.id.action_h3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.H3);
                }
            });

            findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.BOLD);
                }
            });

            findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.ITALIC);
                }
            });

            findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.INDENT);
                }
            });

            findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.updateTextStyle(EditorTextStyle.OUTDENT);
                }
            });

            findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.insertList(false);
                }
            });

            findViewById(R.id.action_color).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ColorPickerPopup.Builder(EditorActivity.this)
                            .initialColor(Color.RED) // Set initial color
                            .enableAlpha(true) // Enable alpha slider or not
                            .okTitle("Choose")
                            .cancelTitle("Cancel")
                            .showIndicator(true)
                            .showValue(true)
                            .build()
                            .show(findViewById(android.R.id.content), new ColorPickerPopup.ColorPickerObserver() {
                                @Override
                                public void onColorPicked(int color) {
                                    Toast.makeText(EditorActivity.this, "picked" + colorHex(color), Toast.LENGTH_LONG).show();
                                    editor.updateTextColor(colorHex(color));
                                }

                                @Override
                                public void onColor(int color, boolean fromUser) {

                                }
                            });


                }
            });

            findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.insertList(true);
                }
            });

            findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.insertDivider();
                }
            });

        /*
        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.openImagePicker();
            }
        });
        */

            findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.insertLink();
                }
            });

        /*
        findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertMap();
            }
        });
        */

            findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.clearAllContents();
                }
            });

        /*
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        editor.setHeadingTypeface(headingTypeface);
        editor.setContentTypeface(contentTypeface);
        */
            editor.setDividerLayout(R.layout.tmpl_divider_layout);
            editor.setEditorImageLayout(R.layout.tmpl_image_view);
            editor.setListItemLayout(R.layout.tmpl_list_item);
            //editor.setNormalTextSize(10);
            // editor.setEditorTextColor("#FF3333");
            //editor.StartEditor();
            editor.setEditorListener(new EditorListener() {
                @Override
                public void onTextChanged(EditText editText, Editable text) {
                    // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUpload(Bitmap image, String uuid) {
                    Toast.makeText(EditorActivity.this, uuid, Toast.LENGTH_LONG).show();
                    /**
                     * TODO do your upload here from the bitmap received and all onImageUploadComplete(String url); to insert the result url to
                     * let the editor know the upload has completed
                     */

                    editor.onImageUploadComplete("http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg", uuid);
                    // editor.onImageUploadFailed(uuid);
                }

            });

            findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*
                Retrieve the content as serialized, you could also say getContentAsHTML();
                */
                String html = editor.getContentAsHTML();
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput("Testhtml.html", Context.MODE_PRIVATE);
                        outputStream.write(html.getBytes());
                        outputStream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                Log.i("HTML is: ", html);
                // TODO: Save HTML and Upload it to Server
                    File file = new File(getFilesDir() + "/" + "Testhtml.html");
                    NextCloudManager nextCloudManager = new NextCloudManager(EditorActivity.this);
                    nextCloudManager.startUpload(file, "text/html");
                /*
                Intent intent = new Intent(getApplicationContext(), RenderActivity.class);
                intent.putExtra("content", html);
                startActivity(intent);
                */

                }
            });

            editor.render();

        }

    private String colorHex(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "#%02X%02X%02X", r, g, b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                editor.insertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            // editor.RestoreState();
        } else if (requestCode == editor.MAP_MARKER_REQUEST) {
            editor.insertMap(data.getStringExtra("cords"));
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Editor?")
                .setMessage("Are you sure you want to exit the editor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    public Map<Integer, String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/GreycliffCF-Bold.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/GreycliffCF-Bold.ttf");
        return typefaceMap;
    }

    public Map<Integer, String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }
}
