package com.eficaz_fitbet_android.fitbet.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.eficaz_fitbet_android.fitbet.ui.fragments.LiveBetFragment;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.SLApplication;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocService extends Service {
    private Handler mHandler;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;


    public void setLocation(Location location) {
        this.mLocation = location;
    }

    private Location mLocation=null;
    public Location getLocation() {
        return mLocation;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest=getLocRequest();

    }

    private LocationRequest getLocRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
private LocationCallback locationCallback=new LocationCallback(){
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.d("LocService", "onLocationResult: "+locationResult.getLastLocation().getLatitude()+","+locationResult.getLastLocation().getLongitude());
        setLocation(locationResult.getLastLocation());

        Intent broadcastIntent=new Intent(LocService.this, LocReceiver.class);
        broadcastIntent.setAction("location_update");
        broadcastIntent.putExtra("lat",locationResult.getLastLocation().getLatitude());
        broadcastIntent.putExtra("lon",+locationResult.getLastLocation().getLongitude());
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
    }
};
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SLApplication.isServiceRunning=true;
        System.out.println("Starting loc service");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,locationCallback, Looper.myLooper());

            }
        },500);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy service");
        SLApplication.isServiceRunning=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        stopSelf();
        SLApplication.isServiceRunning=false;
    }



}
