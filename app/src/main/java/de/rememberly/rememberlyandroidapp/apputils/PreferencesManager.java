package de.rememberly.rememberlyandroidapp.apputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by nilsk on 14.11.2018.
 */

public class PreferencesManager {
    public static void storeUserToken(String JWTToken, Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        String[] encrypted = CryptoManager.encryptString("usertoken", JWTToken);
        spEditor.putString("tokenIV", encrypted[0]);
        spEditor.putString("usertoken", encrypted[1]);
        spEditor.commit();
    }
    public static String getUserToken(Context context) {
        SharedPreferences sharedPreferences;
        String decryptedToken = null;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        String encryptedToken = sharedPreferences.getString("usertoken", "NOT_SET");
        String iv = sharedPreferences.getString("tokenIV", "NOT_SET");
        try {
            decryptedToken = CryptoManager.decryptString("usertoken", iv, encryptedToken);
        } catch (Exception e) {
            Log.e("Token retrieval: ", e.getMessage());
        }
        return decryptedToken;
    }
    public static void storeUserPassword(String password, Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        String[] encrypted = CryptoManager.encryptString("userpassword", password);
        spEditor.putString("passwordIV", encrypted[0]);
        spEditor.putString("userpassword", encrypted[1]);
        spEditor.commit();
    }
    public static String getUserPassword(Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        String decryptedPassword = null;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        String encryptedPassword = sharedPreferences.getString("userpassword", "NOT_SET");
        String iv = sharedPreferences.getString("passwordIV", "NOT_SET");
        try {
            decryptedPassword = CryptoManager.decryptString("userpassword", iv, encryptedPassword);
        } catch (Exception e) {
            Log.e("Password retrieval: ", e.getMessage());
        }
        return decryptedPassword;
    }
    public static void storeUsername(String username, Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        spEditor.putString("username", username);
        spEditor.commit();
    }
    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "NOT_SET");
    }
    public static void storeURL(String url, Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        spEditor.putString("serverurl", url);
        spEditor.commit();
    }
    public static String getURL(Context context) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor spEditor;
        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
        return sharedPreferences.getString("serverurl", "NOT_SET");
    }
//    public static void storeIV(String iv, Context context) {
//        SharedPreferences sharedPreferences;
//        SharedPreferences.Editor spEditor;
//        sharedPreferences = context.getSharedPreferences("API_SETTINGS", Context.MODE_PRIVATE);
//        spEditor = sharedPreferences.edit();
//        spEditor.putString("IV", iv);
//        spEditor.commit();
//    }

}
