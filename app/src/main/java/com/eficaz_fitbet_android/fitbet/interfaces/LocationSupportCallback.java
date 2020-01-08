/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.eficaz_fitbet_android.fitbet.interfaces;

import android.content.Intent;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;


public interface LocationSupportCallback extends GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    int PRIORITY_BALANCED_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    int PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    int PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER;
    int PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER;

    GoogleApiClient getGoogleApiClient();

    LocationRequest getLocationRequest();

    LocationManager getLocationService();

    void setFastestInterval(long fastInterval);

    void setSmallestDisplacement(long distance);

    void setInterval(long interval);

    void setPriority(int priority);

    void stopUpdates();

    void startUpdates();

    boolean getGooglePlayServiceAvailable();

    void onPermissionRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    void onPermissionActivityResult(int requestCode, int resultCode, Intent data);

    void addUpdateListner(LocationUpdateCallback mListner);
}

