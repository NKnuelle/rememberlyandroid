package de.rememberly.rememberlyandroidapp.remote;

import de.rememberly.rememberlyandroidapp.service.UserService;
import okhttp3.Credentials;

public class ApiUtils {

    public static UserService getUserService(String url) {
        return RetrofitClient.getClient(url).create(UserService.class);
    }
    public static String getCredentialString(String username, String password) {
        return Credentials.basic(username, password);
    }

}
