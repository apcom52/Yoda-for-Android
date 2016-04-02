package com.example.Yoda;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by apcom on 02.04.2016.
 */
public class SettingsSingleton {
    private static SettingsSingleton ourInstance = new SettingsSingleton();
    private static String SETTINGS_FILENAME = "settings";
    private static String SETTINGS_GROUP_NUMBER = "GROUP_NUMBER";
    private static String SETTINGS_HIDE_CANCELED_LESSONS = "HIDE_CANCELED_LESSONS";
    private static String SETTINGS_MARK_ENDED_LESSONS = "MARK_ENDED_LESSONS";
    private static Context context;
    private SharedPreferences settings;

    public static SettingsSingleton getInstance() {
        return ourInstance;
    }

    public void setContext(Context context) {
        this.context = context;
        settings = this.context.getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
    }

    private SettingsSingleton() {}

    public int getGroupNumber() {
        if (settings.contains(SETTINGS_GROUP_NUMBER)) {
            return settings.getInt(SETTINGS_GROUP_NUMBER, 1);
        } else {
            setGroupNumber(1);
            return getGroupNumber();
        }
    }

    public void setGroupNumber(int num) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SETTINGS_GROUP_NUMBER, num);
        editor.apply();
    }

    public Boolean getHideCanceled() {
        if (settings.contains(SETTINGS_HIDE_CANCELED_LESSONS)) {
            return settings.getBoolean(SETTINGS_HIDE_CANCELED_LESSONS, false);
        } else {
            setHideCanceled(false);
            return getHideCanceled();
        }
    }

    public void setHideCanceled(Boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SETTINGS_HIDE_CANCELED_LESSONS, value);
        editor.apply();
    }
}
