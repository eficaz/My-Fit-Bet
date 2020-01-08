package com.eficaz_fitbet_android.fitbet.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.fragments.DataParser;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.network.Constant.DRAW_MAP_BASE_URL;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DISTANCE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.END_LOCATION;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.END_address;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.LEGS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.ROUTES;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.START_LOCATION;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.START_address;


public class MapRedirectDetailedActivity extends BaseActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    int AUTOCOMPLETE_REQUEST_CODE = 1;

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

    Double lat,log;
    String distance_draw="0";
    Bundle bundle;
    String winerLat="",winerLog="";
    String startlongitude="",endlongitude="",startlatitude="",endlatitude="";
    Polyline polyline;
    String end_address=null,start_address = null,dis="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_redirect_detaiuld_by_loction);
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        ButterKnife.bind(this);
        bundle =  getIntent().getExtras();
        winerLat=bundle.getString(Contents.POSITION_LATITUDE);
        winerLog=bundle.getString(Contents.POSITION_LONGITUDE);
        startlongitude=bundle.getString(Contents.MYBETS_startlongitude);
        endlongitude=bundle.getString(Contents.MYBETS_endlongitude);
        startlatitude=bundle.getString(Contents.MYBETS_startlatitude);
        endlatitude=bundle.getString(Contents.MYBETS_endlatitude);
        mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);
        if(!startlongitude.equals("") && !endlongitude.equals("")&& !startlatitude.equals("")&& !endlatitude.equals("")){
            String origin=startlatitude+","+startlongitude;
            String dest = endlatitude+","+endlongitude;
            drawRoute1(origin,dest);
        }
        if(!winerLat.equals("") && !winerLog.equals("")){
            String latitudeLongitude = getLocationFromNetwork();
            String[] Lat = latitudeLongitude.split(",");
            lat= Double.valueOf(Lat[0]);
            log= Double.valueOf(Lat[1]);
            //mMapView = (MapView)findViewById(R.id.map);
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    mMap = mMap;
                    // For showing a move to my location button
                    mMap.setMyLocationEnabled(true);
                    // For dropping a marker at a point on the Map
                    LatLng sydney = new LatLng(Double.parseDouble(winerLat), Double.parseDouble(winerLog));
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(17).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void drawRoute1(String origin, String dest){
        Call<ResponseBody> call = RetroClient.getClient(DRAW_MAP_BASE_URL).create(RetroInterface.class).MapDetails(origin,dest,"driving",getString(R.string.google_maps_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    ParserTask1 parserTask = new ParserTask1();
                    parserTask.execute(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
    private class ParserTask1 extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());
                final JSONObject jsonObject_routes = new JSONObject(jsonData[0].toString());
                String data_routes = jsonObject_routes.getString(ROUTES);
                JSONArray yukilanarray =  new JSONArray(data_routes);
                String legs = null;
                for(int i=0;i<yukilanarray.length();i++)
                {
                    JSONObject jb1 = yukilanarray.getJSONObject(i);
                    legs = jb1.getString(LEGS);
                }
                JSONArray legsarray =  new JSONArray(legs);
                for(int i=0;i<legsarray.length();i++)
                {
                    JSONObject jb1 = legsarray.getJSONObject(i);
                    String data_start_location= jb1.getString(START_LOCATION);
                    String data_end_location = jb1.getString(END_LOCATION);
                    String distance1=jb1.getString(DISTANCE);
                    final JSONObject jsonObject_distance = new JSONObject(distance1);
                    distance_draw=jsonObject_distance.getString("text");
                    end_address=jb1.getString(END_address);
                    start_address =jb1.getString(START_address);
                }
            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            distance.setText(distance_draw);
            startpoint.setText(start_address);
            endpoint.setText(end_address);
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("latitude"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                final PolylineOptions finalLineOptions = lineOptions;
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap1) {
                        mMap = mMap1;
                        mMap.setMyLocationEnabled(true);
                        LatLng sydney = new LatLng(Double.parseDouble(startlatitude), Double.parseDouble(startlongitude));
                        mMap.addPolyline(finalLineOptions);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(startlatitude), Double.parseDouble(startlongitude))).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(endlatitude), Double.parseDouble(endlongitude))).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)));
                        polyline = mMap.addPolyline(new PolylineOptions());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        //mMap.setMyLocationEnabled(false);
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                        final GoogleMap finalMMap = mMap;
                    }
                });
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
