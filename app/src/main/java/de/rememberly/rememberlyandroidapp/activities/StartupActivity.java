package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.widget.Toast;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.remote.IApiCallback;

public class StartupActivity extends AnimationActivity implements IApiCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        ConstraintLayout constraintLayout = findViewById(R.id.AnimationRootLayout);
        super.setupAnimation(constraintLayout);
        init();
    }

    private void init() {
        String userToken = PreferencesManager.getUserToken(this);
        String userpassword = PreferencesManager.getUserPassword(this);
        String username = PreferencesManager.getUsername(this);
        String url = PreferencesManager.getURL(this);
        boolean credentialsOK = validateCredentials(userpassword, username, url);
        if (validateToken(userToken) && credentialsOK) {
            APICall apiCall = new APICall(url);
            apiCall.autoLogin(this, userToken, username, userpassword);
        }
        else if (validateCredentials(userpassword, username, url)) {
            APICall apiCall = new APICall(url);
            apiCall.userLogin(this, username, userpassword);

        } else {
            Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    public void onSuccess(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.LOGIN_REQUEST) {
            Token newToken = (Token) httpResponse;
            //Log.e("Token to store is: ", newToken.getToken());
            PreferencesManager.storeUserToken(newToken.getToken(), this);
            startMenuActivity();
        }
        if (requestCode == APICall.TOKEN_LOGIN) {
            startMenuActivity();
        }
    }
    public void onFailure(int requestCode, Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        startMenuActivity();
    }
    public void onError(int requestCode, HttpResponse httpResponse) {
        Toast.makeText(this, httpResponse.getMessage(), Toast.LENGTH_SHORT).show();
        startMenuActivity();
    }
    private void startMenuActivity() {
        Intent intent = new Intent(StartupActivity.this, MainMenu.class);
        startActivity(intent);
        StartupActivity.this.finish();
    }
    private boolean validateCredentials(String password, String username, String url) {
            if (username == null || username.trim().length() == 0) {
                return false;
            }
            if (password == null || password.trim().length() == 0) {
                return false;
            }
            if (url == null || url.trim().length() == 0) {
                return false;
            }
            return true;
        }
        private boolean validateToken(String token) {
            return !(token == null || token.trim().length() == 0);
        }
    }

