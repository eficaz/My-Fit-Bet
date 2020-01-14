package com.androidapp.fitbet.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.androidapp.fitbet.interfaces.LocationReceiveListener;

import java.util.LinkedList;
import java.util.List;

public class LocReceiver extends BroadcastReceiver {
    private LocationReceiveListener mLocationReceiveListener =null;
    @Override
    public void onReceive(Context context, Intent intent) {

System.out.println(" recieve loc receive" );

        for(LocationReceiveListener listener: locationReceiveListeners)
            if(listener != null)
                listener.onLocationReceived(intent.getDoubleExtra("lat",0.0),intent.getDoubleExtra("lon",0.0));



    }




    private static List<LocationReceiveListener> locationReceiveListeners = new LinkedList<>();

    public static void registerLocationReceiveListener(LocationReceiveListener locationReceiveListener){
        locationReceiveListeners.add(locationReceiveListener);
    }

    public static void unregisterLocationReceiveListener(LocationReceiveListener   locationReceiveListener){
        locationReceiveListeners.remove(locationReceiveListener);
    }

}
