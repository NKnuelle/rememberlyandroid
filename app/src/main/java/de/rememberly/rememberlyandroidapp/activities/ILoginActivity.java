package de.rememberly.rememberlyandroidapp.activities;

import de.rememberly.rememberlyandroidapp.model.Token;

/**
 * Created by nilsk on 14.03.2019.
 */

public interface ILoginActivity {

    void onLoginSuccess(Token token);

    void onLoginError(String errormessage);

    void onLoginFailure(Throwable t);
}
