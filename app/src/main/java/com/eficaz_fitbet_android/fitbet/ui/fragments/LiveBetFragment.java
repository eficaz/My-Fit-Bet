package com.eficaz_fitbet_android.fitbet.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.customview.MyDialog;
import com.eficaz_fitbet_android.fitbet.interfaces.LocationReceiveListener;
import com.eficaz_fitbet_android.fitbet.model.LiveBetDetails;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.polyline.DirectionFinder;
import com.eficaz_fitbet_android.fitbet.polyline.DirectionFinderListener;
import com.eficaz_fitbet_android.fitbet.polyline.Route;
import com.eficaz_fitbet_android.fitbet.service.LocReceiver;
import com.eficaz_fitbet_android.fitbet.service.LocService;
import com.eficaz_fitbet_android.fitbet.ui.LoserActivity;
import com.eficaz_fitbet_android.fitbet.ui.WinnerActivity;
import com.eficaz_fitbet_android.fitbet.ui.adapters.LiveBetUserListAdapter;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.CircleImageView;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.SLApplication;
import com.eficaz_fitbet_android.fitbet.utils.Utils;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import static com.eficaz_fitbet_android.fitbet.polyline.GoogleMapHelper.buildCameraUpdate;
import static com.eficaz_fitbet_android.fitbet.polyline.GoogleMapHelper.getDefaultPolyLines;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.BETDETAILS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DISTANCE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.FIRST_NAME;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.IMAGE_STATUS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.LOCATION;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betid;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betname;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_bettype;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_challengerid;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_credit;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_route;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.PARTICIPANT;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.POSITION;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.POSITION_LATITUDE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.POSITION_LONGITUDE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.PROFILE_PIC;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_KEY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_TYPE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.START_DATE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.STATUS_A;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.TOTAL_DISTANCE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.USER_start_latitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.USER_start_longitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.WINNER_PARTICIPANT;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.WON;

public class LiveBetFragment extends Fragment implements OnMapReadyCallback , DirectionFinderListener, LocationReceiveListener {

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

    private Polyline polyline;
    private int REQUEST_CHECK_SETTINGS = 111;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager mLocationManager;
    private Handler counterHandler;
    private Runnable counterRunnable;
    private String origin="", destination="";
    private GoogleMap googleMap;
    private Double startLatitude = 0.0, startLongitude = 0.0, positionLatitude = 0.0, positionLongitude = 0.0;
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
private LocationReceiveListener locationReceiveListener;
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
        AppPreference.getPrefsHelper().savePref(Contents.BET_START_STATUS,"true");
        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION,"2");
      /*  _context.registerReceiver(broadcastReceiver,new IntentFilter("location_update"));*/
        locationReceiveListener=this;
        LocReceiver.registerLocationReceiveListener(locationReceiveListener);
     /*   instance = this;*/
        serviceIntent = new Intent(getActivity(), LocService.class);
        if (!SLApplication.isServiceRunning)
            startLocationService(serviceIntent);

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

    }
private boolean isTimerRunning,run=true;
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

        run=true;

    }

    @Override
    public void onStop() {
        System.out.println("Live bet inside onStop ");
        run=false;
        if (SLApplication.isServiceRunning) {
            System.out.println("Live bet stopping service ");
            stopLocationService(serviceIntent);
            new LocService().stopSelf();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        run=false;
       /* _context.unregisterReceiver(broadcastReceiver);*/
    }
    @Override
    public void onPause() {
        super.onPause();
        run=false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);

        if(positionMarker!=null) {
         // positionMarker.remove();
     positionMarker.setPosition(new LatLng(positionLatitude,positionLongitude));
            System.out.println("onMapReady drawing position marker");
        }


     googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(  positionLatitude,   positionLongitude), 18f));
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
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveBetDetails(AppPreference.getPrefsHelper().getPref(REG_KEY, ""));
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
                positionMarker=googleMap.addMarker(positionMarkerOptions.position(new LatLng(startLatitude,startLatitude)));
               startMarker= googleMap.addMarker(startMarkerOptions.position(new LatLng(startLatitude, startLongitude)));
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
                    locationRoute=betDetailsObject.getString("route");
                    // drawMapRout(jsonObject2.getString(MYBETS_startlatitude),jsonObject2.getString(MYBETS_startlongitude),jsonObject2.getString(MYBETS_endlatitude),jsonObject2.getString(MYBETS_endlongitude));
                }

                liveBetDetailsArrayList = new ArrayList<>();
                liveBetDetailsArrayList.clear();
                JSONArray jsonArray = new JSONArray(mainJsonObject.getString(PARTICIPANT));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                    LiveBetDetails model = new LiveBetDetails();
                    model.setPosition(arrayObject.getString(POSITION));
                    model.setReg_key(arrayObject.getString(REG_KEY));
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


                    if(arrayObject.getString("reg_key").equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
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
                        double finalDistance= totalDistance/1000;
                        final DecimalFormat f = new DecimalFormat("##.00");
                        txtTotalKm.setText(""+f.format(finalDistance)+" km");


                        double remainingDistance=betDetailsObject.getDouble(TOTAL_DISTANCE)-arrayObject.getDouble(DISTANCE);

                        DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
                        double decimal1= Double.parseDouble(String.valueOf(remainingDistance).replace("-",""));
                        String input1 = String.valueOf(decimal1).substring(0,5);
                        double numberAsString1= Double.parseDouble(input1);
                        txtRemainingKm.setText(""+decimalFormat1.format(numberAsString1/1000)+" km");

                    }



                    liveBetUserListAdapter = new LiveBetUserListAdapter(getActivity(), liveBetDetailsArrayList);
                    betMembersRecyclerView.setHasFixedSize(true);
                    betMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    betMembersRecyclerView.setAdapter(liveBetUserListAdapter);
                }


            }


        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
System.out.println("About  to call scheduleTimer ");
        scheduleTimer();
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

                    } else {

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


        if(userRoute.equals(""))
            userRoute=routes.get(0).pointString;
        else
            userRoute = userRoute + "fitbet" + routes.get(0).pointString;




    }



    private void startLocationService(Intent intent) {

       getActivity().startService(intent);
        SLApplication.isServiceRunning = true;
}

    private void stopLocationService(Intent intent) {
        getActivity().stopService(intent);
        SLApplication.isServiceRunning = false;
    }

    private void fetchDirections(String origin, String destination) {
        System.out.println("fetchDirections "+"origin "+origin+" , "+"destination "+destination);
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationReceived(Double lat, Double lon) {
        positionLatitude = lat;
        positionLongitude = lon;
        destination=""+lat+","+lon;

        Log.d("onLocationRecved LIVE", "loc : " + lat + "," + lon);


        if(!origin.equals(""))
            fetchDirections(origin,destination);
        origin=destination;

        onMapReady(googleMap);

    }

    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {

            if(run) {

                getUpdates();

            }

        }
    }

    private void drawPolyLine(@NotNull String userRoute) {
System.out.println("Calling draw polygon");
        List<LatLng> latLngList = new ArrayList<>();

        String[] splitRoutes = userRoute.split("fitbet");
        List<String> routeList= Arrays.asList(splitRoutes);
       System.out.println("routeList.size()"+routeList.size());

        for (String route:routeList) {
            latLngList.clear();
            latLngList = decodePolyLine(route);
            System.out.println("Routes "+route);
            final List<LatLng> finalLatLngList = latLngList;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PolylineOptions polylineOptions = getDefaultPolyLines(finalLatLngList);

                    polyline = googleMap.addPolyline(polylineOptions);
                }
            });



                }



    }

    private  List<LatLng> decodePolyLine(final String poly) {
        System.out.println("Inside decode polyline method= "+poly);
        List<LatLng> decoded = new ArrayList<>();
        try {
            int len = poly.length();
            int index = 0;
            int lat = 0;
            int lng = 0;

            while (index < len) {
                int b;
                int shift = 0;
                int result = 0;
                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                decoded.add(new LatLng(
                        lat / 100000d, lng / 100000d
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decoded;
    }

    private void scheduleTimer() {
        timer = new Timer("update");
        Date executionDate = new Date(); // no params = now
        timer.scheduleAtFixedRate(timerTask, executionDate, 5000L);
        isTimerRunning=true;
    }

    private String getDistance(Double startLat,Double startLon,Double positionLat,Double positionLon){

        Location startLocation=new Location("start");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLon);
        Location positionLocation=new Location("position");
        positionLocation.setLatitude(positionLat);
        positionLocation.setLongitude(positionLon);
        System.out.println("Distance between "+startLocation.distanceTo(positionLocation));
        float distance= startLocation.distanceTo(positionLocation);
        return String.format(Locale.getDefault(), "%.2f", distance);
    }

    private String formatNumber2Decimals(double number ){
        number=number/1000;


        return String.format(Locale.getDefault(), "%.2f", number) ;
    }

    private void getUpdates() {
userDistance=getDistance(startLatitude,startLongitude,positionLatitude,positionLongitude);

System.out.println("Formatted distance "+userDistance);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveDetailsUpdation(challengerId,userDistance,positionLongitude,positionLatitude, AppPreference.getPrefsHelper().getPref(REG_KEY,""),betType, userRoute);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("BET updation ==="+bodyString);
                     JSONObject jsonObject;

                        jsonObject = new JSONObject(bodyString);
                        String data = jsonObject.getString("Status");
                        if(data.equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                             jsonObject = new JSONObject(bodyString);
                            String data1 = jsonObject.getString(WINNER_PARTICIPANT);
                            JSONArray jsonArray1 =  new JSONArray(data1);
                            for(int i=0;i<jsonArray1.length();i++)
                            {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                regNo=jsonObject2.getString(REG_KEY);
                                winName=jsonObject2.getString(FIRST_NAME);
                                //won=jsonObject2.getString(WON);

                            }
                            won=jsonObject.getString(MYBETS_credit);
                            JSONArray jsonArray = new JSONArray(data1);
                            String data2 = jsonObject.getString(BETDETAILS);
                            //JSONArray jsonArray2 = new JSONArray(data2);
                            JSONObject jsonObject3 = new JSONObject(data2);
                            betId=jsonObject3.getString(MYBETS_betid);
                            startLatitude=jsonObject3.getDouble("userstartlatitude");
                            startLongitude=jsonObject3.getDouble("userstartlongitude");
                            if(jsonArray.length()!=0){
                                if(regNo.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                                    if(!regNo.equals("")&&!winName.equals("")&&!won.equals("")&&!betId.equals("")){
                                        if(winner){
                                            winner=false;
                                            counterHandler.removeCallbacks(counterRunnable);
                                            AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
                                            Intent i = new Intent(getActivity(), WinnerActivity.class);
                                            i.putExtra(REG_KEY,regNo);
                                            i.putExtra(FIRST_NAME,winName);
                                            i.putExtra(MYBETS_betid,betId);
                                            i.putExtra(WON,won);
                                            startActivity(i);
                                            stopLocationService(serviceIntent);
                                          cancelTimer();
                                        }
                                    }else{
                                        Utils.showCustomToastMsg(getActivity(), R.string.imvalid_entry);
                                    }
                                }else{
                                    if(loser){
                                        loser=false;
                                        counterHandler.removeCallbacks(counterRunnable);
                                        AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
                                        Intent i = new Intent(getActivity(), LoserActivity.class);
                                        i.putExtra(MYBETS_betid,betId);
                                        startActivity(i);
                                        stopLocationService(serviceIntent);
                                        timer.cancel();
                                    }
                                }
                            }
                            publishLiveDetails(bodyString);
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

    private void publishLiveDetails(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString(STATUS_A);
            if (status.trim().equals("Ok")) {

                final JSONObject jsonObject2 = new JSONObject( jsonObject.getString(BETDETAILS));

                JSONArray jsonArray = new JSONArray(jsonObject.getString(PARTICIPANT));
                String POSITION1="",FIRST_NAME1="";
                userRoute=jsonObject2.getString("userroute");


         /*       mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Inside mHandler run");

                    }
                });*/
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
                    if(regNo.equals(AppPreference.getPrefsHelper().getPref(REG_KEY,""))){
                        positionLatitude=arrayObject.getDouble(POSITION_LATITUDE);
                        positionLongitude=arrayObject.getDouble( POSITION_LONGITUDE);
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
                 /*       double finalDistance= totalDistance/1000;
                        final DecimalFormat f = new DecimalFormat("##.00");
                        txtTotalKm.setText(""+f.format(finalDistance)+" km");*/


                 txtTotalKm.setText(formatNumber2Decimals(totalDistance)+" km");



                        double remainingDistance=jsonObject2.getDouble(TOTAL_DISTANCE)-arrayObject.getDouble(DISTANCE);

             /*         DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
                        double decimal1= Double.parseDouble(String.valueOf(remainingDistance).replace("-",""));
                        String input1 = String.valueOf(decimal1).substring(0,5);
                        double numberAsString1= Double.parseDouble(input1);
                        txtRemainingKm.setText(""+decimalFormat1.format(numberAsString1/1000)+" km");*/

                        txtRemainingKm.setText(formatNumber2Decimals(remainingDistance));

                    }
                }

                liveBetUserListAdapter = new LiveBetUserListAdapter(getActivity(), liveBetDetailsArrayList);
                betMembersRecyclerView.setHasFixedSize(true);
                betMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                betMembersRecyclerView.setAdapter(liveBetUserListAdapter);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!userRoute.equals(""))
                            drawPolyLine(userRoute);

                    }
                });


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
