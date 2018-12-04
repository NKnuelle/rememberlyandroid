package de.rememberly.rememberlyandroidapp.remote;

import de.rememberly.rememberlyandroidapp.service.UserService;
import okhttp3.Credentials;

public class ApiUtils {

    public static final String BASE_URL = "https://rememberly.nils-kretschmer.de/";

    /*
    public static Token getUserToken() {
        return userToken;
    }

    public static void setUserToken(Token userToken) {
        ApiUtils.userToken = userToken;
    }
    */


    public static String getBaseUrl() {
        return BASE_URL;
    }



    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
    public static String getCredentialString(String username, String password) {
        return Credentials.basic(username, password);
    }

}
