package com.androidapp.fitbet.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.androidapp.fitbet.polyline.DirectionFinder;
import com.androidapp.fitbet.polyline.DirectionFinderListener;
import com.androidapp.fitbet.polyline.Route;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class LocService extends Service implements DirectionFinderListener {
    private Handler mHandler;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private double latitude, longitude, distance = 0.0, positionLatitude, positionLongitude;
    private String origin, destination;
    AppPreference appPreference;

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    private Location mLocation = null;

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = getLocRequest();
        appPreference = AppPreference.getPrefsHelper();
    }

    private LocationRequest getLocRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(0.1f);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    private int i = 1, j = 2;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d("LocService", "onLocationResult: " + locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());
            setLocation(locationResult.getLastLocation());
            latitude = locationResult.getLastLocation().getLatitude();
            longitude = locationResult.getLastLocation().getLongitude();


            if (appPreference != null)
                if (appPreference.getPref(Contents.BET_START_STATUS, "").equals("true")) {

                    i++;
                    j++;

                    if (i == 2) {

                        i = 0;


                        appPreference.savePref(Contents.USER_start_latitude, String.valueOf(latitude));
                        appPreference.savePref(Contents.USER_start_latitude, String.valueOf(latitude));


                        destination = "" + latitude + "," + longitude;

                        System.out.println("Origin and destination " + appPreference.getSavedOrigin() + " , " + destination);

                        if (!appPreference.getSavedOrigin().equals("")) {


                            fetchDirections(appPreference.getSavedOrigin(), destination);

                        }
                        appPreference.saveOrigin(destination);


                    }
                    if (j == 3) {
                        j = 0;

                        if (!appPreference.getPositionLatitude().equals("0.0")) {
                            double distance = Double.parseDouble(appPreference.getSavedDistance());
                            distance = distance + getDistanceL(Double.parseDouble(appPreference.getPositionLatitude()), Double.parseDouble(appPreference.getSavedPositionLongitude()), latitude, longitude);

                            appPreference.saveDistance(String.valueOf(distance));
                        }

                        appPreference.savePositionLatitude(String.valueOf(latitude));
                        appPreference.savePositionLongitude(String.valueOf(longitude));
                        SLApplication.firstUpdateConnect = true;
                        Intent serviceIntent = new Intent(LocService.this, BetUpdateService.class);
                        serviceIntent.putExtra("positionLatitude", latitude);
                        serviceIntent.putExtra("positionLongitude", longitude);
                        BetUpdateService.enqueueWork(LocService.this, serviceIntent);
                    }


                }

            Intent broadcastIntent = new Intent(LocService.this, LocReceiver.class);
            broadcastIntent.setAction("location_update");
            broadcastIntent.putExtra("lat", locationResult.getLastLocation().getLatitude());
            broadcastIntent.putExtra("lon", +locationResult.getLastLocation().getLongitude());
            SLApplication.firstConnect = true;
            sendBroadcast(broadcastIntent);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };


    private int getDistanceL(double startLat, double startLon, double positionLat, double positionLon) {

        Location startLocation = new Location("start");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLon);
        Location positionLocation = new Location("position");
        positionLocation.setLatitude(positionLat);
        positionLocation.setLongitude(positionLon);

        return (int) startLocation.distanceTo(positionLocation);
    }

    private void fetchDirections(String origin, String destination) {

        System.out.println("fetchDirections " + "origin " + origin + " , " + "destination " + destination);
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SLApplication.isServiceRunning = true;
        System.out.println("Starting loc service");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());

            }
        }, 500);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFusedLocationProviderClient != null)
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        System.out.println("Destroy Loc service");
        SLApplication.isServiceRunning = false;
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
        SLApplication.isServiceRunning = false;
        stopSelf();

    }


    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

        appPreference.setLatLongList(route);

        String r = StringEscapeUtils.escapeJava(route.get(0).pointString);
        if (appPreference.getSavedUserRoute().equals("")) {

            appPreference.saveUserRoute(r);
        } else
            appPreference.saveUserRoute(appPreference.getSavedUserRoute() + "fitbet" + r);
    }
}
