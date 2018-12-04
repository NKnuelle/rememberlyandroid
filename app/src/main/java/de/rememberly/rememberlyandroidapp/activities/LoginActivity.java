package de.rememberly.rememberlyandroidapp.activities;

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
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.CryptoManager;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.ReturnMessage;
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
//    CredentialsClient googleCredentialsClient;
//    CredentialRequest googleCredentialRequest;
//    final int RC_READ = 100;
//    final int RC_SAVE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();

        // Try to login or build activity
        login();
    }
    private void login() {
        String userToken = PreferencesManager.getUserToken(this);
        if (!TextUtils.isEmpty(userToken)) {
            tokenLogin(userToken);
            Log.i("Usertoken is: ", userToken);
        }
        if (!TextUtils.isEmpty(PreferencesManager.getUserPassword(this))
                && !TextUtils.isEmpty(PreferencesManager.getUsername(this))) {
            doLogin(PreferencesManager.getUsername(this), PreferencesManager.getUserPassword(this));
        }
        buildActivity();
    }
    private void tokenLogin(String userToken) {
        Call<ReturnMessage> call = userService.tokenLogin("Bearer " + userToken);
        call.enqueue(new Callback<ReturnMessage>() {
            @Override
            public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                if (response.isSuccessful()) {
                    ReturnMessage returnMessage = response.body();
                    Intent intent = new Intent(LoginActivity.this, MainMenu.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    Log.e("Token login: ", "Failed");
                }
            }
            @Override
            public void onFailure(Call<ReturnMessage> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    doLogin(username, password);
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
    private void doLogin(final String username, final String password) {
        final String credentials = ApiUtils.getCredentialString(username, password);
        Call<Token> call = userService.login(credentials);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token userToken = response.body();
                    PreferencesManager.storeUserToken(userToken.getToken(), LoginActivity.this);
                    storeCredentials(username, password);
                    Intent intent = new Intent(LoginActivity.this, MainMenu.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Username or password incorrect",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void storeCredentials(String username, String password) {
        PreferencesManager.storeUsername(username, this);
        PreferencesManager.storeUserPassword(password, this);
        Log.i("Credentials stored: ", username + " " + password);
    }
}
