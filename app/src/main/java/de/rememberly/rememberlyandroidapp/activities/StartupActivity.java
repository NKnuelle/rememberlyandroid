package de.rememberly.rememberlyandroidapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.apputils.APICall;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Token;

public class StartupActivity extends AppCompatActivity implements ILoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        init();
    }

    private void init() {
        String userToken = PreferencesManager.getUserToken(this);
        String userpassword = PreferencesManager.getUserPassword(this);
        String username = PreferencesManager.getUsername(this);
        APICall apiCall = new APICall(PreferencesManager.getURL(this));
    }
    public void onLoginSuccess(Token token) {

    }
    public void onLoginFailure(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }
    public void onLoginError(String errormessage) {
        Toast.makeText(this, errormessage, Toast.LENGTH_SHORT).show();
    }
}
