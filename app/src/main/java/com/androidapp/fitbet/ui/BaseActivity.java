package com.androidapp.fitbet.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.fitbet.customview.CountDownDialog;
import com.androidapp.fitbet.service.GPSTracker;

import com.androidapp.fitbet.utils.SLApplication;

public class BaseActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {

private IntentFilter filter=new IntentFilter("count_down");

private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
     System.out.println("Base activity onReceive");
    startCountDown(context);

    }
};

    private void startCountDown(Context context) {

new CountDownDialog(context);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
System.out.println("Base OnCreate");
        registerReceiver(mBroadcastReceiver,filter);

    }

    @Override
    protected void onStop() {
        System.out.println("Base OnStop");

        super.onStop();
    }

    @Override
    protected void onResume() {
        System.out.println("Base OnResume");
        registerReceiver(mBroadcastReceiver,filter);
        super.onResume();

    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void showKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editText, 0);
    }

    @SuppressLint("MissingPermission")
    public String getLocationFromNetwork() {
        GPSTracker gpsTracker = new GPSTracker(SLApplication.getContext());
        if (gpsTracker.gpsStatus()) {
            String Latitude = String.valueOf(gpsTracker.getLatitude());
            String Longitude = String.valueOf(gpsTracker.getLongitude());
            if (Latitude.equals("0.0") || (Longitude.equals("0.0"))) {
                // showToast((R.string.gps_msg));
            } else {
                return Latitude + "," + Longitude;
            }
        } else {
            LocationManager locationManager = (LocationManager) SLApplication.getContext().getSystemService(LOCATION_SERVICE);
            boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (network_enabled) {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null)
                    return location.getLatitude() + "," + location.getLongitude();
            }
        }
        return "0.0,0.0";
    }


}
