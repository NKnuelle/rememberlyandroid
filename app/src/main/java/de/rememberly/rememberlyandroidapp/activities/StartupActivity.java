package de.rememberly.rememberlyandroidapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartupActivity extends AppCompatActivity {

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();
        setContentView(R.layout.activity_startup);
        init();
    }

    private void init() {
        String userToken = PreferencesManager.getUserToken(this);
        String userpassword = PreferencesManager.getUserPassword(this);
        String username = PreferencesManager.getUsername(this);
        autoLogin(userToken, username, userpassword);
    }
    private void autoLogin(final String userToken, final String username, final String password) {
        Call<HttpResponse> call = userService.tokenLogin("Bearer " + userToken);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("Login: ", "Successful. Starting MainMenu.");
                    Intent intent = new Intent(StartupActivity.this, MainMenu.class);
                    startActivity(intent);
                } else {
                    Log.e("Token login: ", "Failed");
                    // try login with username + password
                    userLogin(username, password);
                }
            }
            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                Toast.makeText(StartupActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void userLogin(final String username, final String password) {
        final String credentials = ApiUtils.getCredentialString(username, password);
        Call<Token> call = userService.login(credentials);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token userToken = response.body();
                    PreferencesManager.storeUserToken(userToken.getToken(), StartupActivity.this);
                    Intent intent = new Intent(StartupActivity.this, MainMenu.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(StartupActivity.this, "Login failed",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(StartupActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
