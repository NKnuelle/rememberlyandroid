package de.rememberly.rememberlyandroidapp.activities;

/**
 * Created by nilsk on 14.03.2019.
 */

public interface ILoginActivity {

    void onLoginSuccess();

    void onLoginError(String errormessage);

    void onLoginFailure(Throwable t);
}
