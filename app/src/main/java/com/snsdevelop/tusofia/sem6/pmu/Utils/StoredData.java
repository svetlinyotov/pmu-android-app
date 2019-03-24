package com.snsdevelop.tusofia.sem6.pmu.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StoredData {

    public static final String LOGGED_USER_ORIGIN = "logged_user_origin";
    public static final String LOGGED_USER_ID = "logged_user_id";
    public static final String LOGGED_USER_EMAIL = "logged_user_email";
    public static final String LOGGED_USER_NAME = "logged_user_name";
    public static final String LOGGED_USER_IMAGE = "logged_user_image";
    public static final String LOGGED_USER_TOKEN = "logged_user_token";

    static final String SETTINGS_IS_MUTED = "settingsIsBGMusicMuted";

    public static void saveInt(Context context, String key, Integer value) {
        SharedPreferences.Editor edit = getSP(context).edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor edit = getSP(context).edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static void saveLong(Context context, String key, Long value) {
        SharedPreferences.Editor edit = getSP(context).edit();
        edit.putLong(key, value);
        edit.apply();
    }

    private static SharedPreferences getSP(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(Context context, String value) {
        return getSP(context).getString(value, null);
    }

    public static Boolean getBoolean(Context context, String value) {
        return getSP(context).getBoolean(value, false);
    }

    public static int getInt(Context context, String value) {
        return getSP(context).getInt(value, 0);
    }

    public static long getLong(Context context, String value) {
        return getSP(context).getLong(value, 0L);
    }

}