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

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import de.rememberly.rememberlyandroidapp.R;
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
    CredentialsClient googleCredentialsClient;
    CredentialRequest googleCredentialRequest;
    final int RC_READ = 100;
    final int RC_SAVE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = ApiUtils.getUserService();

        // Try Token Login
        String userToken = ApiUtils.getUserToken(this);
        Log.i("Usertoken is: ", userToken);
        if (!TextUtils.isEmpty(userToken)) {
            tokenLogin(userToken);
        } else {
            buildActivity();
        }
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
                    Toast.makeText(LoginActivity.this, "Session expired, please login.",
                            Toast.LENGTH_LONG).show();
                    buildActivity();
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
        // Setup Smartlock and try to login with retrieved credentials
        setupSmartlock();

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
                    ApiUtils.storeUserToken(userToken.getToken(), LoginActivity.this);
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
    private void resolveResult(ResolvableApiException rae, int requestCode) {
        try {
            rae.startResolutionForResult(LoginActivity.this, requestCode);
            // mIsResolving = true;
        } catch (IntentSender.SendIntentException e) {
            Log.e("Login Error:", "Failed to send resolution.", e);
           //  hideProgress();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ...

        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
                doLogin(credentials.getId(), credentials.getPassword());
            } else {
                Log.e("Login Error: ", "Credential Read: NOT OK");
                Toast.makeText(this, "Credential Read Failed", Toast.LENGTH_SHORT).show();
            }
        }

        // ...

    }
    private void storeCredentials(String username, String password) {
        Credential credential = new Credential.Builder(username)
                .setPassword(password)  // Important: only store passwords in this field.
                // Android autofill uses this value to complete
                // sign-in forms, so repurposing this field will
                // likely cause errors.
                .build();
        googleCredentialsClient.save(credential).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d("Status: ", "SAVE: OK");
                            return;
                        }

                        Exception e = task.getException();
                        if (e instanceof ResolvableApiException) {
                            // Try to resolve the save request. This will prompt the user if
                            // the credential is new.
                            ResolvableApiException rae = (ResolvableApiException) e;
                            try {
                                rae.startResolutionForResult(LoginActivity.this, RC_SAVE);
                            } catch (IntentSender.SendIntentException exception) {
                                // Could not resolve the request
                                Log.e("Status: ", "Failed to send resolution.", e);
                                Toast.makeText(LoginActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Request has no resolution
                            Toast.makeText(LoginActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void setupSmartlock() {
        googleCredentialsClient = Credentials.getClient(this);
        googleCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();
        googleCredentialsClient.request(googleCredentialRequest).addOnCompleteListener(
                new OnCompleteListener<CredentialRequestResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<CredentialRequestResponse> task) {

                        if (task.isSuccessful()) {
                            // See "Handle successful credential requests"
                            Credential credentials = task.getResult().getCredential();
                            doLogin(credentials.getId(), credentials.getPassword());
                        }
                        Exception e = task.getException();
                        if (e instanceof ResolvableApiException) {
                            // This is most likely the case where the user has multiple saved
                            // credentials and needs to pick one. This requires showing UI to
                            // resolve the read request.
                            ResolvableApiException rae = (ResolvableApiException) e;
                            resolveResult(rae, RC_READ);
                        } else if (e instanceof ApiException) {
                            // The user must create an account or sign in manually.
                            Log.e("Login Error", "Unsuccessful credential request.", e);

                            ApiException ae = (ApiException) e;
                            int code = ae.getStatusCode();
                            // ...
                        }


                        // See "Handle unsuccessful and incomplete credential requests"
                        // ...
                    }
                });
    }


}
