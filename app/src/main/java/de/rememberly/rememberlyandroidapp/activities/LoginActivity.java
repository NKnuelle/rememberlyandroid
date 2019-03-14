package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements ILoginActivity {

    EditText loginField;
    EditText passwordField;
    EditText urlField;
    Button loginButton;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init and build the activity
        buildActivity();
        init();

    }
    private void init() {

    }
    private void buildActivity() {
        // Build Login Activity
        setContentView(R.layout.activity_login);

        loginField = findViewById(R.id.loginfield);
        passwordField = findViewById(R.id.passwordfield);
        urlField = findViewById(R.id.urlfield);
        loginButton = findViewById(R.id.loginbutton);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.AnimationRootLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Log.i("Firebase ID: ", FirebaseInstanceId.getInstance().getId());

        initLoginButton();
    }
    private void initLoginButton () {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = loginField.getText().toString();
                String password = passwordField.getText().toString();
                String url = urlField.getText().toString();
                // validate form
                if (validateLogin(username, password, url)) {
                    // do login
                    userLogin(username, password, url);
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
    private void userLogin(final String username, final String password, final String url) {
        final String credentials = ApiUtils.getCredentialString(username, password);
        Call<Token> call = userService.login(credentials);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token userToken = response.body();
                    PreferencesManager.storeUserToken(userToken.getToken(), LoginActivity.this);
                    storeCredentials(username, password, url);
                    startMenuActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Username, password or URL incorrect",
                            Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
            }
        });
    }
    private void startMenuActivity() {
        Intent intent = new Intent(LoginActivity.this, MainMenu.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }
    public void onLoginSuccess() {
        startMenuActivity();
    }
}
