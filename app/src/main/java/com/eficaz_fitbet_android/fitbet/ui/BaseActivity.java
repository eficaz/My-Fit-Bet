package com.eficaz_fitbet_android.fitbet.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.eficaz_fitbet_android.fitbet.service.GPSTracker;

import com.eficaz_fitbet_android.fitbet.utils.SLApplication;

public class BaseActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
