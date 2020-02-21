package com.androidapp.fitbet.map;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationMonitoringService extends Service {


    private static final String TAG = LocationMonitoringService.class.getSimpleName();

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mLocation;
    private LocationRequest locationRequest;


    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = Utils.getLocationRequest();
        createLocationCallback();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

        SLApplication.isServiceRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SLApplication.isServiceRunning = false;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createLocationCallback() {


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        onNewLocation(location);
                    }
                }
            }
        };
    }


    private void onNewLocation(Location location) {
        mLocation = location;
        if (mLocation != null) {
            Log.d(TAG, "== onNewLocation " + location.getLatitude() + "," + location.getLongitude());
            AppPreference.getPrefsHelper().savePref(Contents.FOR_START_BET_LAT, "" + location.getLatitude());
            AppPreference.getPrefsHelper().savePref(Contents.FOR_START_BET_LOG, "" + location.getLongitude());
            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}