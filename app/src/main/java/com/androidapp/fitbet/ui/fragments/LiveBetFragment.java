package com.androidapp.fitbet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.interfaces.LocationReceiveListener;
import com.androidapp.fitbet.model.LiveBetDetails;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.polyline.DirectionFinder;
import com.androidapp.fitbet.polyline.DirectionFinderListener;
import com.androidapp.fitbet.polyline.Route;
import com.androidapp.fitbet.service.LocReceiver;
import com.androidapp.fitbet.service.LocService;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.ui.LoserActivity;
import com.androidapp.fitbet.ui.WinnerActivity;
import com.androidapp.fitbet.ui.adapters.LiveBetUserListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.polyline.GoogleMapHelper.getDefaultPolyLines;
import static com.androidapp.fitbet.utils.Contents.BETDETAILS;
import static com.androidapp.fitbet.utils.Contents.BET_START_STATUS;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.FIRST_NAME;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.LOCATION;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betname;
import static com.androidapp.fitbet.utils.Contents.MYBETS_bettype;
import static com.androidapp.fitbet.utils.Contents.MYBETS_challengerid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_credit;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.androidapp.fitbet.utils.Contents.PARTICIPANT;
import static com.androidapp.fitbet.utils.Contents.POSITION;
import static com.androidapp.fitbet.utils.Contents.POSITION_LATITUDE;
import static com.androidapp.fitbet.utils.Contents.POSITION_LONGITUDE;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_KEY;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.START_DATE;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.TOTAL_DISTANCE;
import static com.androidapp.fitbet.utils.Contents.USER_start_latitude;
import static com.androidapp.fitbet.utils.Contents.USER_start_longitude;
import static com.androidapp.fitbet.utils.Contents.WINNER_PARTICIPANT;
import static com.androidapp.fitbet.utils.Contents.WON;

public class LiveBetFragment extends Fragment implements OnMapReadyCallback , DirectionFinderListener, LocationReceiveListener {

    @Bind(R.id.contstraint_layout)
    ConstraintLayout constraintLayout;

    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;

    @Bind(R.id.frameLayout)
    FrameLayout frameLayout;

    @Bind(R.id.cardView)
    CardView cardView;

    @Bind({R.id.arrow})
    ImageView arrowImage;

    @Bind({R.id.mapView})
    MapView mapView;

    @Bind(R.id.txt_remaining_km)
    TextView txtRemainingKm;

    @Bind(R.id.txt_time_lapsed)
    TextView txtTimeLapsed;

    @Bind(R.id.txt_participate)
    TextView txtParticipate;

    @Bind(R.id.total_km)
    TextView txtTotalKm;

    @Bind(R.id.txt_bet_name)
    TextView txtBetName;

    @Bind(R.id.txt_name)
    TextView txtName;

    @Bind(R.id.recyclerView)
    RecyclerView betMembersRecyclerView;

    @Bind(R.id.img_user)
    CircleImageView userImage;

    private Polyline polyline=null;
    private int REQUEST_CHECK_SETTINGS = 111;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager mLocationManager;
    private Handler counterHandler;
    private Runnable counterRunnable;
    private String origin="", destination="";
    private GoogleMap googleMap;
    private Double startLatitude = 0.0, startLongitude = 0.0, positionLatitude = 0.0, positionLongitude = 0.0,endLatitude=0.0,endLongitude=0.0;
    private String challengerId, betType, userDistance, userRoute="";
    private Intent serviceIntent;
    private ArrayList<LiveBetDetails> liveBetDetailsArrayList = null;
    private LiveBetUserListAdapter liveBetUserListAdapter;
    private MyDialog noInternetDialog;
    private String locationRoute="";
/*    private static LiveBetFragment instance;*/

    private MyTimerTask timerTask=new MyTimerTask();
    private  Timer timer=new Timer("update");
    private String regNo="";
    private  String betId="";
    private String winName="";
    private String won="";
    private  boolean winner=true;
    private  boolean loser=true;
    private MarkerOptions startMarkerOptions, positionMarkerOptions;
    private Marker positionMarker=null,startMarker=null;
private LocationReceiveListener locationReceiveListener=null;
private DashBoardActivity dashBoardActivity;
private AppPreference appPreference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_bet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appPreference=AppPreference.getPrefsHelper(getActivity());
        appPreference.savePref(Contents.BET_START_STATUS,"true");
        appPreference.savePref(Contents.DASH_BOARD_POSICTION,"2");
      /*  _context.registerReceiver(broadcastReceiver,new IntentFilter("location_update"));*/
        dashBoardActivity=(DashBoardActivity)getActivity();

     /*   instance = this;*/
        serviceIntent = new Intent(getActivity(), LocService.class);

        startLocationService(serviceIntent);

        locationReceiveListener=this;
        LocReceiver.registerLocationReceiveListener(locationReceiveListener);

System.out.println("onViewCreated LiveBetFragment");
        mapView.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null);
        mapView.onResume(); // needed to get the map to display immediately
        mapView.getMapAsync(this);
         startMarkerOptions =new MarkerOptions().title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.start_location));
         positionMarkerOptions =new MarkerOptions().title("").snippet("").icon(BitmapDescriptorFactory.fromResource(R.drawable.end_location));

        noInternetDialog = new MyDialog(getActivity(), null, getString(R.string.no_internet), getString(R.string.no_internet_message), getString(R.string.ok), "", true, "internet");
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
            Log.e("fitbet ds", "Gps not enabled");
            createLocationRequest();
        } else {
            if (Utils.isConnectedToInternet(getActivity())) {

                getLiveBetDetails();
            } else {
                noInternetDialog.show();
            }

        }

        arrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(((String)arrowImage.getTag()).equals("down")){
                  arrowImage.setTag("up");
                  arrowImage.setImageResource(R.drawable.arrow_up);
                  setupNewConstraints();

              }else{
                  arrowImage.setTag("down");
                  arrowImage.setImageResource(R.drawable.arrow_down);
                  releaseNewConstraints();
              }
            }
        });

    }

    private void setupNewConstraints() {

        frameLayout.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);
        betMembersRecyclerView.setVisibility(View.GONE);
        dashBoardActivity.hideBottomNavigationView();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.mapView,ConstraintSet.BOTTOM,R.id.linearLayout,ConstraintSet.TOP,0);
        constraintSet.connect(R.id.mapView,ConstraintSet.END,R.id.contstraint_layout,ConstraintSet.END,0);
        constraintSet.connect(R.id.mapView,ConstraintSet.START,R.id.contstraint_layout,ConstraintSet.START,0);
        constraintSet.connect(R.id.mapView,ConstraintSet.TOP,R.id.txt_bet_name,ConstraintSet.BOTTOM,0);
        constraintSet.constrainDefaultHeight(R.id.mapView,ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.constrainDefaultWidth(R.id.mapView,ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.setVerticalBias(R.id.mapView,0.0f);
        constraintSet.setHorizontalBias(R.id.mapView,0.0f);

       constraintSet.constrainDefaultHeight(R.id.linearLayout,70);

        constraintSet.applyTo(constraintLayout);

/*        app:layout_constraintBottom_toTopOf="@+id/linearLayout"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/txt_bet_name"
        app:layout_constraintVertical_bias="0.0"*/

    }
    private void releaseNewConstraints() {

        frameLayout.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);
        betMembersRecyclerView.setVisibility(View.VISIBLE);
        dashBoardActivity.showBottomNavigationView();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.mapView,ConstraintSet.BOTTOM,R.id.contstraint_layout,ConstraintSet.BOTTOM,0);
        constraintSet.connect(R.id.mapView,ConstraintSet.END,R.id.contstraint_layout,ConstraintSet.END,0);
        constraintSet.connect(R.id.mapView,ConstraintSet.START,R.id.contstraint_layout,ConstraintSet.START,0);
        constraintSet.connect(R.id.mapView,ConstraintSet.TOP,R.id.txt_bet_name,ConstraintSet.BOTTOM,0);
        constraintSet.constrainDefaultHeight(R.id.mapView,200);
        constraintSet.constrainDefaultWidth(R.id.mapView,ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.setVerticalBias(R.id.mapView,0.0f);
        constraintSet.setHorizontalBias(R.id.mapView,0.0f);

        constraintSet.constrainDefaultHeight(R.id.linearLayout,60);

        constraintSet.applyTo(constraintLayout);

     /*      <com.google.android.gms.maps.MapView
        android:id="@+id/mapView"
        android:layout_width="0dp"
        android:layout_height="200dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/txt_bet_name"
        app:layout_constraintVertical_bias="0.0" />*/
    }

    private boolean isTimerRunning,run;
    private void cancelTimer(){

        if(isTimerRunning){
            timer.cancel();
            timer.purge();
            isTimerRunning=false;
        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private Context _context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SLApplication.isServiceRunning)
            startLocationService(serviceIntent);
if(locationReceiveListener==null) {
    locationReceiveListener = this;
    LocReceiver.registerLocationReceiveListener(locationReceiveListener);
}



    }

    @Override
    public void onStop() {
        System.out.println("Live bet inside onStop ");
  /*      run=false;
        if (SLApplication.isServiceRunning) {
            System.out.println("Live bet stopping service ");
            stopLocationService(serviceIntent);
            new LocService().stopSelf();
        }*/
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // run=false;

    }
    @Override
    public void onPause() {
        super.onPause();
       // run=false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
if(googleMap!=null)
        googleMap.setMyLocationEnabled(true);

        if(positionMarker!=null) {
         // positionMarker.remove();
    positionMarker.setPosition(new LatLng(positionLatitude,positionLongitude));
           // animateMarker(positionMarker,positionMarker.getPosition(),new LatLng(positionLatitude,positionLongitude),false);
            System.out.println("onMapReady drawing position marker");
        }


     googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(  positionLatitude,   positionLongitude), 18f));

    }


    private void animateMarker(final Marker marker , final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker) {




        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
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
        LocationRequest mLocationRequest = Utils.getLocationRequest();


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                Log.d("Location", "High accuracy location enabled");
                // Toast.makeText(DashBoardActivity.this, "addOnSuccessListener", Toast.LENGTH_SHORT).show();
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                if (!SLApplication.isServiceRunning)
                    startLocationService(serviceIntent);
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

    private void getLiveBetDetails() {
        if(!CustomProgress.getInstance().isShowing())
        CustomProgress.getInstance().showProgress(getActivity(), "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveBetDetails(appPreference.getPref(REG_KEY, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("BET details "+bodyString);
                    publishBetDetails(bodyString);

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

    private void publishBetDetails(String bodyString) {
if(CustomProgress.getInstance().isShowing())
        CustomProgress.getInstance().hideProgress();
        try {
            JSONObject mainJsonObject = new JSONObject(bodyString);
            if (mainJsonObject.getString(STATUS_A).trim().equals("Ok")) {
                JSONObject betDetailsObject = mainJsonObject.getJSONObject("betdetails");

                txtBetName.setText(betDetailsObject.getString(MYBETS_betname));
                origin = betDetailsObject.getString(USER_start_latitude) + "," + betDetailsObject.getString(USER_start_longitude);
                destination = betDetailsObject.getString(USER_start_latitude) + "," + betDetailsObject.getString(USER_start_longitude);

                startLatitude = betDetailsObject.getDouble(USER_start_latitude);
                startLongitude = betDetailsObject.getDouble(USER_start_longitude);
                positionLatitude=startLatitude;
                positionLongitude=startLongitude;
                positionMarker=googleMap.addMarker(positionMarkerOptions.position(new LatLng(startLatitude,startLatitude)));
               startMarker= googleMap.addMarker(startMarkerOptions.position(new LatLng(startLatitude, startLongitude)));
                if(!appPreference.getSavedStatusFlag()) {
                    appPreference.savePref(USER_start_latitude, betDetailsObject.getString(USER_start_latitude));
                    appPreference.savePref(USER_start_longitude, betDetailsObject.getString(USER_start_longitude));
                    appPreference.saveOrigin(betDetailsObject.getString(USER_start_latitude) + "," + betDetailsObject.getString(USER_start_longitude));
                    appPreference.savedStatusFlag(true);
                }
               onMapReady(googleMap);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(betDetailsObject.getString(START_DATE));
                df.setTimeZone(TimeZone.getDefault());
                String formattedDate = df.format(date);
                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                String output = null;
                output = outputformat.format(date);


                if (!output.equals("")) {
                    startCounter(output);
                }

                challengerId = betDetailsObject.getString(MYBETS_challengerid);
                userRoute = betDetailsObject.getString("userroute");
                betType = betDetailsObject.getString(MYBETS_bettype);
               // userDistance=betDetailsObject.getString("distance");

                if (betDetailsObject.getString(MYBETS_bettype).equals(LOCATION)) {
                    startLatitude = betDetailsObject.getDouble(MYBETS_startlatitude);
                    startLongitude = betDetailsObject.getDouble(MYBETS_startlongitude);
                    endLatitude=betDetailsObject.getDouble("endlatitude");
                    endLongitude=betDetailsObject.getDouble("endlongitude");
                    locationRoute=betDetailsObject.getString("route");
                    googleMap.addMarker(positionMarkerOptions.position(new LatLng(endLatitude,endLongitude)));
                    drawPolyLine(locationRoute,Color.BLACK);
                    // drawMapRout(jsonObject2.getString(MYBETS_startlatitude),jsonObject2.getString(MYBETS_startlongitude),jsonObject2.getString(MYBETS_endlatitude),jsonObject2.getString(MYBETS_endlongitude));
                }

                liveBetDetailsArrayList = new ArrayList<>();
                liveBetDetailsArrayList.clear();
                JSONArray jsonArray = new JSONArray(mainJsonObject.getString(PARTICIPANT));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                    LiveBetDetails liveBetDetails = new LiveBetDetails();
                    liveBetDetails.setPosition(arrayObject.getString(POSITION));
                    liveBetDetails.setReg_key(arrayObject.getString(REG_KEY));
                    liveBetDetails.setFirstname(arrayObject.getString(Contents.FIRST_NAME));
                    liveBetDetails.setEmail(arrayObject.getString(Contents.EMAIL));
                    liveBetDetails.setCreditScore(arrayObject.getString(Contents.CREDIT_SCORE));
                    liveBetDetails.setWon(arrayObject.getString(WON));

                    liveBetDetails.setLost(arrayObject.getString(Contents.LOST));

                    liveBetDetails.setCountry(arrayObject.getString(Contents.COUNTRY));

                    liveBetDetails.setProfile_pic(arrayObject.getString(PROFILE_PIC));

                    liveBetDetails.setImage_status(arrayObject.getString(IMAGE_STATUS));

                    liveBetDetails.setRegType(arrayObject.getString(REG_TYPE));

                    liveBetDetails.setDistance(arrayObject.getString(DISTANCE));

                    liveBetDetails.setStartdate(arrayObject.getString(Contents.START_DATE));

                    liveBetDetails.setPositionlongitude(arrayObject.getString(Contents.POSITION_LONGITUDE));

                    liveBetDetails.setPositionlatitude(arrayObject.getString(Contents.POSITION_LATITUDE));

                    liveBetDetailsArrayList.add(liveBetDetails);


                    if(arrayObject.getString("reg_key").equals(appPreference.getPref(REG_KEY,""))){
                        positionLatitude=arrayObject.getDouble(POSITION_LATITUDE);
                        positionLongitude=arrayObject.getDouble( POSITION_LONGITUDE);
                        txtParticipate.setText(arrayObject.getString(POSITION));
                        txtName.setText(arrayObject.getString(FIRST_NAME));
                        if (!arrayObject.getString(PROFILE_PIC).equals("NA")) {
                            if (arrayObject.getString(REG_TYPE).equals("normal")&&arrayObject.getString(IMAGE_STATUS).equals("0")){

                                Picasso.get().
                                        load(Constant.BASE_APP_IMAGE__PATH+arrayObject.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(userImage);
                            }else{

                                Picasso.get().
                                        load(arrayObject.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(userImage);
                            }
                        } else {
                            userImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.user_profile_avatar));
                        }

                        double totalDistance= (arrayObject.getDouble(DISTANCE));
                        txtTotalKm.setText(formatNumber2Decimals(totalDistance)+" km");


                        double remainingDistance=betDetailsObject.getDouble(TOTAL_DISTANCE)-arrayObject.getDouble(DISTANCE);

                        txtRemainingKm.setText(formatNumber2Decimals(remainingDistance)+" km");

                    }



                    liveBetUserListAdapter = new LiveBetUserListAdapter(getActivity(), liveBetDetailsArrayList);
                    betMembersRecyclerView.setHasFixedSize(true);
                    betMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    betMembersRecyclerView.setAdapter(liveBetUserListAdapter);
                }


            }
            startLocationService(serviceIntent);
      locationReceiveListener = LiveBetFragment.this;
      LocReceiver.registerLocationReceiveListener(locationReceiveListener);


        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        //getUpdates();

        //////////////////////////////sunday/////////////////
/*new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        scheduleTimer();
    }
},1000);*/
        /////////////////////////////////////////////////////

    }


    private void startCounter(final String startDate) {
        counterHandler = new Handler();
        counterRunnable = new Runnable() {
            @Override
            public void run() {
                counterHandler.postDelayed(this, 1000);
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
                        String hours_one, minutes_one, seconds_one;
                        if (String.valueOf(hours).replace("-", "").length() == 1) {
                            hours_one = "0" + String.valueOf(hours).replace("-", "");
                        } else {
                            hours_one = String.valueOf(hours).replace("-", "");
                        }
                        if (String.valueOf(minutes).replace("-", "").length() == 1) {
                            minutes_one = "0" + String.valueOf(minutes).replace("-", "");
                        } else {
                            minutes_one = String.valueOf(minutes).replace("-", "");
                        }
                        if (String.valueOf(seconds).replace("-", "").length() == 1) {
                            seconds_one = "0" + String.valueOf(seconds).replace("-", "");
                        } else {
                            seconds_one = String.valueOf(seconds).replace("-", "");
                        }
                        txtTimeLapsed.setText("" + hours_one + ":" + "" + minutes_one + ":" + "" + seconds_one);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        counterHandler.postDelayed(counterRunnable, 1 * 1000);
    }

    @Override
    public void onDirectionFinderStart() {

    }


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        System.out.println("onDirectionFinderSuccess");

   /* if (!routes.isEmpty() && polyline != null)
        polyline.remove();*/
/*        try {
            for (Route route : routes) {
                PolylineOptions polylineOptions = getDefaultPolyLines(route.points);

                polyline = googleMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
    googleMap.animateCamera(buildCameraUpdate(routes.get(0).endLocation), 10, null);*/

/////////////////////////////sunday//////////////////////////////////////////
       /* if(userRoute.equals(""))
            userRoute=routes.get(0).pointString;
        else
            userRoute = userRoute + "fitbet" + routes.get(0).pointString;


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(!userRoute.equals("")) {
                    userRoute=userRoute.replaceAll("\\\\", "");
                    System.out.println("Userroute == "+userRoute);
                    drawPolyLine(userRoute, Color.RED);
                }

            }
        });*/
/////////////////////////////////////////////////////////////////////////////
    }



    private void startLocationService(Intent intent) {
if(getActivity()!=null) {
    getActivity().startService(intent);
    SLApplication.isServiceRunning = true;
}
}

    private void stopLocationService(Intent intent) {
        if(getActivity()!=null) {
            getActivity().stopService(intent);
            SLApplication.isServiceRunning = false;
        }
    }

    private void fetchDirections(String origin, String destination) {
        System.out.println("fetchDirections "+"origin "+origin+" , "+"destination "+destination);
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
private int i=2;
    private double userDistanceDouble=0.0;
    @Override
    public void onLocationReceived(Double lat, Double lon) {
   /*     i++;*/

   ////////////////////////////////////sunday////////////////////////////////////////////////////////////////
    /*  if(positionLatitude!=0.0)
        userDistanceDouble=userDistanceDouble+getDistance(positionLatitude,positionLongitude,lat,lon);

        positionLatitude = lat;
        positionLongitude = lon;

        *//*if(i%3==0) {*//*


            Log.d("onLocationRcvd LIVE "+i, "location : " + lat + "," + lon+"poslat "+positionLatitude);

            destination=""+lat+","+lon;

            if (!origin.equals(""))
                fetchDirections(origin, destination);
            origin = destination;*/
 //////////////////////////////////////////////////////////////////////////////////////////////////////////
            //  new UpdateAsyncTask().execute();
      /*      i=0;
        }
*/

       //////////////////////////sunday//////////////
        Log.d("onLocationRcvd LIVE ",""+lat+" , "+lon);


        if(!run) {
            System.gc();
            if(positionLatitude!=0.0)
            appPreference.saveDistance(String.valueOf(Double.parseDouble(appPreference.getSavedDistance() )+ getDistance(positionLatitude, positionLongitude, lat, lon)));
            System.out.println("appPreference.getSavedDistance() = "+appPreference.getSavedDistance());

            positionLatitude = lat;
            positionLongitude = lon;

            getUpdates();
        }



        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(!appPreference.getSavedUserRoute().equals("")) {
                    userRoute=appPreference.getSavedUserRoute().replaceAll("\\\\", "");
                    System.out.println("Userroute == "+userRoute);
                    drawPolyLine(userRoute, Color.RED);
                }

            }
        });
      //////////////////////////////////
        onMapReady(googleMap);

    }

    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {

            if(positionLatitude!=0.0) {
                System.gc();
                getUpdates();

            }

        }
    }

    private void drawPolyLine(@NotNull String userRoute, final int color) {
        List<LatLng> latLngList = new ArrayList<>();

        String[] splitRoutes = userRoute.split("fitbet");
        List<String> routeList= new LinkedList<>(Arrays.asList(splitRoutes));
        System.out.println("routeList.size()"+routeList.size());
        Set<String> routeSets=new LinkedHashSet<>(routeList);
        routeList.clear();
        routeList.addAll(routeSets);


        if(polyline==null){

            if(routeList.size()>0){

                latLngList=DirectionFinder.decodePolyLine(routeList.get(0));
                final PolylineOptions polylineOptions=getDefaultPolyLines(latLngList);
                if(getActivity()!=null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        polyline = googleMap.addPolyline(polylineOptions);
                    }
                });


            }


        }else{

            final List<LatLng> points=polyline.getPoints();
            for (int i = 1; i <routeList.size() ; i++) {

                final List<LatLng> to = DirectionFinder.decodePolyLine(routeList.get(i));
                points.addAll(to);
                if(getActivity()!=null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        polyline.setPoints(points);
                    }
                });

            }
        }


/* /////////////////////////////////////////////////////////////////////
        for (String route:routeList) {
            latLngList.clear();
            latLngList = DirectionFinder.decodePolyLine(route);
            System.out.println("Routes "+route);
            final List<LatLng> finalLatLngList = latLngList;
            if(getActivity()!=null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PolylineOptions polylineOptions = getDefaultPolyLines(finalLatLngList,color);

                    polyline = googleMap.addPolyline(polylineOptions);
                }
            });



       ////////////////////////////////////////////////////////////////////  }*/



    }



    private void scheduleTimer() {
        timer = new Timer("update");
        Date executionDate = new Date(); // no params = now
        timer.scheduleAtFixedRate(timerTask, executionDate, 3000L);
        isTimerRunning=true;
    }

    private double getDistance(double lat1,double lon1,double lat2,double lon2) {

      /*  Location startLocation = new Location("start");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLon);
        Location positionLocation = new Location("position");
        positionLocation.setLatitude(positionLat);
        positionLocation.setLongitude(positionLon);
        System.out.println("Distance between " + startLocation.distanceTo(positionLocation));
        return    startLocation.distanceTo(positionLocation);*/


/*
        double latA = Math.toRadians(startLat);
        double lonA = Math.toRadians(startLon);
        double latB = Math.toRadians(positionLat);
        double lonB = Math.toRadians(positionLon);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist=ang *6371;
        return dist*1000 ;*/

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist*1000;


    }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private String formatNumber2Decimals(double number ){
        number=number/1000;


        return String.format(Locale.getDefault(), "%.2f", number) ;
    }

    private class UpdateAsyncTask extends AsyncTask<Void,Void,Void>{
  /*      WeakReference<LiveBetFragment> liveBetFragmentWeakReference;
        UpdateAsyncTask(LiveBetFragment liveBetFragment)
        {
            liveBetFragmentWeakReference = new WeakReference<>(liveBetFragment);
        }*/
        @Override
        protected Void doInBackground(Void... voids) {

            getUpdates();

            return null;
        }
    }
boolean actFlag;// to avoid starting of activity twice
    private void getUpdates() {
run=true;

//System.out.println("Formatted distance "+formatNumber2Decimals(userDistanceDouble));
        System.out.println("Formatted distance "+formatNumber2Decimals(Double.parseDouble(appPreference.getSavedDistance())));
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveDetailsUpdation(challengerId,appPreference.getSavedDistance(),positionLongitude,positionLatitude, appPreference.getPref(REG_KEY,""),betType, appPreference.getSavedUserRoute());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
                try {
run=false;
                    final String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("BET updation ==="+bodyString);
                     JSONObject jsonObject;

                        jsonObject = new JSONObject(bodyString);
                        String data = jsonObject.getString("Status");
                        if(data.equals("Ok")) {
                            CustomProgress.getInstance().hideProgress();
                            jsonObject = new JSONObject(bodyString);
                            String data1 = jsonObject.getString(WINNER_PARTICIPANT);
                            JSONArray jsonArray1 = new JSONArray(data1);
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                regNo = jsonObject2.getString(REG_KEY);
                                winName = jsonObject2.getString(FIRST_NAME);
                                //won=jsonObject2.getString(WON);

                            }
                            won = jsonObject.getString(MYBETS_credit);
                            JSONArray jsonArray = new JSONArray(data1);
                            String data2 = jsonObject.getString(BETDETAILS);
                            //JSONArray jsonArray2 = new JSONArray(data2);
                            JSONObject jsonObject3 = new JSONObject(data2);
                            betId = jsonObject3.getString(MYBETS_betid);
                            startLatitude = jsonObject3.getDouble("userstartlatitude");
                            startLongitude = jsonObject3.getDouble("userstartlongitude");
                            positionLatitude=jsonObject3.getDouble("positionlatitude");
                            positionLongitude=jsonObject3.getDouble("positionlongitude");

                            if (jsonArray.length() != 0) {
                                if (regNo.equals(appPreference.getPref(REG_KEY, ""))) {
                                    if (!regNo.equals("") && !winName.equals("") && !won.equals("") && !betId.equals("")) {
                                        if (winner&&!actFlag) {
                                            appPreference.savePref(BET_START_STATUS,"false");
                                            actFlag=true;
                                            stopLocationService(serviceIntent);
                                            clearSavedBetItems();
                                            cancelTimer();
                                            winner = false;
                                            counterHandler.removeCallbacks(counterRunnable);
                                            appPreference.savePref(Contents.UPDATE_METER, "0");
                                            if(getActivity()!=null) {
                                                Intent i = new Intent(getActivity(), WinnerActivity.class);
                                                i.putExtra(REG_KEY, regNo);
                                                i.putExtra(FIRST_NAME, winName);
                                                i.putExtra(MYBETS_betid, betId);
                                                i.putExtra(WON, won);
                                                startActivity(i);
                                                getActivity().finish();
                                            }

                                        }
                                    } else {
                                        Utils.showCustomToastMsg(getActivity(), R.string.imvalid_entry);
                                    }
                                } else {
                                    if (loser&&!actFlag) {
                                        appPreference.savePref(BET_START_STATUS,"false");
                                        actFlag=true;
                                        stopLocationService(serviceIntent);
                                        cancelTimer();
                                        clearSavedBetItems();
                                        loser = false;
                                        counterHandler.removeCallbacks(counterRunnable);
                                        appPreference.savePref(Contents.UPDATE_METER, "0");
                                        if(getActivity()!=null) {
                                            Intent i = new Intent(getActivity(), LoserActivity.class);
                                            i.putExtra(MYBETS_betid, betId);
                                            startActivity(i);
                                            getActivity().finish();
                                        }

                                    }
                                }
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    publishLiveDetails(bodyString);
                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(CustomProgress.getInstance().isShowing())
                CustomProgress.getInstance().hideProgress();
            }
        });

    }

    private void clearSavedBetItems() {
        appPreference.saveDistance("0.0");
        appPreference.savedStatusFlag(false);
        appPreference.saveUserRoute("");
        appPreference.saveOrigin("");
    }

    private void publishLiveDetails(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString(STATUS_A);
            if (status.trim().equals("Ok")) {

                final JSONObject jsonObject2 = new JSONObject( jsonObject.getString(BETDETAILS));

                JSONArray jsonArray = new JSONArray(jsonObject.getString(PARTICIPANT));
                String POSITION1="",FIRST_NAME1="";

                liveBetDetailsArrayList = new ArrayList<>();
                liveBetDetailsArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                    LiveBetDetails model = new LiveBetDetails();
                    model.setPosition(arrayObject.getString(POSITION));

                    model.setReg_key(arrayObject.getString(REG_KEY));

                    regNo=arrayObject.getString(REG_KEY);

                    model.setFirstname(arrayObject.getString(Contents.FIRST_NAME));

                    model.setEmail(arrayObject.getString(Contents.EMAIL));

                    model.setCreditScore(arrayObject.getString(Contents.CREDIT_SCORE));

                    model.setWon(arrayObject.getString(WON));

                    model.setLost(arrayObject.getString(Contents.LOST));

                    model.setCountry(arrayObject.getString(Contents.COUNTRY));

                    model.setProfile_pic(arrayObject.getString(PROFILE_PIC));

                    model.setImage_status(arrayObject.getString(IMAGE_STATUS));

                    model.setRegType(arrayObject.getString(REG_TYPE));

                    model.setDistance(arrayObject.getString(DISTANCE));

                    model.setStartdate(arrayObject.getString(Contents.START_DATE));

                    model.setPositionlongitude(arrayObject.getString(Contents.POSITION_LONGITUDE));

                    model.setPositionlatitude(arrayObject.getString(Contents.POSITION_LATITUDE));

                    liveBetDetailsArrayList.add(model);
                    if(regNo.equals(appPreference.getPref(REG_KEY,""))){
                        /*positionLatitude=arrayObject.getDouble(POSITION_LATITUDE);
                        positionLongitude=arrayObject.getDouble( POSITION_LONGITUDE);*/
                        POSITION1=arrayObject.getString(POSITION);
                        txtParticipate.setText(POSITION1);
                        if (!arrayObject.getString(PROFILE_PIC).equals("NA")) {
                            if (arrayObject.getString(REG_TYPE).equals("normal")&&arrayObject.getString(IMAGE_STATUS).equals("0")){

                                Picasso.get().
                                        load(Constant.BASE_APP_IMAGE__PATH+arrayObject.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(userImage);
                            }else{

                                Picasso.get().
                                        load(arrayObject.getString(PROFILE_PIC))
                                        .placeholder(R.drawable.image_loader)
                                        .into(userImage);
                            }
                        }/* else {
                            userImage.setImageDrawable(getActivity().getDrawable(R.drawable.user_profile_avatar));
                        }
*/
                        double totalDistance= (arrayObject.getDouble(DISTANCE));
                        if(userDistanceDouble==0.0){
                            userDistanceDouble=totalDistance;
                        }
                 /*       double finalDistance= totalDistance/1000;
                        final DecimalFormat f = new DecimalFormat("##.00");
                        txtTotalKm.setText(""+f.format(finalDistance)+" km");*/
                 System.out.println("formatNumber2Decimals(totalDistance) "+totalDistance+" "+formatNumber2Decimals(totalDistance));
                 txtTotalKm.setText(formatNumber2Decimals(totalDistance)+" km");



                        double remainingDistance=jsonObject2.getDouble(TOTAL_DISTANCE)-arrayObject.getDouble(DISTANCE);

             /*         DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
                        double decimal1= Double.parseDouble(String.valueOf(remainingDistance).replace("-",""));
                        String input1 = String.valueOf(decimal1).substring(0,5);
                        double numberAsString1= Double.parseDouble(input1);
                        txtRemainingKm.setText(""+decimalFormat1.format(numberAsString1/1000)+" km");*/
                        System.out.println("formatNumber2Decimals(remainingDistance)"+remainingDistance+" "+formatNumber2Decimals(remainingDistance));
                        txtRemainingKm.setText(formatNumber2Decimals(remainingDistance));

                    }
                }

                liveBetUserListAdapter = new LiveBetUserListAdapter(getActivity(), liveBetDetailsArrayList);
                betMembersRecyclerView.setHasFixedSize(true);
                betMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                betMembersRecyclerView.setAdapter(liveBetUserListAdapter);



            }
        }catch (Exception e){
e.printStackTrace();
        }

    }
Handler mHandler=new Handler();
/*
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("On Receive LIVE BET "+intent.getDoubleExtra("lat", 0.0));

            double lat = intent.getDoubleExtra("lat", 0.0);
            double lon = intent.getDoubleExtra("lon", 0.0);
            positionLatitude = lat;
            positionLongitude = lon;
            destination=""+lat+","+lon;

            Log.d("LocReceiver LIVE BET", " onReceive inside if: " + lat + "," + lon);

            if(!origin.equals(""))
                fetchDirections(origin,destination);
            origin=destination;

            onMapReady(googleMap);
        }
    };
*/


  /*  private int i=4;
    public static class LocReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("On Receive "+intent.getDoubleExtra("lat", 0.0));
            instance.i++;
            double lat = intent.getDoubleExtra("lat", 0.0);
            double lon = intent.getDoubleExtra("lon", 0.0);
            instance.positionLatitude = lat;
            instance.positionLongitude = lon;
            instance.destination=""+lat+","+lon;
          *//*  if (instance.i%5==0) {*//*
                Log.d("LocReceiver", instance.i+" onReceive inside if: " + lat + "," + lon);
                instance.i=0;
             if(!instance.origin.equals(""))
                instance.fetchDirections(instance.origin,instance.destination);
                instance.origin=instance.destination;

                    instance.onMapReady(instance.googleMap);


          *//*  }*//*


        }
    }
*/


}
