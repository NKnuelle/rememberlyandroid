package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;

public class LoginActivity extends AnimationActivity implements IApiCallback {

    EditText loginField;
    EditText passwordField;
    EditText urlField;
    Button loginButton;
    String username;
    String password;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup background animation
        setContentView(R.layout.activity_login);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(constraintLayout);

        // init and build the activity
        buildActivity();

    }
    private void buildActivity() {
        // Build Login Activity


        loginField = findViewById(R.id.loginfield);
        passwordField = findViewById(R.id.passwordfield);
        urlField = findViewById(R.id.urlfield);
        loginButton = findViewById(R.id.loginbutton);

        Log.i("Firebase ID: ", FirebaseInstanceId.getInstance().getId());

        initLoginButton();
    }
    private void initLoginButton () {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = loginField.getText().toString();
                password = passwordField.getText().toString();
                url = urlField.getText().toString();
                // validate form
                if (validateLogin(username, password, url)) {
                    // do login
                    APICall apiCall = new APICall(url);
                    apiCall.userLogin(LoginActivity.this, username, password);
                }
            }
        });

    }
    private boolean validateLogin(String username, String password, String url) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (url == null || url.trim().length() == 0) {
            Toast.makeText(this, "Server URL is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void storeCredentials(String username, String password, String url) {
        PreferencesManager.storeUsername(username, this);
        PreferencesManager.storeUserPassword(password, this);
        PreferencesManager.storeURL(url, this);
    }

    private void startMenuActivity() {
        Intent intent = new Intent(LoginActivity.this, MainMenu.class);
        finish();
        startActivity(intent);
    }
    public void onSuccess(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.LOGIN_REQUEST) {
            Token newToken = (Token) httpResponse;
            PreferencesManager.storeUserToken(newToken.getToken(), LoginActivity.this);
            storeCredentials(username, password, url);
            startMenuActivity();
        }
    }
    public void onFailure(int requestCode, Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
    }
    public void onError(int requestCode, HttpResponse httpResponse) {
        Toast.makeText(this, httpResponse.getMessage(), Toast.LENGTH_LONG).show();
    }
}
