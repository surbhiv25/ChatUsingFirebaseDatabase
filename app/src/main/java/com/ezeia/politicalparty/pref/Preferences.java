package com.ezeia.politicalparty.pref;

/*
 * Created by riyazudinp on 7/21/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static SharedPreferences sp;
    public static SharedPreferences filpref;
    public static SharedPreferences.Editor fileditor;

    private static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    private static final String SCHEME = "SCHEME";
    private static final String DISTRICT = "DISTRICT";
    private static final String ROLE = "ROLE";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    private static SharedPreferences getInstance(Context context) {
        if (sp == null) {
            String APP_SHARED_PREFERENCE = "Political_Preference";
            sp = context.getSharedPreferences(APP_SHARED_PREFERENCE, Activity.MODE_PRIVATE);
        }
        return sp;
    }

    public static void setDeviceToken(Context context, String deviceToken) {
        getInstance(context).edit().putString(DEVICE_TOKEN, deviceToken).apply();
    }

    public static String getDeviceToken(Context context) {
        return getInstance(context).getString(DEVICE_TOKEN, "");
    }

    public static void setPassword(Context context, String password) {
        getInstance(context).edit().putString(PASSWORD, password).apply();
    }

    public static String getPassword(Context context) {
        return getInstance(context).getString(PASSWORD, "");
    }

    public static void setDistrict(Context context, String district) {
        getInstance(context).edit().putString(DISTRICT, district).apply();
    }

    public static String getDistrict(Context context) {
        return getInstance(context).getString(DISTRICT, "");
    }

    public static void setRole(Context context, String role) {
        getInstance(context).edit().putString(ROLE, role).apply();
    }

    public static String getRole(Context context) {
        return getInstance(context).getString(ROLE, "");
    }

    public static void setScheme(Context context, String scheme) {
        getInstance(context).edit().putString(SCHEME, scheme).apply();
    }

    public static String getScheme(Context context) {
        return getInstance(context).getString(SCHEME, "");
    }

    public static void setUsername(Context context, String userName) {
        getInstance(context).edit().putString(USERNAME, userName).apply();
    }

    public static String getUsername(Context context) {
        return getInstance(context).getString(USERNAME, "");
    }

    public static void clearAllPreference(Context context) {
        getInstance(context).edit().clear().apply();
    }

}

