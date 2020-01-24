package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    Polyline polyline;


    @Override
    protected void onMessageReceived(String message) {
        super.onMessageReceived(message);
        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_redirect_detaiuld_by_loction);
        ButterKnife.bind(this);
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

        drawPolyLines(originalRoute);

    }


    private void drawPolyLines(String userRoute) {


        List<LatLng> latLngList = new ArrayList<>();

        String[] splitRoutes = userRoute.split("fitbet");
        List<String> routeList= Arrays.asList(splitRoutes);
        System.out.println("routeList.size() map redir"+routeList.size());

        for (String route:routeList) {
            latLngList.clear();
            latLngList = DirectionFinder.decodePolyLine(route);
            System.out.println("Routes "+route);
            final List<LatLng> finalLatLngList = latLngList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PolylineOptions polylineOptions = getDefaultPolyLines(finalLatLngList);

                    polyline = mMap.addPolyline(polylineOptions);
                }
            });



        }

        zoomRoute(mMap,latLngList);
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
        //mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mMapView.onDestroy();
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
