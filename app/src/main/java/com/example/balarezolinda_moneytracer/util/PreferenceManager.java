package com.example.balarezolinda_moneytracer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREFS_NAME = "MoneyTracerPrefs";
    private static final String KEY_IS_ONBOARDING_COMPLETE = "isOnboardingComplete";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_BUDGET = "budget";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_START_DAY = "startDay";

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isOnboardingComplete() {
        return sharedPreferences.getBoolean(KEY_IS_ONBOARDING_COMPLETE, false);
    }

    public void setOnboardingComplete(boolean complete) {
        sharedPreferences.edit().putBoolean(KEY_IS_ONBOARDING_COMPLETE, complete).apply();
    }

    public void saveUserSettings(String name, float budget, String currency, int startDay) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, name);
        editor.putFloat(KEY_BUDGET, budget);
        editor.putString(KEY_CURRENCY, currency);
        editor.putInt(KEY_START_DAY, startDay);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public float getBudget() {
        return sharedPreferences.getFloat(KEY_BUDGET, 0.0f);
    }

    public String getCurrency() {
        return sharedPreferences.getString(KEY_CURRENCY, "USD");
    }

    public int getStartDay() {
        return sharedPreferences.getInt(KEY_START_DAY, 1);
    }
}
