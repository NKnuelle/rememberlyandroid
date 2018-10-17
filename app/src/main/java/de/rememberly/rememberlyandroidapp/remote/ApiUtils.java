package de.rememberly.rememberlyandroidapp.remote;

import android.content.Context;
import android.content.SharedPreferences;

import de.rememberly.rememberlyandroidapp.model.Token;
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
    public static void storeUserToken(String JWTToken, Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        spEditor.putString("usertoken", JWTToken);
        spEditor.commit();
    }
    public static String getUserToken(Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        return sharedPreferences.getString("usertoken", "NOT_SET");
    }
}
