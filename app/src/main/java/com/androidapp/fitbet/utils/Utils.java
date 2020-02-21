package com.androidapp.fitbet.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.service.GPSTracker;
import com.google.android.gms.location.LocationRequest;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class Utils {

    public static boolean permissionStatus;
    public static boolean permissionDeclineStatus;
    public static String BetScreenName = "Bet";

    public static void showCustomToastMsg(Context mContext, int msg) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        TextView textAlrt = layout.findViewById(R.id.tv_message);
        textAlrt.setText(msg);
        Toast customtoast = new Toast(mContext);
        customtoast.setView(layout);
        customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        customtoast.setDuration(Toast.LENGTH_SHORT);
        customtoast.show();
    }

    public static void showCustomToastMsg(Context mContext, String msg) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        TextView textAlrt = layout.findViewById(R.id.tv_message);
        textAlrt.setText(msg);
        Toast customtoast = new Toast(mContext);
        customtoast.setView(layout);
        customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        customtoast.setDuration(Toast.LENGTH_SHORT);
        customtoast.show();
    }


    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.parseDouble(twoDForm.format(d));
    }

    public static boolean isValidEmail(String target) {
        if (target.equals("")) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isConnectedToInternet(Context _context) {
        return isReallyConnectedToInternet(_context);
    }

    public static boolean isReallyConnectedToInternet(Context _context) {
        if (_context != null) {
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
        }
        return false;
    }


    public static String[] getAddressFromGPSLocation(Context context, Double lat, Double lang) {
        Geocoder geocoder;
        List<Address> address;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            String address0 = "";
            address = geocoder.getFromLocation(lat, lang, 3); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            for (int i = 0; i < address.get(0).getMaxAddressLineIndex(); i++) {
                address0 = address0 + " " + address.get(0).getAddressLine(i); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            }
            String city = address.get(0).getLocality();
            String state = address.get(0).getAdminArea();
            String postalCode = address.get(0).getPostalCode();
            String city1 = address.get(0).getLocality();
            Address returnedAddress = address.get(0);
            StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }


            return new String[]{address0, address.get(0).getLocality(), address.get(0).getAdminArea(), address.get(0).getCountryName(), strReturnedAddress.toString()};
        } catch (Exception e) {
            return new String[]{};
        }
    }


    @SuppressLint("MissingPermission")
    public static String getLocationFromNetwork() {
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


    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";


    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }


    public static LocationRequest getLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


}
