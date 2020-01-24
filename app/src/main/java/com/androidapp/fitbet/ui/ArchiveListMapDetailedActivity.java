package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.polyline.DirectionFinder;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.network.Constant.DRAW_MAP_BASE_URL;
import static com.androidapp.fitbet.polyline.GoogleMapHelper.getDefaultPolyLines;


public class ArchiveListMapDetailedActivity extends BaseActivity  implements OnMapReadyCallback {

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
    String winnerPositionLat , winnerPositionLog ,winnerStartLat,winnerStartLog,winnerRoute,winnerDistance;
    Double startLongitude=0.0, positionLongitude, startLatitude, positionLatitude,distanceInMeters;
    Polyline polyline;
    private String origin,destination;
    private MyDialog noInternetDialog;

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
        winnerPositionLat =bundle.getString(Contents.POSITION_LATITUDE);
        winnerPositionLog =bundle.getString(Contents.POSITION_LONGITUDE);
        winnerStartLat =bundle.getString(Contents.MYBETS_startlatitude);
        winnerStartLog =bundle.getString(Contents.MYBETS_startlongitude);
        winnerRoute=bundle.getString("winner route");
        winnerDistance=bundle.getString("distance");
        startLatitude=Double.parseDouble(winnerStartLat);
        startLongitude=Double.parseDouble(winnerStartLog);
        positionLatitude=Double.parseDouble(winnerPositionLat);
        positionLongitude=Double.parseDouble(winnerPositionLog);
        distanceInMeters=Double.parseDouble(winnerDistance);

        origin=winnerStartLat+","+winnerStartLog;
        destination=winnerPositionLat+","+winnerPositionLog;
        noInternetDialog = new MyDialog(this, null, getString(R.string.no_internet), getString(R.string.no_internet_message), getString(R.string.ok), "", true, "internet");
        mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(Utils.isConnectedToInternet(this)){
            setLocationDetails(origin,destination);
        }else noInternetDialog.show();

    }
    private void setLocationDetails(String origin, String dest){

        CustomProgress.getInstance().showProgress(ArchiveListMapDetailedActivity.this,"",false);

        Call<ResponseBody> call = RetroClient.getClient(DRAW_MAP_BASE_URL).create(RetroInterface.class).MapDetails(origin,dest,"driving",getString(R.string.google_maps_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    CustomProgress.getInstance().hideProgress();
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                   System.out.println("google driving == "+bodyString);
                    JSONObject jsonObject=new JSONObject(bodyString);
                    JSONArray routeArray = jsonObject.getJSONArray("routes");
                    JSONArray legsArray=routeArray.getJSONObject(0).getJSONArray("legs");
                    endpoint.setText(legsArray.getJSONObject(0).getString("end_address"));
                    startpoint.setText(legsArray.getJSONObject(0).getString("start_address"));
                    System.out.println(legsArray.getJSONObject(0).getString("start_address"));

                    distance.setText(formatNumber2Decimals(distanceInMeters)+"KM");
                    onMapReady(mMap);
new Handler().post(new Runnable() {
    @Override
    public void run() {
        drawPolyLines(winnerRoute);
    }
});



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void drawPolyLines(String userRoute) {


        List<LatLng> latLngList = new ArrayList<>();
        String[] splitRoutes = userRoute.split("fitbet");
        List<String> routeList = Arrays.asList(splitRoutes);
        ///////////////////////////////////////////////////////////////
     /*   String[] splitRoutes = userRoute.split("fitbet");
        List<String> routeList= new LinkedList<>(Arrays.asList(splitRoutes));
        System.out.println("routeList.size()"+routeList.size());
        Set<String> routeSets=new LinkedHashSet<>(routeList);
        routeList.clear();
        routeList.addAll(routeSets);*/
//////////////////////////////////////////////////////////////////////
       /* String[] splitRoutes = userRoute.split("fitbet");
        List<String> routeList = Arrays.asList(splitRoutes);
        System.out.println("routeList.size() map redir" + routeList.size());

        for (String route : routeList) {
            latLngList.clear();
            latLngList = DirectionFinder.decodePolyLine(route);
            System.out.println("Routes " + route);
            final List<LatLng> finalLatLngList = latLngList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PolylineOptions polylineOptions = getDefaultPolyLines(finalLatLngList);

                    polyline = mMap.addPolyline(polylineOptions);
                }
            });


        }*/





        for (String route : routeList) {
            latLngList.clear();
            latLngList = DirectionFinder.decodePolyLine(route);
            System.out.println("Routes " + route);
            final List<LatLng> finalLatLngList = latLngList;
       /*     if (polyline == null) {
                polylineOptions = getDefaultPolyLines(finalLatLngList);
            } else {

                if (polylineOptions != null) {
                    polylineOptions.addAll(finalLatLngList);
                }
            }
*/
final PolylineOptions polylineOptions=getDefaultPolyLines(finalLatLngList);

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    polyline = mMap.addPolyline(polylineOptions);

                }
            });


        }

       // zoomRoute(mMap, latLngList);

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(  startLatitude,   startLongitude), 18f));
    }

    private String formatNumber2Decimals(double number ){
        number=number/1000;


        return String.format(Locale.getDefault(), "%.2f", number) ;
    }
}
