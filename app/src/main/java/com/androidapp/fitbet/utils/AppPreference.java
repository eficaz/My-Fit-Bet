package com.androidapp.fitbet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidapp.fitbet.polyline.Route;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class AppPreference {

    private static AppPreference appPreference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String TAG_DISTANCE = "distance_in_double";

    private static final String TAG_ROUTE = "tag_user_route";
    private static final String TAG_ORIGIN = "tag_origin";
    private static final String TAG_SAVED_STATUS = "saved_status";
    private static final String TAG_SAVED_LAT_LONG_LIST = "savedlist";
    private static final String TAG_PROFILE_NAME = "profile_name";
    private static final String TAG_PROFILE_IMAGE = "profile_image";
    private static final String TAG_BET_ID = "bet_id";
    private static final String TAG_BET_DATE = "bet_date";
    private static final String TAG_CHALLENGER_ID = "challenger_id";
    private static final String TAG_COUNTDOWN_TIME = "countdown_time";
    private static final String TAG_BET_TYPE = "bet_type";
    private static final String TAG_POSITION_LATITUDE = "position_latitude";
    private static final String TAG_POSITION_LONGITUDE = "position_longitude";


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

    public void saveDistance(String distance) {
        editor.putString(TAG_DISTANCE, distance);
        editor.commit();

    }

    public String getSavedDistance() {

        return sharedPreferences.getString(TAG_DISTANCE, "0.0");
    }

    public void saveUserRoute(String route) {
        editor.putString(TAG_ROUTE, route);
        editor.commit();

    }

    public String getSavedUserRoute() {

        return sharedPreferences.getString(TAG_ROUTE, "");
    }


    public void saveOrigin(String origin) {
        editor.putString(TAG_ORIGIN, origin);
        editor.commit();

    }

    public String getSavedOrigin() {

        return sharedPreferences.getString(TAG_ORIGIN, "");
    }

    public void savedStatusFlag(boolean status) {
        editor.putBoolean(TAG_SAVED_STATUS, status);
        editor.commit();

    }

    public boolean getSavedStatusFlag() {
        return sharedPreferences.getBoolean(TAG_SAVED_STATUS, false);

    }

    public <T> void setLatLongList(List<Route> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        setList(TAG_SAVED_LAT_LONG_LIST, json);
    }

    public void setList(String key, String value) {
        if (sharedPreferences != null) {

            editor.putString(key, value);
            editor.commit();
        }
    }

    public List<Route> getRouteList() {
        if (sharedPreferences != null) {

            Gson gson = new Gson();
            List<Route> routeList;
            String json = sharedPreferences.getString(TAG_SAVED_LAT_LONG_LIST, "");
            Type type = new TypeToken<List<Route>>() {
            }.getType();
            routeList = gson.fromJson(json, type);
            return routeList;
        }
        return null;
    }


    public void saveProfileImage(String imageUrl) {
        editor.putString(TAG_PROFILE_IMAGE, imageUrl);
        editor.commit();

    }

    public String getSavedProfileImage() {

        return sharedPreferences.getString(TAG_PROFILE_IMAGE, "");
    }

    public void saveProfileName(String name) {
        editor.putString(TAG_PROFILE_NAME, name);
        editor.commit();

    }

    public String getSavedProfileName() {

        return sharedPreferences.getString(TAG_PROFILE_NAME, "");
    }


    public void saveBetId(String betid) {
        editor.putString(TAG_BET_ID, betid);
        editor.commit();

    }

    public String getSavedBetId() {

        return sharedPreferences.getString(TAG_BET_ID, "");
    }


    public void saveBetDate(String date) {
        editor.putString(TAG_BET_DATE, date);
        editor.commit();

    }

    public String getSavedBetDate() {

        return sharedPreferences.getString(TAG_BET_DATE, "");
    }


    public void saveChallengerId(String challengerId) {
        editor.putString(TAG_CHALLENGER_ID, challengerId);
        editor.commit();

    }

    public String getSavedChallengerId() {

        return sharedPreferences.getString(TAG_CHALLENGER_ID, "");
    }


    public void saveCountDownTime(int count) {
        editor.putInt(TAG_COUNTDOWN_TIME, count);
        editor.commit();

    }

    public int getSavedCountdownTime() {

        return sharedPreferences.getInt(TAG_COUNTDOWN_TIME, 0);
    }

    public void saveBetType(String betType) {
        editor.putString(TAG_BET_TYPE, betType);
        editor.commit();

    }

    public String getSavedBetType() {

        return sharedPreferences.getString(TAG_BET_TYPE, "");
    }

    public void savePositionLatitude(String positionLatitude) {
        editor.putString(TAG_POSITION_LATITUDE, positionLatitude);
        editor.commit();

    }

    public String getPositionLatitude() {

        return sharedPreferences.getString(TAG_POSITION_LATITUDE, "0.0");
    }

    public void savePositionLongitude(String positionLongitude) {
        editor.putString(TAG_POSITION_LONGITUDE, positionLongitude);
        editor.commit();

    }

    public String getSavedPositionLongitude() {

        return sharedPreferences.getString(TAG_POSITION_LONGITUDE, "0.0");
    }

}
