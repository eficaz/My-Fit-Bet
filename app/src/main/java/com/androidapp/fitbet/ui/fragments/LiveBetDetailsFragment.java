package com.androidapp.fitbet.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.BuildConfig;
import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.map.LocationMonitoringService;
import com.androidapp.fitbet.model.LiveBetDetails;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.LoserActivity;
import com.androidapp.fitbet.ui.WinnerActivity;
import com.androidapp.fitbet.ui.adapters.LiveBetUserListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.network.Constant.DRAW_MAP_BASE_URL;
import static com.androidapp.fitbet.utils.Contents.BETDETAILS;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_FIRSTNAME;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.END_LOCATION;
import static com.androidapp.fitbet.utils.Contents.FIRST_NAME;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.LEGS;
import static com.androidapp.fitbet.utils.Contents.LOCATION;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betname;
import static com.androidapp.fitbet.utils.Contents.MYBETS_bettype;
import static com.androidapp.fitbet.utils.Contents.MYBETS_challengerid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_credit;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlongitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_route;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.androidapp.fitbet.utils.Contents.PARTICIPANT;
import static com.androidapp.fitbet.utils.Contents.POSITION;
import static com.androidapp.fitbet.utils.Contents.POSITION_LATITUDE;
import static com.androidapp.fitbet.utils.Contents.POSITION_LONGITUDE;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_KEY;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.ROUTES;
import static com.androidapp.fitbet.utils.Contents.START_DATE;
import static com.androidapp.fitbet.utils.Contents.START_LOCATION;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.TOTAL_DISTANCE;
import static com.androidapp.fitbet.utils.Contents.USER_start_latitude;
import static com.androidapp.fitbet.utils.Contents.USER_start_longitude;
import static com.androidapp.fitbet.utils.Contents.WINNER_PARTICIPANT;
import static com.androidapp.fitbet.utils.Contents.WON;

public class  LiveBetDetailsFragment extends Fragment implements OnMapReadyCallback{

    @Bind(R.id.map)
    MapView mMapView;

    @Bind(R.id.swipe_down)
    ImageView swipe_down;

    @Bind(R.id.img_user)
    CircleImageView img_user;


    @Bind(R.id.swipe_up)
    ImageView swipe_up;

    @Bind(R.id.swipe_up1)
    ImageView swipe_up1;


    @Bind(R.id.map_row)
    LinearLayout map_row;

    @Bind(R.id.details_row)
    LinearLayout details_row;

    @Bind(R.id.live_bet_title)
    LinearLayout live_bet_title;


    @Bind(R.id.details_row2)
    LinearLayout details_row2;

    @Bind(R.id.details_row_1)
    TableRow details_row_1;

    @Bind(R.id.betName)
    TextView betName;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.startTime)
    TextView startTime;

    @Bind(R.id.startTime1)
    TextView startTime1;

    @Bind(R.id.total_km)
    TextView total_km;


    @Bind(R.id.participate)
    TextView participate;

    @Bind(R.id.remaining_km)
    TextView remaining_km;

    @Bind(R.id.remaining_km1)
    TextView remaining_km1;

    @Bind(R.id.bet_members_list)
    RecyclerView bet_members_list;

    String regId_key;

    Double startALat=0.0;
    Double startALog=0.0;
    Polyline polyline;


    int kmInDec=0;
    ArrayList<LiveBetDetails>liveBetDetails;
    //////////////////////////////////////
    private GoogleMap mMap;
    ArrayList<LatLng> mMarkerPoints;
    //////////////////////////////////////
    String str_origin="";
    String str_dest="";
    LatLng sydneyStart;
    LatLng sydneyEnd;
    String betName1;

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedService = false;
    String startLog,startLat;
    String startDate="";
    String remaningKM;
    String regNo="";
    String betId="";
    String winName="";
    String won="";
    LiveBetUserListAdapter liveBetUserListAdapter;
    Marker moveableCurrentPoint;

    long delay = 5 * 1000; // delay in milliseconds
    LoopTask task = new LoopTask();
    Timer timer = new Timer("TaskName");

    String liveBetUpdateChallengerid;
    String liveBetUpdateTotalDis;

    String liveBetUpdateREG_KEY;
    String liveBetUpdateBettype;
    String liveBetUpdateRoute;
    Double endALat=0.0;
    Double endALog=0.0;


    String distance_draw="0";
    Double startDrowLat=0.0,startDrowLog=0.0,endDrowLat=0.0,endDrowLog=0.0;


    LocationManager manager;

    Double POSITION_LATITUDE1 = 0.0;
    Double DPOSITION_LONGITUD1 = 0.0;
    Double USER_STARTING_LATITUDE1 = 0.0;
    Double USER_STARTING_LONGITUD1 = 0.0;
    private Handler handler;
    private Runnable runnable;
    boolean kmDistenceMarker=false;
    boolean winner=true;
    boolean loser=true;

    private LocationRequest locationRequest;

    private int REQUEST_CHECK_SETTINGS=111;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap = mMap;
                mMap.setMyLocationEnabled(false);
               // LatLng sydney = new LatLng(lat, log);
            }
        });
        manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE );
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("Lat n lon live bet ",""+location.getLatitude()+" "+location.getLongitude());
                }
            }
        });
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
            Log.e("fitbet ds","Gps not enabled");
        createLocationRequest();
        }else{
            //AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
            callLiveBetApi(savedInstanceState);






            intintView(savedInstanceState);
        }
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
      /*  locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 500); // 10 seconds
        locationRequest.setFastestInterval(5 * 500); // 5 seconds
        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS

            }
        });
*/

    }
    private void call_Timer() {
        timer.cancel();
        timer = new Timer("TaskName");
        Date executionDate = new Date(); // no params = now
        timer.scheduleAtFixedRate(task, executionDate, delay);



    }

    private class LoopTask extends TimerTask {
        public void run() {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                Log.e("fitbet LoopTask","Gps not enabled");
           createLocationRequest();
            }else{
                String latitudeLongitude = Utils.getLocationFromNetwork();
                if(!latitudeLongitude.equals("")||!latitudeLongitude.equals(null)){
                    String[] Lat1 = latitudeLongitude.split(",");
                    Double lat1 = null;
                    Double log1= null;
                    lat1= Double.valueOf(Lat1[0]);
                    log1= Double.valueOf(Lat1[1]);
                    String origin=startALat+","+startALog;
                    //String dest = liveBetUpdateLatitude+","+liveBetUpdateLongitude;
                    Activity activity = getActivity();
                    if(activity != null){
                        Location locationA = new Location("point A");

                        if(POSITION_LATITUDE1.equals(0.0)){
                            locationA.setLatitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")));
                            locationA.setLongitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,"")));
                        }else{
                            locationA.setLatitude(POSITION_LATITUDE1);
                            locationA.setLongitude(DPOSITION_LONGITUD1);
                        }
                        Location locationB = new Location("point B");
                        if(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"").equals("")){
                            locationB.setLatitude(POSITION_LATITUDE1);
                            locationB.setLongitude(DPOSITION_LONGITUD1);
                        }else{
                            locationB.setLatitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")));
                            locationB.setLongitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,"")));
                        }
                        float location_distance = locationA.distanceTo(locationB);
                        double l2=0.0;
                        double l3=0.0;
                       if(location_distance>20){
                           l2= Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.UPDATE_METER,""));
                           l3= 0.01;
                       }else{
                           l2= Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.UPDATE_METER,""));
                           l3= Double.parseDouble(String.valueOf(location_distance));
                       }

                        double final_distence=0.0;
                        if (String.valueOf(l2).length() > 3) {
                            String input1 = String.valueOf(l2);
                            double numberAsString1= Double.parseDouble(input1);
                            String input2 = String.valueOf(l3);
                            double numberAsString2= Double.parseDouble(input2);
                            final_distence= numberAsString1 + numberAsString2;
                        }else{
                            String input1 = String.valueOf(l2);
                            double numberAsString1= Double.parseDouble(input1);
                            String input2 = String.valueOf(l3);
                            double numberAsString2= Double.parseDouble(input2);
                            final_distence= numberAsString1 + numberAsString2;
                        }
                        AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, ""+final_distence);
                        double remaningKMBefore=Double.parseDouble(remaningKM);
                        final double remaningKM1=(remaningKMBefore-final_distence);

                        callLiveBetUpdation(liveBetUpdateChallengerid, String.valueOf(final_distence),AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,""),AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""),liveBetUpdateREG_KEY,liveBetUpdateBettype,liveBetUpdateRoute);
                    }
                    Location dest_location1 = new Location("");
                    dest_location1.setLatitude(lat1);
                    dest_location1.setLongitude(log1);
                }else{
                    Toast.makeText(getActivity(), "lat : Not getting location", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
            Log.e("fitbet onResume","Gps not enabled");
          createLocationRequest();
        }else{
            startStep1();
        }
    }
    private float getDistanceBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween( lat1, lon1, lat2, lon2, distance);
        return distance[0];
    }
   private boolean hasGPSDevice(Context context) {
       final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
       if (mgr == null)
           return false;
       final List<String> providers = mgr.getAllProviders();
       if (providers == null)
           return false;
       return providers.contains(LocationManager.GPS_PROVIDER);
   }
    private void createLocationRequest() {
        LocationRequest mLocationRequest =Utils.getLocationRequest();


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                Log.d("Location","High accuracy location enabled");
                // Toast.makeText(DashBoardActivity.this, "addOnSuccessListener", Toast.LENGTH_SHORT).show();
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.live_bet_details_layout,container,false);
        ButterKnife.bind(this, view);
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void intintView(Bundle savedInstanceState) {
        showMap();
        swipe_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                details_row_1.setVisibility(View.VISIBLE);
                details_row2.setVisibility(View.GONE);
                swipe_up.setVisibility(View.VISIBLE);
                swipe_down.setVisibility(View.GONE);
                live_bet_title.setVisibility(View.GONE);
                return false;
            }
        });
        swipe_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                details_row_1.setVisibility(View.GONE);
                details_row2.setVisibility(View.VISIBLE);
                swipe_up.setVisibility(View.GONE);
                swipe_down.setVisibility(View.VISIBLE);
                live_bet_title.setVisibility(View.VISIBLE);
                return false;
            }
        });
        swipe_up1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                details_row_1.setVisibility(View.GONE);
                details_row2.setVisibility(View.VISIBLE);
                swipe_up.setVisibility(View.GONE);
                swipe_down.setVisibility(View.VISIBLE);
                live_bet_title.setVisibility(View.VISIBLE);
                return false;
            }
        });
        AppPreference.getPrefsHelper().savePref(Contents.DISTANCE, "0");
    }
    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                    // Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse(startDate);
                    Date currentDate = new Date();
                    if (!currentDate.before(futureDate)) {
                        long diff = futureDate.getTime() - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        String hours_one,minutes_one,seconds_one;
                        if(String.valueOf(hours).replace("-","").length()==1){
                             hours_one="0"+String.valueOf(hours).replace("-","");
                        }else{
                             hours_one= String.valueOf(hours).replace("-","");
                        }
                        if(String.valueOf(minutes).replace("-","").length()==1){
                             minutes_one="0"+String.valueOf(minutes).replace("-","");
                        }else{
                             minutes_one=String.valueOf(minutes).replace("-","");
                        }
                        if(String.valueOf(seconds).replace("-","").length()==1){
                             seconds_one="0"+String.valueOf(seconds).replace("-","");
                        }else{
                             seconds_one=String.valueOf(seconds).replace("-","");
                        }
                        startTime.setText("" + hours_one + ":" +"" +  minutes_one+":"+"" + seconds_one);
                        startTime1.setText("" + hours_one + ":" +"" +  minutes_one+":"+"" + seconds_one);
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }
    public static Bitmap createCustomMarker(Context context, @SuppressLint("SupportAnnotationUsage") @DrawableRes BitmapDescriptor resource, String _name) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        /*TextView txt_name = (TextView)marker.findViewById(R.id.name);
        txt_name.setText(_name);*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }
    private void callLiveBetApi(final Bundle savedInstanceState) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveBetDetails(AppPreference.getPrefsHelper().getPref(REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    liveBetDetailsReportapiresult(bodyString,savedInstanceState);
                    CustomProgress.getInstance().hideProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void liveBetDetailsReportapiresult(String bodyString, Bundle savedInstanceState) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString(STATUS_A);
            kmDistenceMarker=true;
            if (status.trim().equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String betdetails_data1 = jsonObject1.getString(BETDETAILS);
                final JSONObject jsonObject2 = new JSONObject(betdetails_data1);
                betName.setText(jsonObject2.getString(MYBETS_betname));
                str_origin=jsonObject2.getString(USER_start_latitude)+","+jsonObject2.getString(USER_start_longitude);
                str_dest=jsonObject2.getString(USER_start_latitude)+","+jsonObject2.getString(USER_start_longitude);
                startLat=jsonObject2.getString(USER_start_latitude);
                try{
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = df.parse(jsonObject2.getString(START_DATE));
                    df.setTimeZone(TimeZone.getDefault());
                    String formattedDate = df.format(date);
                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                    String output = null;
                    output = outputformat.format(date);
                    startDate=output;
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(!startDate.equals("")){
                    countDownStart();
                }
                remaningKM= jsonObject2.getString(TOTAL_DISTANCE);
                startLog= jsonObject2.getString(USER_start_longitude);
                sydneyStart = new LatLng(Double.valueOf(jsonObject2.getString(USER_start_latitude)),Double.valueOf(jsonObject2.getString(USER_start_longitude)));
                sydneyEnd = new LatLng(Double.valueOf(jsonObject2.getString(USER_start_latitude)),Double.valueOf(jsonObject2.getString(USER_start_longitude)));
                betName1=jsonObject2.getString(MYBETS_betname);
                mMapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
                mMapView.onResume(); // needed to get the map to display immediately
                mMapView.getMapAsync(this);
                mMarkerPoints = new ArrayList<>();
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        mMap = mMap;
                        mMap.setMyLocationEnabled(true);
                       // LatLng sydney = new LatLng(lat, log);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydneyStart).zoom(18).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        try{
                            //mMap.addMarker(new MarkerOptions().position(sydneyStart).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                        }catch (Exception e){

                        }
                    }
                });
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                        new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                final Activity activity = getActivity() ;
                                Double lat1 = null;
                                Double log1= null;
                                if(!AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"").equals("")){
                                    if (moveableCurrentPoint != null) {
                                        //moveableCurrentPoint.remove();
                                        //moveableCurrentPoint=mMap.addMarker(new MarkerOptions().position(new LatLng(POSITION_LATITUDE1, DPOSITION_LONGITUD1)).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(POSITION_LATITUDE1, DPOSITION_LONGITUD1), 18f));
                                        //moveVechile(moveableCurrentPoint, lat1,log1);
                                        //rotateMarker(moveableCurrentPoint, dest_location1.getBearing(), start_rotation);
                                    }
                                }
                                try{
                                    double dis= Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.DISTANCE,""));
                                    double distance1= dis;
                                    double totalDis=distance1*1000;
                                    AppPreference.getPrefsHelper().savePref(Contents.DISTANCE, ""+distance1);
                                    liveBetUpdateChallengerid=jsonObject2.getString(MYBETS_challengerid);
                                    liveBetUpdateREG_KEY=AppPreference.getPrefsHelper().getPref(REG_KEY,"");
                                    liveBetUpdateBettype=jsonObject2.getString(MYBETS_bettype);
                                    liveBetUpdateRoute=jsonObject2.getString(MYBETS_route);
                                }catch (Exception e){
                                }

                            }
                        }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));


                       if(jsonObject2.getString(MYBETS_bettype).equals(LOCATION)){
                            startALat= Double.valueOf(jsonObject2.getString(MYBETS_startlatitude));
                            startALog= Double.valueOf(jsonObject2.getString(MYBETS_startlongitude));
                           drawMapRout(jsonObject2.getString(MYBETS_startlatitude),jsonObject2.getString(MYBETS_startlongitude),jsonObject2.getString(MYBETS_endlatitude),jsonObject2.getString(MYBETS_endlongitude));
                       }else {
                           startALat= Double.valueOf(jsonObject2.getString(USER_start_latitude));
                           startALog= Double.valueOf(jsonObject2.getString(USER_start_longitude));
                       }
                String data1 = jsonObject1.getString(PARTICIPANT);
                JSONArray jsonArray = new JSONArray(data1);
                String POSITION1="",FIRST_NAME1="";
                liveBetDetails = new ArrayList<>();
                liveBetDetails.clear();

                CustomProgress.getInstance().hideProgress();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    LiveBetDetails model = new LiveBetDetails();
                    model.setPosition(jsonList.getString(POSITION));
                    model.setReg_key(jsonList.getString(REG_KEY));
                    regId_key=jsonList.getString(REG_KEY);
                    model.setFirstname(jsonList.getString(Contents.FIRST_NAME));
                    model.setEmail(jsonList.getString(Contents.EMAIL));
                    model.setCreditScore(jsonList.getString(Contents.CREDIT_SCORE));
                    model.setWon(jsonList.getString(WON));

                    model.setLost(jsonList.getString(Contents.LOST));

                    model.setCountry(jsonList.getString(Contents.COUNTRY));

                    model.setProfile_pic(jsonList.getString(PROFILE_PIC));

                    model.setImage_status(jsonList.getString(IMAGE_STATUS));

                    model.setRegType(jsonList.getString(REG_TYPE));

                    model.setDistance(jsonList.getString(DISTANCE));

                    model.setStartdate(jsonList.getString(Contents.START_DATE));

                    model.setPositionlongitude(jsonList.getString(Contents.POSITION_LONGITUDE));

                    model.setPositionlatitude(jsonList.getString(Contents.POSITION_LATITUDE));

                    liveBetDetails.add(model);
                    POSITION_LATITUDE1=Double.valueOf(jsonList.getString(POSITION_LATITUDE));
                    DPOSITION_LONGITUD1=Double.valueOf(jsonList.getString( POSITION_LONGITUDE));
                    USER_STARTING_LATITUDE1 = Double.valueOf(jsonObject2.getString(USER_start_longitude));
                    USER_STARTING_LONGITUD1 = Double.valueOf(jsonObject2.getString(USER_start_latitude));
                    POSITION1=jsonList.getString(POSITION);
                    participate.setText(POSITION1);
                    FIRST_NAME1=jsonList.getString(FIRST_NAME);
                    final Context mContext = getActivity() ;
                    if(regId_key.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                        double totaldis= Double.parseDouble(jsonList.getString(DISTANCE));
                        double final_distence1= totaldis/1000;
                        final DecimalFormat f = new DecimalFormat("##.00");
                        total_km.setText(""+f.format(final_distence1)+" km");
                        String remtime= String.valueOf((Double.parseDouble(remaningKM))-Double.parseDouble(jsonList.getString(DISTANCE))/1000);
                        name.setText(jsonList.getString(DASH_BOARD_FIRSTNAME));
                        //final DecimalFormat f = new DecimalFormat("##.00");
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        double decimal= Double.parseDouble(String.valueOf(remtime).replace("-",""));
                        String input = String.valueOf(decimal).substring(0,5);
                        double numberAsString= Double.parseDouble(input);
                        remaining_km.setText(""+decimalFormat.format(numberAsString/1000)+" km");
                        remaining_km1.setText(""+decimalFormat.format(numberAsString/1000)+" km");
                        if (!jsonList.getString(PROFILE_PIC).equals("NA")) {
                            if (jsonList.getString(REG_TYPE).equals("normal")&&jsonList.getString(IMAGE_STATUS).equals("0")){
                                Picasso.get().
                                        load(Constant.BASE_APP_IMAGE__PATH+jsonList.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(img_user);
                            }else{
                                Picasso.get().
                                        load(jsonList.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(img_user);
                            }
                        } else {
                            img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.user_profile_avatar));
                        }
                    }
                    if(jsonObject2.getString(MYBETS_bettype).equals(LOCATION)){
                        markAllUserFromLocation(Double.parseDouble(jsonObject2.getString(MYBETS_startlatitude)),Double.parseDouble(jsonObject2.getString(MYBETS_startlongitude)),Double.parseDouble(jsonObject2.getString(MYBETS_endlatitude)),Double.parseDouble(jsonObject2.getString(MYBETS_endlongitude)),POSITION_LATITUDE1,DPOSITION_LONGITUD1,regId_key);
                    }else{
                        markAllbetUsers(USER_STARTING_LATITUDE1,USER_STARTING_LONGITUD1,POSITION1,FIRST_NAME1,regId_key,jsonObject2.getString(MYBETS_bettype));
                    }
                }
                //participate.setText(jsonObject2.getString(POSITION));
                liveBetUserListAdapter = new LiveBetUserListAdapter(getActivity(), liveBetDetails);
                bet_members_list.setHasFixedSize(true);
                bet_members_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                bet_members_list.setAdapter(liveBetUserListAdapter);
                if(liveBetDetails.size()>0){
                    call_Timer();
                }

            }
        }catch (Exception e){

        }
    }
    private void markAllUserFromLocation(final Double startLat, final Double startLog, final Double endLat, final Double eldLog, final Double currentLat, final Double currentLog, final String regId_key) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                if(regId_key.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                   // moveableCurrentPoint= mMap.addMarker(new MarkerOptions().position(new LatLng(currentLat, currentLog)).title("").snippet(""));
                }
                mMap.addMarker(new MarkerOptions().position(new LatLng(startLat, startLog)).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(endLat, eldLog)).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startLat, startLog), 18f));
            }
        });
    }
    private void drawMapRout(String startLat, String startLog, String endLat, String eldLog) {
        drawMapViewRoot(Double.parseDouble(startLat),Double.parseDouble(startLog),Double.parseDouble(endLat),Double.parseDouble(eldLog));
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
            String origin=startALat+","+startALog;
            String dest=endALat+","+endALog;
            drawRoute(origin,dest);
        }else{

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
                    String distance=jb1.getString(DISTANCE);
                    final JSONObject jsonObject_distance = new JSONObject(distance);
                    distance_draw=jsonObject_distance.getString("text");
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
                    liveBetUpdateTotalDis=distance_draw;
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
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
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
                        LatLng sydney = new LatLng(startALat, startALog);
                        mMap.addPolyline(finalLineOptions);
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
    private void callLiveBetUpdation(String challengerid,String disstence,String lat,String log,String regId_key,String betType,String root) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveDetailsUpdation(challengerid,disstence,Double.valueOf(log),Double.valueOf(lat), AppPreference.getPrefsHelper().getPref(REG_KEY,""),betType,root);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(bodyString);
                        String data = jsonObject.getString("Status");
                        if(data.equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                            JSONObject jsonObject1 = new JSONObject(bodyString);
                            String data1 = jsonObject1.getString(WINNER_PARTICIPANT);
                            JSONArray yukilanarray =  new JSONArray(data1);
                            for(int i=0;i<yukilanarray.length();i++)
                            {
                                JSONObject jsonObject2 = yukilanarray.getJSONObject(i);
                                regNo=jsonObject2.getString(REG_KEY);
                                winName=jsonObject2.getString(FIRST_NAME);
                                //won=jsonObject2.getString(WON);

                            }
                            won=jsonObject1.getString(MYBETS_credit);
                            JSONArray jsonArray = new JSONArray(data1);
                            String data2 = jsonObject1.getString(BETDETAILS);
                            //JSONArray jsonArray2 = new JSONArray(data2);
                            JSONObject jsonObject3 = new JSONObject(data2);
                            betId=jsonObject3.getString(MYBETS_betid);
                            if(jsonArray.length()!=0){
                                if(regNo.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                                    if(!regNo.equals("")&&!winName.equals("")&&!won.equals("")&&!betId.equals("")){
                                        if(winner==true){
                                            winner=false;
                                            handler.removeCallbacks(runnable);
                                            AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
                                            Intent i = new Intent(getActivity(), WinnerActivity.class);
                                            i.putExtra(REG_KEY,regNo);
                                            i.putExtra(FIRST_NAME,winName);
                                            i.putExtra(MYBETS_betid,betId);
                                            i.putExtra(WON,won);
                                            startActivity(i);
                                            getActivity().stopService(new Intent(getActivity(), LocationMonitoringService.class));
                                        }
                                    }else{
                                        Utils.showCustomToastMsg(getActivity(), R.string.imvalid_entry);
                                    }
                                }else{
                                    if(loser==true){
                                        loser=false;
                                        handler.removeCallbacks(runnable);
                                        AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
                                        Intent i = new Intent(getActivity(), LoserActivity.class);
                                        i.putExtra(MYBETS_betid,betId);
                                        startActivity(i);
                                        getActivity().stopService(new Intent(getActivity(), LocationMonitoringService.class));
                                    }
                                }
                            }
                            liveBetDetailsReportapiresult1(bodyString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void liveBetDetailsReportapiresult1(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString(STATUS_A);
            if (status.trim().equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String betdetails_data1 = jsonObject1.getString(BETDETAILS);
                final JSONObject jsonObject2 = new JSONObject(betdetails_data1);
                //jsonObject2.getString(TOTAL_DISTANCE);
                String data1 = jsonObject1.getString(PARTICIPANT);
                JSONArray jsonArray = new JSONArray(data1);
                String POSITION1="",FIRST_NAME1="";
                liveBetDetails = new ArrayList<>();
                liveBetDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    LiveBetDetails model = new LiveBetDetails();
                    model.setPosition(jsonList.getString(POSITION));

                    model.setReg_key(jsonList.getString(REG_KEY));

                    regId_key=jsonList.getString(REG_KEY);

                    model.setFirstname(jsonList.getString(Contents.FIRST_NAME));

                    model.setEmail(jsonList.getString(Contents.EMAIL));

                    model.setCreditScore(jsonList.getString(Contents.CREDIT_SCORE));

                    model.setWon(jsonList.getString(WON));

                    model.setLost(jsonList.getString(Contents.LOST));

                    model.setCountry(jsonList.getString(Contents.COUNTRY));

                    model.setProfile_pic(jsonList.getString(PROFILE_PIC));

                    model.setImage_status(jsonList.getString(IMAGE_STATUS));

                    model.setRegType(jsonList.getString(REG_TYPE));

                    model.setDistance(jsonList.getString(DISTANCE));

                    model.setStartdate(jsonList.getString(Contents.START_DATE));

                    model.setPositionlongitude(jsonList.getString(Contents.POSITION_LONGITUDE));

                    model.setPositionlatitude(jsonList.getString(Contents.POSITION_LATITUDE));

                    liveBetDetails.add(model);
                    POSITION_LATITUDE1=Double.valueOf(jsonList.getString(POSITION_LATITUDE));
                    DPOSITION_LONGITUD1=Double.valueOf(jsonList.getString( POSITION_LONGITUDE));
                    POSITION1=jsonList.getString(POSITION);
                    participate.setText(POSITION1);
                    FIRST_NAME1=jsonList.getString(FIRST_NAME);
                    final Context mContext = getActivity() ;

                    if(regId_key.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                        double totaldis= Double.parseDouble(jsonList.getString(DISTANCE));
                        double final_distence= totaldis/1000;
                        final DecimalFormat f = new DecimalFormat("##.00");
                        total_km.setText(""+f.format(final_distence)+" km");


                        double total_dis=Double.parseDouble(jsonObject2.getString(TOTAL_DISTANCE))-Double.parseDouble(jsonList.getString(DISTANCE));
                        //totalKM= jsonObject2.getString(TOTAL_DISTANCE);
                        remaningKM= String.valueOf(total_dis);
                        DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
                        double decimal1= Double.parseDouble(String.valueOf(total_dis).replace("-",""));
                        String input1 = String.valueOf(decimal1).substring(0,5);
                        double numberAsString1= Double.parseDouble(input1);
                        remaining_km.setText(""+decimalFormat1.format(numberAsString1/1000)+" km");
                        remaining_km1.setText(""+decimalFormat1.format(numberAsString1/1000)+" km");
                        mMapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                if(regId_key.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                                }
                                //mMap.addMarker(new MarkerOptions().position(new LatLng(startLat, startLog)).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                               if(kmDistenceMarker==true){
                                   moveableCurrentPoint=mMap.addMarker(new MarkerOptions().position(new LatLng(POSITION_LATITUDE1, DPOSITION_LONGITUD1)).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)));
                                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(POSITION_LATITUDE1, DPOSITION_LONGITUD1), 18f));
                               }else{
                                   moveableCurrentPoint.remove();
                                   moveableCurrentPoint=mMap.addMarker(new MarkerOptions().position(new LatLng(POSITION_LATITUDE1, DPOSITION_LONGITUD1)).title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location)));
                                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(POSITION_LATITUDE1, DPOSITION_LONGITUD1), 18f));
                               }
                                kmDistenceMarker=false;
                            }
                        });

                        if (!jsonList.getString(PROFILE_PIC).equals("NA")) {
                            if (jsonList.getString(REG_TYPE).equals("normal")&&jsonList.getString(IMAGE_STATUS).equals("0")){

                                Picasso.get().
                                        load(Constant.BASE_APP_IMAGE__PATH+jsonList.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(img_user);
                            }else{

                                Picasso.get().
                                        load(jsonList.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(img_user);
                            }
                        } else {
                            img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.user_profile_avatar));
                        }
                    }
                }
                //participate.setText(jsonObject2.getString(POSITION));
                liveBetUserListAdapter = new LiveBetUserListAdapter(getActivity(), liveBetDetails);
                bet_members_list.setHasFixedSize(true);
                bet_members_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                bet_members_list.setAdapter(liveBetUserListAdapter);
            }
        }catch (Exception e){

        }
    }
    public void markAllbetUsers(final Double latitude, final Double longitude, final String position, final String firstName,final String regId_key,String betType) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(regId_key.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                    //moveableCurrentPoint=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(getActivity(),BitmapDescriptorFactory.defaultMarker(),position))).title(firstName));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18f));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(firstName).snippet(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                }else{
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(firstName).snippet(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location)));
                }
            }
        });
    }
    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {
        startStep2(null);
    }
    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        //Yes there is active internet connection. Next check Location is granted by user or not.
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }
    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);
        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {
                            //Now make sure about location permission.
                            if (checkPermissions()) {
                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {
        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.
        if (!mAlreadyStartedService) {
            //name.setText(R.string.msg_location_service_started);
            //Start location sharing service to app server.........
            Intent intent = new Intent(getActivity(), LocationMonitoringService.class);
            getActivity().startService(intent);
            mAlreadyStartedService = true;
            //Ends................................................
        }
    }
    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;
    }
    /**
     * Start permissions requests.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStep3();
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }

    }
    @Override
    public void onDestroy() {
        //Stop location sharing service to app server.........
        mAlreadyStartedService = false;
        super.onDestroy();
    }
    public void showMap() {
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }



}
