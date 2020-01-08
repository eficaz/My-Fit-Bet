package com.eficaz_fitbet_android.fitbet.forceupdate;

import android.app.Application;

import androidx.annotation.NonNull;

import com.eficaz_fitbet_android.fitbet.BuildConfig;
import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class ReadyAndroidApp extends Application {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        //DeviceUtils.setWindowDimensions(this);
        initFirebaseRemoteConfig();
    }

    private void initFirebaseRemoteConfig() {
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // [END set_default_values]

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.showCustomToastMsg(getApplicationContext(), "Fetch Succeeded");
                            //CommonUtils.showToast(getApplicationContext(), "Fetch Succeeded");

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Utils.showCustomToastMsg(getApplicationContext(),"Fetch Failed");
                            //CommonUtils.showToast(getApplicationContext(), "Fetch Failed");
                        }
                        //displayWelcomeMessage();
                    }
                });
        // [END fetch_config_with_callback]
    }
}
