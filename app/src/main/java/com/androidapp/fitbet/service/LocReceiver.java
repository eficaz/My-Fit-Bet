package com.androidapp.fitbet.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.androidapp.fitbet.interfaces.LocationReceiveListener;
import com.androidapp.fitbet.ui.fragments.LiveBetFragment;
import com.androidapp.fitbet.utils.SLApplication;

import java.util.LinkedList;
import java.util.List;


public class LocReceiver extends BroadcastReceiver {
    public static final String TAG = "LocReceiver";

    private LocationReceiveListener mLocationReceiveListener = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println(TAG + " receive loc receive");


        for (LocationReceiveListener listener : locationReceiveListeners) {

            System.out.println(TAG + "listener.getClass().getSimpleName()" + listener.getClass().getSimpleName());
            if (listener instanceof LiveBetFragment) {

                if (SLApplication.firstConnect) {
                    SLApplication.firstConnect = false;
                    listener.onLocationReceived(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lon", 0.0));

                }
            } else

                listener.onLocationReceived(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lon", 0.0));


        }


    }


    private static List<LocationReceiveListener> locationReceiveListeners = new LinkedList<>();

    public static void registerLocationReceiveListener(LocationReceiveListener locationReceiveListener) {
        locationReceiveListeners.add(locationReceiveListener);

    }

    public static void unregisterLocationReceiveListener(LocationReceiveListener locationReceiveListener) {

        locationReceiveListeners.remove(locationReceiveListener);

    }

}
