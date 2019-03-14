package de.rememberly.rememberlyandroidapp.apputils;


import de.rememberly.rememberlyandroidapp.activities.ILoginActivity;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nilsk on 14.11.2018.
 */


public class APICall {

    private UserService userService;

    public APICall(String url) {
        this.userService = ApiUtils.getUserService(url);
    }

    public void autoLogin(final ILoginActivity iLoginActivity, final String userToken, final String username, final String password) {
        Call<HttpResponse> call = userService.tokenLogin("Bearer " + userToken);
        call.enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                if (response.isSuccessful()) {
                    iLoginActivity.onLoginSuccess();
                } else {

                }
            }
            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {

            }
        });
    }
    private void userLogin(final ILoginActivity iLoginActivity, final String username, final String password) {
        final String credentials = ApiUtils.getCredentialString(username, password);
        Call<Token> call = userService.login(credentials);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    iLoginActivity.onLoginSuccess();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }

}