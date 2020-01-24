package com.androidapp.fitbet.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.forceupdate.ForceUpdateChecker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;


public class SLApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks{
    public static boolean isServiceRunning=false;
    public static boolean isCountDownRunning=false;
    public static SLApplication mInstance;
    public static boolean isBetCreatedOrEdited;

    private Activity activeActivity;
    public static FusedLocationProviderClient fusedLocationProviderClient=null;
    public static LocationCallback locationCallback=null;



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        new AppPreference(this);

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, R.string.ver_no);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL, "https://play.google.com/store/apps/details?id=com.sembozdemir.renstagram");
        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

    }
    public static Context getContext() {
        if (mInstance == null)
            mInstance = new SLApplication();
        return mInstance;
    }
    public static SLApplication getInstance() {
        if (mInstance == null)
            mInstance = new SLApplication();
        return mInstance;
    }
    public static Context getAppContext() {
        if (mInstance == null)
            mInstance = new SLApplication();
        return mInstance.getApplicationContext();
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }
    public Activity getActiveActivity() {
        return activeActivity;
    }
    @Override
    public void onActivityResumed(Activity activity) {

        activeActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(fusedLocationProviderClient!=null&& locationCallback!=null)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }
public static void removeLocationUpdates(){
    if(fusedLocationProviderClient!=null&& locationCallback!=null)
    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
}

}
