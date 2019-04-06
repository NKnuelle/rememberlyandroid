package de.rememberly.rememberlyandroidapp.apputils;

import android.util.Log;

/**
 * Created by nilsk on 05.04.2019.
 */

public class StringValidator {
    private static final StringValidator ourInstance = new StringValidator();

    public static StringValidator getInstance() {
        return ourInstance;
    }

    private StringValidator() {
    }
    public static boolean isEmptyOrNull(String stringToValidate) {
        if (stringToValidate != null && !stringToValidate.trim().isEmpty()) {
            Log.d("String validation: ", "String is fine!");
            return false;
        } else {
            Log.e("String validation: ", "String is bad!");
            return true;
        }
    }
}
