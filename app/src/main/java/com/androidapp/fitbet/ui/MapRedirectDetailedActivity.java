package com.androidapp.fitbet.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.polyline.DirectionFinder;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.androidapp.fitbet.polyline.GoogleMapHelper.getDefaultPolyLines;


public class MapRedirectDetailedActivity extends BaseActivity  implements OnMapReadyCallback {
    private GoogleMap mMap;


    @Bind(R.id.map)
    MapView mMapView;

    @Bind(R.id.btn_close)
    ImageView btn_close;

    @Bind(R.id.startpoint)
    TextView startpoint;

    @Bind(R.id.endpoint)
    TextView endpoint;
    @Bind(R.id.distance)
    TextView distance;

    Bundle bundle;
    String originalPositionLat , originalPositionLog ,originalStartLat,originalStartLog,originalRoute,originalDistance,startAddress,endAddress;
    Double startLongitude=0.0, positionLongitude, startLatitude, positionLatitude,distanceInMeters;
    private Polyline polyline;


    private IntentFilter filter=new IntentFilter("count_down");
    private boolean firstConnect=true;
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if (firstConnect) {
                    firstConnect = false;

                    String message = intent.getStringExtra("message");
                    onMessageReceived(message);

                }
            }else{
                firstConnect=true;
            }

        }
    };

    @Override
    public void onMessageReceived(String message) {

        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_redirect_detaiuld_by_loction);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        bundle =  getIntent().getExtras();
        originalPositionLat =bundle.getString(Contents.POSITION_LATITUDE);
        originalPositionLog =bundle.getString(Contents.POSITION_LONGITUDE);
        originalStartLat =bundle.getString(Contents.MYBETS_startlatitude);
        originalStartLog =bundle.getString(Contents.MYBETS_startlongitude);
        originalRoute=bundle.getString("original route");
        originalDistance=bundle.getString("original distance");
        startAddress=bundle.getString("start address");
        endAddress=bundle.getString("end address");
        startLatitude=Double.parseDouble(originalStartLat);
        startLongitude=Double.parseDouble(originalStartLog);
        positionLatitude=Double.parseDouble(originalPositionLat);
        positionLongitude=Double.parseDouble(originalPositionLog);
        distanceInMeters=Double.parseDouble(originalDistance);

        mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startpoint.setText(startAddress);
        endpoint.setText(endAddress);
        distance.setText(formatNumber2Decimals(distanceInMeters));
new Handler().post(new Runnable() {
    @Override
    public void run() {
        drawPolyLines(originalRoute);
    }
});


    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }


    private void drawPolyLines(String userRoute) {

   String r= StringEscapeUtils.unescapeJava(userRoute);
   System.out.println("map route "+r);
        List<LatLng> latLngList = DirectionFinder.decodePolyLine(r);

            PolylineOptions polylineOptions = getDefaultPolyLines(latLngList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    polyline = mMap.addPolyline(polylineOptions);
                }
            });


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(positionLatitude,positionLongitude),14f));
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }


    @Override
    public void onResume() {
        super.onResume();
LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap!=null)
            mMap.setMyLocationEnabled(true);
        if(startLatitude!=0.0) {
            googleMap.addMarker(new MarkerOptions().title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)).position(new LatLng(startLatitude, startLongitude)));
            googleMap.addMarker(new MarkerOptions().title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)).position(new LatLng(positionLatitude, positionLongitude)));
        }

    }

    private String formatNumber2Decimals(double number ){
        number=number/1000;


        return String.format(Locale.getDefault(), "%.2f", number) ;
    }

}
