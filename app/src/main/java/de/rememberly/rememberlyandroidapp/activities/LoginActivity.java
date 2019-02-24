package de.rememberly.rememberlyandroidapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.CryptoManager;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText loginField;
    EditText passwordField;
    Button loginButton;
    UserService userService;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();
        progressBar = findViewById(R.id.loginbar);
        progressBar.setVisibility(View.INVISIBLE);

        // init and build the activity
        buildActivity();
        init();

    }
    private void init() {

        String userToken = PreferencesManager.getUserToken(this);
        String userpassword = PreferencesManager.getUserPassword(this);
        String username = PreferencesManager.getUsername(this);
        autoLogin(userToken, username, userpassword);
    }
    private void buildActivity() {
        // Build Login Activity
        setContentView(R.layout.activity_login);

        loginField = findViewById(R.id.loginfield);
        passwordField = findViewById(R.id.passwordfield);
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
                // validate form
                if (validateLogin(username, password)) {
                    // do login
                    userLogin(username, password);
                }
            }
        });

    }
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void storeCredentials(String username, String password) {
        PreferencesManager.storeUsername(username, this);
        PreferencesManager.storeUserPassword(password, this);
        Log.i("Credentials stored: ", username + " " + password);
    }
    private void userLogin(final String username, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        final String credentials = ApiUtils.getCredentialString(username, password);
        Call<Token> call = userService.login(credentials);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token userToken = response.body();
                    PreferencesManager.storeUserToken(userToken.getToken(), LoginActivity.this);
                    storeCredentials(username, password);
                    startMenuActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password incorrect",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
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
    private void autoLogin(final String userToken, final String username, final String password) {
        // deactivate loginbutton - gets activated if login fails
        loginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Call<HttpResponse> call = userService.tokenLogin("Bearer " + userToken);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    startMenuActivity();
                } else {
                    Log.e("Token login: ", "Failed");
                    // try login with username + password
                    userLogin(username, password);
                }
            }
            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startMenuActivity() {
        Intent intent = new Intent(LoginActivity.this, MainMenu.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }
}
