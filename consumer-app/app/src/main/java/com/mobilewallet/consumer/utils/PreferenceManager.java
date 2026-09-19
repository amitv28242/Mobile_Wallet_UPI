package com.mobilewallet.consumer.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "wallet_prefs";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_BALANCE_VISIBLE = "balance_visible";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    private static SharedPreferences prefs;
    private static PreferenceManager instance;

    private PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
    }

    public static PreferenceManager getInstance() {
        return instance;
    }

    public boolean isBiometricEnabled() {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public boolean isBalanceVisible() {
        return prefs.getBoolean(KEY_BALANCE_VISIBLE, true);
    }

    public void setBalanceVisible(boolean visible) {
        prefs.edit().putBoolean(KEY_BALANCE_VISIBLE, visible).apply();
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, firstLaunch).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }
}