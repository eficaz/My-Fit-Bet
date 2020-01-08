package com.eficaz_fitbet_android.fitbet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public class AppPreference {

    private static AppPreference appPreference;
    private SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;
    private Context context;



    public AppPreference() {
        this.context = SLApplication.getContext();
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static AppPreference getPrefsHelper() {
        if (appPreference == null)
            appPreference = new AppPreference();
        return appPreference;
    }

    public AppPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static AppPreference getPrefsHelper(Context context) {
        if (appPreference == null)
            appPreference = new AppPreference(context);
        return appPreference;
    }

    public void savePref(String key, Object value) {
        getPrefsHelper();
        delete(key);

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-primitive preference");
        }
        editor.commit();
    }
    public <T> T getPref(String key, T defValue) {
        getPrefsHelper();
        T returnValue = (T) sharedPreferences.getAll().get(key);
        returnValue = returnValue == null ? defValue : returnValue;
        return returnValue;
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key))
            editor.remove(key).commit();
    }

}
