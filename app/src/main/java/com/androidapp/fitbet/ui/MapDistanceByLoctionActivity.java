package com.androidapp.fitbet.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.interfaces.LocationReceiveListener;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.service.LocReceiver;
import com.androidapp.fitbet.service.LocService;
import com.androidapp.fitbet.ui.fragments.DataParser;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.network.Constant.DRAW_MAP_BASE_URL;
import static com.androidapp.fitbet.network.Constant.PLACE_BASE_URL;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.END_LOCATION;
import static com.androidapp.fitbet.utils.Contents.GEOMETRY;
import static com.androidapp.fitbet.utils.Contents.LAT;
import static com.androidapp.fitbet.utils.Contents.LEGS;
import static com.androidapp.fitbet.utils.Contents.LNG;
import static com.androidapp.fitbet.utils.Contents.LOCATION;
import static com.androidapp.fitbet.utils.Contents.OVERVIEW_POLYLINE;
import static com.androidapp.fitbet.utils.Contents.POINTS;
import static com.androidapp.fitbet.utils.Contents.RESULT;
import static com.androidapp.fitbet.utils.Contents.ROUTES;
import static com.androidapp.fitbet.utils.Contents.START_LOCATION;
import static com.androidapp.fitbet.utils.SLApplication.getContext;
import static com.androidapp.fitbet.utils.Utils.getAddressFromGPSLocation;


public class MapDistanceByLoctionActivity extends BaseActivity  implements OnMapReadyCallback, LocationReceiveListener {

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

    @Bind(R.id.set)
    TextView set;

    @Bind(R.id.setRow)
    TableRow setRow;

    @Bind(R.id.row_startpoint)
    TableRow row_startpoint;

    @Bind(R.id.row_endpoint)
    TableRow row_endpoint;

    @Bind(R.id.close_row)
    TableRow close_row;


    Double latitude =0.0, longitude =0.0;

    Double startALat=0.0;
    Double startALog=0.0;

    Double endALat=0.0;
    Double endALog=0.0;

    Double start_loc_lat=0.0;
    Double start_loc_log=0.0;

    int kmInDec=0;

    Marker startMarker;
    Marker entMarker;
    Polyline polyline;
    int startOrend=0;
    String distance_draw="0";
    Double startDrowLat=0.0,startDrowLog=0.0,endDrowLat=0.0,endDrowLog=0.0;

    String start_point,end_point;

    boolean km_m=false;
    ArrayList markerPoints= new ArrayList();

    public static final int PATTERN_DASH_LENGTH_PX = 20;
    public static final int PATTERN_GAP_LENGTH_PX = 4;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    PolylineOptions lineOptions = null;
    boolean curentMarker=true;


    String overview_polyline;
private Intent serviceIntent;
private LocationManager mLocationManager;
private LocationReceiveListener locationReceiveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_distance_by_loction);
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        ButterKnife.bind(this);
        distance.setText("");
        locationReceiveListener=this;
        LocReceiver.registerLocationReceiveListener(locationReceiveListener);
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        serviceIntent = new Intent(this, LocService.class);
        if (!SLApplication.isServiceRunning)
            startLocationService(serviceIntent);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapDistanceByLoctionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //mMapView = (MapView)findViewById(R.id.map);


        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("true")){
            curentMarker=true;
            CustomProgress.getInstance().showProgress(MapDistanceByLoctionActivity.this, "", false);
        }else{
            curentMarker=true;
            CustomProgress.getInstance().showProgress(MapDistanceByLoctionActivity.this, "", false);
        }


           /* mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    mMap = mMap;
                    LatLng sydney = new LatLng(latitude, longitude);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(18).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    startMarker=mMap.addMarker(new MarkerOptions().position(sydney).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                    String abc[]=getAddressFromGPSLocation(MapDistanceByLoctionActivity.this,latitude,longitude);
                    startpoint.setVisibility(View.VISIBLE);
                    startpoint.setText(abc[4]);
                    start_point=abc[4];
                    startALat= Double.valueOf(latitude);
                    startALog= Double.valueOf(longitude);
                }
            });*/






        close_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("true")){
                    Intent i = new Intent(MapDistanceByLoctionActivity.this, BetCreationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key1","value1");
                    bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
                    bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
                    bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
                    bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
                    bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    bundle.putString(Contents.START_Address, String.valueOf(start_point));
                    bundle.putString(Contents.END_Address, String.valueOf(end_point));
                    bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("false")){
                    Intent i = new Intent(MapDistanceByLoctionActivity.this, EditBetCreationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key1","value1");
                    bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
                    bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
                    bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
                    bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
                    bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    bundle.putString(Contents.START_Address, String.valueOf(start_point));
                    bundle.putString(Contents.bet_edit_details, "");
                    bundle.putString(Contents.END_Address, String.valueOf(end_point));
                    bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }



            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("true")){
                    Intent i = new Intent(MapDistanceByLoctionActivity.this, BetCreationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key1","value1");
                    bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
                    bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
                    bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
                    bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
                    bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    bundle.putString(Contents.START_Address, String.valueOf(start_point));
                    bundle.putString(Contents.END_Address, String.valueOf(end_point));
                    bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("false")){
                    Intent i = new Intent(MapDistanceByLoctionActivity.this, EditBetCreationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key1","value1");
                    bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
                    bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
                    bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
                    bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
                    bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    bundle.putString(Contents.START_Address, String.valueOf(start_point));
                    bundle.putString(Contents.bet_edit_details, "");
                    bundle.putString(Contents.END_Address, String.valueOf(end_point));
                    bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            }
        });
        startpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrend=0;
                autocompletePlace();

            }
        });
        row_startpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrend=0;
                autocompletePlace();
            }
        });
        row_endpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrend=1;
                autocompletePlace();
                setRow.setVisibility(View.VISIBLE);
            }
        });
        endpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrend=1;
                autocompletePlace();
                setRow.setVisibility(View.VISIBLE);

            }
        });
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location startPoint=new Location("locationA");
                startPoint.setLatitude(startALat);
                startPoint.setLongitude(startALog);
                Location endPoint=new Location("locationA");
                endPoint.setLatitude(endALat);
                endPoint.setLongitude(endALog);
                double distance=startPoint.distanceTo(endPoint);
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("true")){
                    Intent i = new Intent(MapDistanceByLoctionActivity.this, BetCreationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key1","value1");
                    bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
                    bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
                    bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
                    bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
                    bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    bundle.putString(Contents.START_Address, String.valueOf(start_point));
                    bundle.putString(Contents.END_Address, String.valueOf(end_point));
                    bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }else  if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("false")){
                    Intent i = new Intent(MapDistanceByLoctionActivity.this, EditBetCreationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key1","value1");
                    bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
                    bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
                    bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
                    bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
                    bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    bundle.putString(Contents.START_Address, String.valueOf(start_point));
                    bundle.putString(Contents.bet_edit_details, "");
                    bundle.putString(Contents.END_Address, String.valueOf(end_point));
                    bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }



            }
        });

    }

    private void startLocationService(Intent intent) {
if(!SLApplication.isServiceRunning) {
    startService(intent);
    SLApplication.isServiceRunning = true;
}
    }

    private void stopLocationService(Intent intent) {
        if(SLApplication.isServiceRunning) {
            stopService(intent);
            SLApplication.isServiceRunning = false;
        }
    }
    @Override
    public void onStop() {

        super.onStop();
        stopLocationService(serviceIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        stopLocationService(serviceIntent);

        if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("true")){
            Intent i = new Intent(MapDistanceByLoctionActivity.this, BetCreationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("key1","value1");
            bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
            bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
            bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
            bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
            bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
            bundle.putString(Contents.START_Address, String.valueOf(start_point));
            bundle.putString(Contents.END_Address, String.valueOf(end_point));
            bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }else  if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("false")){
            Intent i = new Intent(MapDistanceByLoctionActivity.this, EditBetCreationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("key1","value1");
            bundle.putString(Contents.pass_startlatitude, String.valueOf(startALat));
            bundle.putString(Contents.pass_startlongitude, String.valueOf(startALog));
            bundle.putString(Contents.pass_endlatitude, String.valueOf(endALat));
            bundle.putString(Contents.pass_endlongitude, String.valueOf(endALog));
            bundle.putString(Contents.MYBETS_distance, String.valueOf(distance_draw));
            bundle.putString(Contents.START_Address, String.valueOf(start_point));
            bundle.putString(Contents.bet_edit_details, "");
            bundle.putString(Contents.END_Address, String.valueOf(end_point));
            bundle.putString(Contents.OVERVIEW_POLYLINE, String.valueOf(overview_polyline));
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }
    }



    @Override
    public void onLocationReceived(Double lat, Double lon) {
        latitude =lat;
        longitude=lon;
        if(lat!=null){
            if(curentMarker==true){
                curentMarker=false;
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        mMap = mMap;
                        LatLng latLng = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(18).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        startMarker=mMap.addMarker(new MarkerOptions().position(latLng).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                        String abc[]=getAddressFromGPSLocation(MapDistanceByLoctionActivity.this,latitude, longitude);
                        startpoint.setVisibility(View.VISIBLE);
                        startpoint.setText(abc[4]);
                        start_point=abc[4];
                        startALat= Double.valueOf(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,""));
                        startALog= Double.valueOf(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""));
                    }
                });
                CustomProgress.getInstance().hideProgress();
            }
        }

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            startpoint.setVisibility(View.VISIBLE);
            startpoint.setText(locationAddress);
            start_point=locationAddress;
        }
    }
    private void drawMapViewRoot(Double startALat, Double startALog, Double endALat, Double endALog) {
        if(!(startALat ==0.0) ||!(startALog==0.0)||!(endALat==0.0)||!(endALog==00)){
            int Radius = 6371;
            double lat1 = startALat;
            double lat2 = endALat;
            double lon1 = startALog;
            double lon2 = endALog;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;
            double km = valueResult / 1;
            DecimalFormat newFormat = new DecimalFormat("####");
            kmInDec = Integer.valueOf(newFormat.format(km));
            double meter = valueResult % 1000;
            int meterInDec = Integer.valueOf(newFormat.format(meter));
            set.setVisibility(View.VISIBLE);
            distance.setVisibility(View.VISIBLE);
            String origin=startALat+","+startALog;
            String dest=endALat+","+endALog;
            drawRoute(origin,dest);
        }else{
            //distance.setText("0" +" km");
        }
    }
    private void drawRoute(String origin, String dest){
        Call<ResponseBody> call = RetroClient.getClient(DRAW_MAP_BASE_URL).create(RetroInterface.class).MapDetails(origin,dest,"driving",getString(R.string.google_maps_key));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    ParserTask parserTask = new ParserTask();
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
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
   System.out.println("Parser task 1 = "+jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask2", parser.toString());
                routes = parser.parse(jObject);
                Log.d("ParserTask3","Executing routes");
                Log.d("ParserTask4",routes.toString());
                final JSONObject jsonObject_routes = new JSONObject(jsonData[0].toString());
                String data_routes = jsonObject_routes.getString(ROUTES);
                JSONArray yukilanarray =  new JSONArray(data_routes);
                String legs = null;
                for(int i=0;i<yukilanarray.length();i++)
                {
                    JSONObject jb1 = yukilanarray.getJSONObject(i);
                    legs = jb1.getString(LEGS);
                    String overview_polyline_routes_data  = jb1.getString(OVERVIEW_POLYLINE);
                    final JSONObject overview_polyline_routes = new JSONObject(overview_polyline_routes_data);
                    overview_polyline = overview_polyline_routes.getString(POINTS);
                }
                JSONArray legsarray =  new JSONArray(legs);
                for(int i=0;i<legsarray.length();i++)
                {
                    JSONObject jb1 = legsarray.getJSONObject(i);
                    String data_start_location= jb1.getString(START_LOCATION);
                    String data_end_location = jb1.getString(END_LOCATION);
                    String distance=jb1.getString(DISTANCE);
                    final JSONObject jsonObject_distance = new JSONObject(distance);
                    //String dis=jsonObject_distance.getString("text").replace("km","").replace("m","");
                    String str = jsonObject_distance.getString("text").substring(jsonObject_distance.getString("text").length() - 2);
                    double lave_bet_dis;

                    if(str.equals("km")){
                        distance_draw= String.valueOf(Double.parseDouble(jsonObject_distance.getString("text").replace("km","").trim())*1000);
                        km_m=true;
                    }else{
                        distance_draw= String.valueOf(Double.parseDouble(jsonObject_distance.getString("text").replace("m","").trim()));
                        km_m=false;
                    }
                    final JSONObject jsonObject_start_location = new JSONObject(data_start_location);
                    final JSONObject jsonObject_data_end_location = new JSONObject(data_end_location);
                     startDrowLat= Double.valueOf(jsonObject_start_location.getString("lat"));
                     startDrowLog= Double.valueOf(jsonObject_start_location.getString("lng"));
                     endDrowLat= Double.valueOf(jsonObject_data_end_location.getString("lat"));
                     endDrowLog= Double.valueOf(jsonObject_data_end_location.getString("lng"));
                     startALat=startDrowLat;
                     startALog=startDrowLog;
                     endALat=endDrowLat;
                     endALog=endDrowLog;
                }
            } catch (Exception e) {
                Log.d("ParserTask5",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions = new PolylineOptions();
                lineOptions.color(ContextCompat.getColor(getContext(), R.color.black));
                lineOptions.addAll(points);
                lineOptions.width(10);
           /*     lineOptions.pattern(PATTERN_POLYGON_ALPHA);*/
                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap1) {
                        mMap = mMap1;
                        if(km_m==true){
                            distance.setText((Double.parseDouble(distance_draw)/1000) +"Km");
                        }else{
                            distance.setText((Double.parseDouble(distance_draw)/1000) +"Km");
                        }

                      /*  double dis= Double.parseDouble(distance_draw/)/1000;
                        distance.setText(""+dis+"km");*/
                        startMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(startDrowLat, startDrowLog)).title("Start").snippet("Start").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                        entMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(endDrowLat, endDrowLog)).title("End").snippet("End").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)));
                        startMarker.setDraggable(true);
                        entMarker.setDraggable(true);

                        LatLng sydney = new LatLng(endALat, endALog);
                        mMap.addPolyline(lineOptions);
                        polyline = mMap.addPolyline(new PolylineOptions());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mMap.setMyLocationEnabled(false);
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                        final GoogleMap finalMMap = mMap;
                        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {
                            }
                            @Override
                            public void onMarkerDrag(Marker marker) {
                            }
                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                if(marker.getId().equals(startMarker.getId())){
                                    mMap.clear();
                                    startMarker.setPosition(marker.getPosition());
                                    setRow.setVisibility(View.VISIBLE);
                                    drawMapViewRoot(marker.getPosition().latitude,marker.getPosition().longitude,endDrowLat,endDrowLog);
                                    Geocoder geocoder = new Geocoder(MapDistanceByLoctionActivity.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                                        Address obj = addresses.get(0);
                                        String add = obj.getAddressLine(0);
                                        add = add + "\n" + obj.getCountryName();
                                        add = add + "\n" + obj.getCountryCode();
                                        add = add + "\n" + obj.getAdminArea();
                                        add = add + "\n" + obj.getPostalCode();
                                        add = add + "\n" + obj.getSubAdminArea();
                                        add = add + "\n" + obj.getLocality();
                                        add = add + "\n" + obj.getSubThoroughfare();
                                        start_point=add;
                                        startpoint.setText(add);
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        //Toast.makeText(MapDistanceByLoctionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }else if(marker.getId().equals(entMarker.getId())){
                                    mMap.clear();
                                    entMarker.setPosition(marker.getPosition());
                                    setRow.setVisibility(View.VISIBLE);
                                    drawMapViewRoot(startDrowLat,startDrowLog,marker.getPosition().latitude,marker.getPosition().longitude);
                                    Geocoder geocoder = new Geocoder(MapDistanceByLoctionActivity.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                                        Address obj = addresses.get(0);
                                        String add1 = obj.getAddressLine(0);
                                        add1 = add1 + "\n" + obj.getCountryName();
                                        add1 = add1 + "\n" + obj.getCountryCode();
                                        add1 = add1 + "\n" + obj.getAdminArea();
                                        add1 = add1 + "\n" + obj.getPostalCode();
                                        add1 = add1 + "\n" + obj.getSubAdminArea();
                                        add1 = add1 + "\n" + obj.getLocality();
                                        add1 = add1 + "\n" + obj.getSubThoroughfare();
                                        endpoint.setText(add1);
                                        end_point=add1;
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        //Toast.makeText(MapDistanceByLoctionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    private void autocompletePlace() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = Autocomplete.getPlaceFromIntent(data);
                if (place != null) {
                    String placeId = place.getId();
                    Call<ResponseBody> call = RetroClient.getClient(PLACE_BASE_URL).create(RetroInterface.class).PlaceDetails(placeId,getString(R.string.google_maps_key));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String bodyString = new String(response.body().bytes(), "UTF-8");
                                try {
                                    final JSONObject jsonObject = new JSONObject(bodyString);
                                    String data = jsonObject.getString(RESULT);
                                    final JSONObject jsonObject1 = new JSONObject(data);
                                    String geometry = jsonObject1.getString(GEOMETRY);
                                    final JSONObject jsonObject2 = new JSONObject(geometry);
                                    String location = jsonObject2.getString(LOCATION);
                                    final JSONObject jsonObject3 = new JSONObject(location);
                                    String lat = jsonObject3.getString(LAT);
                                    String lng = jsonObject3.getString(LNG);
                                    if (startOrend==0) {
                                        mMapView.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap mMap) {
                                                mMap = mMap;
                                                mMap.clear();
                                            }
                                        });
                                        String start_point1=place.getName();
                                        startpoint.setVisibility(View.VISIBLE);
                                        start_point=start_point1;
                                        startpoint.setText(start_point1);
                                        startALat= Double.valueOf(lat);
                                        startALog= Double.valueOf(lng);
                                        AppPreference.getPrefsHelper().savePref(Contents.START_LAT, ""+startALat);
                                        AppPreference.getPrefsHelper().savePref(Contents.START_LOG, ""+startALog);
                                        drawMapViewRoot(startALat,startALog,endALat,endALog);
                                    }else if(startOrend==1){
                                        mMapView.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap mMap) {
                                                mMap = mMap;
                                                mMap.clear();
                                            }
                                        });
                                        String end_point1=place.getName();
                                        endpoint.setVisibility(View.VISIBLE);
                                        endpoint.setText(end_point1);
                                        end_point=end_point1;
                                        endALat=Double.valueOf(lat);
                                        endALog=Double.valueOf(lng);
                                        AppPreference.getPrefsHelper().savePref(Contents.END_LAT, ""+endALat);
                                        AppPreference.getPrefsHelper().savePref(Contents.END_LOG, ""+endALog);
                                        drawMapViewRoot(startALat,startALog,endALat,endALog);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });

                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
   /* @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
*/
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
