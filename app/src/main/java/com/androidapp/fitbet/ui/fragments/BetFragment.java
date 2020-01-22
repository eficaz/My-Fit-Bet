package com.androidapp.fitbet.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.interfaces.LocationReceiveListener;
import com.androidapp.fitbet.model.JoinBets;
import com.androidapp.fitbet.model.MyBets;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.service.LocReceiver;
import com.androidapp.fitbet.service.LocService;
import com.androidapp.fitbet.ui.ArchivesListActivity;
import com.androidapp.fitbet.ui.BetCreationActivity;
import com.androidapp.fitbet.ui.CreateGroupActivity;
import com.androidapp.fitbet.ui.CreditPurchaseActivity;
import com.androidapp.fitbet.ui.MessageListActivity;
import com.androidapp.fitbet.ui.adapters.JoinBetsDetailsListAdapter;
import com.androidapp.fitbet.ui.adapters.MyBetsDetailsListAdapter;
import com.androidapp.fitbet.ui.adapters.UpcommingBetsDetailsListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.androidapp.fitbet.utils.Contents.BET_PAGE_POSICTION;
import static com.androidapp.fitbet.utils.Contents.BET_START_STATUS;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_CREDIT_SCORE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_USERS;
import static com.androidapp.fitbet.utils.Contents.MYBETS;

public class BetFragment extends Fragment implements LocationReceiveListener {

    @Bind(R.id.bt_createGroup)
    LinearLayout bt_createGroup;

    @Bind(R.id.bt_createBet)
    LinearLayout bt_createBet;

    @Bind(R.id.bt_createArchives)
    Button bt_createArchives;

    @Bind(R.id.bt_joinbet)
    LinearLayout bt_joinbet;

    @Bind(R.id.messgae_row)
    TableRow messgae_row;


    @Bind(R.id.upcomming_bets)
    LinearLayout upcomming_bets;

    @Bind(R.id.my_bets)
    LinearLayout my_bets;

    @Bind(R.id.upcomming_bets_img)
    ImageView upcomming_bets_img;

    @Bind(R.id.my_bets_img)
    ImageView my_bets_img;

    @Bind(R.id.tv_my_bets)
    TextView tv_my_bets;

    @Bind(R.id.no_data)
    TextView no_data;

    @Bind(R.id.tv_upcomming_bets)
    TextView tv_upcomming_bets;

    @Bind(R.id.upcomming_bets_view_line)
    View upcomming_bets_view_line;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.searchview)
    EditText searchView;

    @Bind(R.id.my_bet_view)
    View my_bet_view;

    @Bind(R.id.upcomming_bets_imageView)
    ImageView upcomming_bets_imageView;

    @Bind(R.id.my_bets_imageView)
    ImageView my_bets_imageView;

    @Bind(R.id.bt_joinbet_imageView)
    ImageView bt_joinbet_imageView;

    @Bind(R.id.bt_createBet_imageView)
    ImageView bt_createBet_imageView;

    @Bind(R.id.bt_createGroup_imageView)
    ImageView bt_createGroup_imageView;

    ArrayList<MyBets> MyBetesDetails=null;
    ArrayList<JoinBets> joinDetails;

    MyBetsDetailsListAdapter  myBetsListAdapter;
    UpcommingBetsDetailsListAdapter upcommingBetsDetailsListAdapter;
    JoinBetsDetailsListAdapter joinBetsDetailsListAdapter;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    int tab_selection=0;
    String usetr_credit="0";

    private LocationManager mLocationManager;

    private int REQUEST_CHECK_SETTINGS=111;
    private AppPreference appPreference;
private LocationReceiveListener locationReceiveListener;
    @Override
    public void onDestroy() {
        if(!AppPreference.getPrefsHelper().getPref(BET_START_STATUS,"").equals("true")&&SLApplication.isServiceRunning)
            stopLocationService();

        super.onDestroy();
    }



    @Override
    public void onStop() {
        System.out.println("Bet inside onStop ");
        if(!AppPreference.getPrefsHelper().getPref(BET_START_STATUS,"").equals("true")&&SLApplication.isServiceRunning) {
            System.out.println("Bet stopping service ");
            stopLocationService();
        }
        super.onStop();
    }


    @Override
    public void onPause() {
        super.onPause();

    }


    private Context _context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    }

    private void stopLocationService() {
        if(SLApplication.isServiceRunning) {
            Intent intent = new Intent(getActivity(), LocService.class);
            getActivity().stopService(intent);
            new LocService().stopSelf();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onViewCreated BetFragment");
        appPreference=      AppPreference.getPrefsHelper(getActivity());
       appPreference.savePref(DASH_BOARD_POSICTION,"1");
        locationReceiveListener=this;
    LocReceiver.registerLocationReceiveListener(locationReceiveListener);
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            if (!SLApplication.isServiceRunning) {
                //name.setText(R.string.msg_location_service_started);
                System.out.println("Bet frag startLocationService");
                startLocationService();

                //Ends................................................
            }
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
;

        intintView();
    }
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
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    private void startLocationService() {
        if(!SLApplication.isServiceRunning) {
            Intent intent = new Intent(getActivity(), LocService.class);
            getActivity().startService(intent);

        }
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


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(Utils.getLocationRequest());

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
    private void intintView() {
        callDashboardDetailsApi();
    /*    upcomming_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_active_icon));
        my_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_inactive_icon));*/
        tv_my_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.gray_1));
        tv_upcomming_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.light_blue));
        upcomming_bets_view_line.setBackgroundResource(R.color.light_blue);
        my_bet_view.setBackgroundResource(R.color.gray_1);
        //callUpcommingBetsAPi();
        upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon_active));
        my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
        bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
        bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
        bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
        tab_selection=0;
        bt_createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
                my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
                bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
                bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon_active));

                startActivity(new Intent(getActivity(), CreateGroupActivity.class));

                searchView.setFocusable(false);
                searchView.setClickable(false);

            }
        });
        bt_createBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                    Log.e("fitbet","Gps not enabled");
                    createLocationRequest();
                }else{
                    if(usetr_credit.equals("0")){
                        Utils.showCustomToastMsg(getActivity(), R.string.you_don_have_credit);
                        Intent i = new Intent(getActivity(), CreditPurchaseActivity.class);
                        startActivity(i);
                    }else{
                        searchView.setFocusable(false);
                        searchView.setClickable(false);
                        upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
                        my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
                        bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
                        bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                        bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));

                        //startActivity(new Intent(getActivity(), BetCreationActivity.class));
                       appPreference.savePref(Contents.MYBETS_betname, "");
                       appPreference.savePref(Contents.CREDIT_SCORE, "");
                       appPreference.savePref(Contents.START_DATE, "");
                       appPreference.savePref(Contents.MYBETS_enddate, "");
                       appPreference.savePref(Contents.MYBETS_description,  "");
                        Intent i = new Intent(getActivity(), BetCreationActivity.class);
                        i.putExtra(Contents.pass_startlatitude,"0");
                        i.putExtra(Contents.pass_startlongitude,"0");
                        i.putExtra(Contents.pass_endlatitude,"0");
                        i.putExtra(Contents.pass_endlongitude,"0");
                        i.putExtra(Contents.MYBETS_distance,"0");
                        i.putExtra(Contents.START_Address, "0");
                        i.putExtra(Contents.END_Address, "0");
                        startActivity(i);
                    }
                }

            }
        });
        upcomming_bets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setFocusable(false);
                searchView.setClickable(false);
                upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon_active));
                my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
                bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
                bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));

       /*         upcomming_bets_img.setBackground(getResources().getDrawable(R.drawable.betJ));
                my_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_inactive_icon));*/
                tv_my_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.gray_1));
                tv_upcomming_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.light_blue));
                upcomming_bets_view_line.setBackgroundResource(R.color.light_blue);
                my_bet_view.setBackgroundResource(R.color.gray_1);
                searchView.setText("");
                callUpcommingBetsAPi();
                tab_selection=0;

            }
        });
        my_bets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
AppPreference.getPrefsHelper().savePref(BET_PAGE_POSICTION,"1");
                searchView.setFocusable(false);
                searchView.setClickable(false);
                upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
                my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon_active));
                bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
                bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
/*
                upcomming_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_inactive_icon));
                my_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_active_icon));*/
                tv_my_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.light_blue));
                tv_upcomming_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.gray_1));
                upcomming_bets_view_line.setBackgroundResource(R.color.gray_1);
                my_bet_view.setBackgroundResource(R.color.light_blue);
                searchView.setText("");
                callmyBetsApi();
                tab_selection=1;

            }
        });
        searchView.setFocusable(false);
        searchView.setClickable(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setFocusableInTouchMode(true);
                searchView.setFocusable(true);
                searchView.requestFocus();
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                try{
                    if(editable.toString().equals("")){
                        filter(" ");
                    }else{
                        filter(editable.toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
        bt_joinbet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                    Log.e("fitbet bt_joinbet","Gps not enabled");
             createLocationRequest();
                }else{
                   appPreference.savePref(BET_PAGE_POSICTION,"2");
                    searchView.setFocusable(false);
                    searchView.setClickable(false);
                    searchView.setText("");
                    upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
                    my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
                    bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon_active));
                    bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                    bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
                    callJoinBetsAPi();

                }
            }
        });
        bt_createArchives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ArchivesListActivity.class);
                startActivity(i);
            }
        });

        messgae_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MessageListActivity.class);
                i.putExtra(Contents.MYBETS_betid,"");
                startActivity(i);
            }
        });

        if(AppPreference.getPrefsHelper().getPref(Contents.BET_PAGE_POSICTION,"").equals("0")){
            searchView.setFocusable(false);
            searchView.setClickable(false);
            upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon_active));
            my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
            bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
            bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
            bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));

        /*    upcomming_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_active_icon));
            my_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_inactive_icon));*/
            tv_my_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.gray_1));
            tv_upcomming_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.light_blue));
            upcomming_bets_view_line.setBackgroundResource(R.color.light_blue);
            my_bet_view.setBackgroundResource(R.color.gray_1);
            searchView.setText("");
            callUpcommingBetsAPi();
            tab_selection=0;
            //CustomProgress.getInstance().showProgress(getActivity(), "", false);
        }
        else if(AppPreference.getPrefsHelper().getPref(Contents.BET_PAGE_POSICTION,"").equals("1")){
            searchView.setFocusable(false);
            searchView.setClickable(false);
            upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
            my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon_active));
            bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
            bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
            bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
          /*  upcomming_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_inactive_icon));
            my_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_active_icon));*/
            tv_my_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.light_blue));
            tv_upcomming_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.gray_1));
            upcomming_bets_view_line.setBackgroundResource(R.color.gray_1);
            my_bet_view.setBackgroundResource(R.color.light_blue);
            searchView.setText("");
            callmyBetsApi();
            tab_selection=1;
            //CustomProgress.getInstance().showProgress(getActivity(), "", false);
        }
        else if(AppPreference.getPrefsHelper().getPref(Contents.BET_PAGE_POSICTION,"").equals("2")){
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                Log.e("fitbet mLocationManager","Gps not enabled");
              createLocationRequest();
            }else{
                searchView.setFocusable(false);
                searchView.setClickable(false);
                searchView.setText("");
                upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
                my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
                bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon_active));
                bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
                callJoinBetsAPi();
               // CustomProgress.getInstance().showProgress(getActivity(), "", false);
            }
        }
        else if(AppPreference.getPrefsHelper().getPref(Contents.BET_PAGE_POSICTION,"").equals("3")){
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
                Log.e("fitbet mLocationM","Gps not enabled");
       createLocationRequest();
            }else{
                if(usetr_credit.equals("0")){
                    Utils.showCustomToastMsg(getActivity(), R.string.you_don_have_credit);
                    Intent i = new Intent(getActivity(), CreditPurchaseActivity.class);
                    startActivity(i);
                }else{
                    searchView.setFocusable(false);
                    searchView.setClickable(false);
                    upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
                    my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
                    bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
                    bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
                    bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
                    //startActivity(new Intent(getActivity(), BetCreationActivity.class));
                   appPreference.savePref(Contents.MYBETS_betname, "");
                   appPreference.savePref(Contents.CREDIT_SCORE, "");
                   appPreference.savePref(Contents.START_DATE, "");
                   appPreference.savePref(Contents.MYBETS_enddate, "");
                   appPreference.savePref(Contents.MYBETS_description,  "");
                    Intent i = new Intent(getActivity(), BetCreationActivity.class);
                    i.putExtra(Contents.pass_startlatitude,"0");
                    i.putExtra(Contents.pass_startlongitude,"0");
                    i.putExtra(Contents.pass_endlatitude,"0");
                    i.putExtra(Contents.pass_endlongitude,"0");
                    i.putExtra(Contents.MYBETS_distance,"0");
                    i.putExtra(Contents.START_Address, "0");
                    i.putExtra(Contents.END_Address, "0");
                    startActivity(i);
                }
            }
        } else if(AppPreference.getPrefsHelper().getPref(Contents.BET_PAGE_POSICTION,"").equals("4")){
            upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
            my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon));
            bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon_active));
            bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
            bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
            startActivity(new Intent(getActivity(), CreateGroupActivity.class));
            getActivity().finish();
            searchView.setFocusable(false);
            searchView.setClickable(false);
        }
    }
    private void callDashboardDetailsApi() {
        if(!CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().showProgress(getActivity(), "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardDetails(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("callDashboardDetailsApi "+bodyString);
                    DashboardDetailReportapiresult(bodyString);
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
    private void DashboardDetailReportapiresult(String bodyString) {

        /*if(CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().hideProgress();*/

        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                String data1 = jsonObject.getString(DASH_BOARD_USERS);
                JSONObject jsonObject1 = new JSONObject(data1);
                usetr_credit=jsonObject1.getString(DASH_BOARD_CREDIT_SCORE);


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void callJoinBetsAPi() {
        if(!CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().showProgress(getActivity(), "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).JoinBetList(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    ResultJoinBets(bodyString);
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
    @Override
    public void onResume() {
        super.onResume();
        if (!SLApplication.isServiceRunning)
            startLocationService();
        if(AppPreference.getPrefsHelper().getPref(Contents.CREATE_BET_STATUS,"").equals("true")) {
            upcomming_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.join_bet_icon));
            my_bets_imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_bet_icon_active));
            bt_joinbet_imageView .setImageDrawable(getResources().getDrawable(R.drawable.up_coming_bet_icon));
            bt_createBet_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_bet));
            bt_createGroup_imageView.setImageDrawable(getResources().getDrawable(R.drawable.create_group_icon));
/*            upcomming_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_inactive_icon));
            my_bets_img.setBackground(getResources().getDrawable(R.drawable.bet_active_icon));*/
            tv_my_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.light_blue));
            tv_upcomming_bets.setTextColor(ContextCompat.getColorStateList(getActivity(), R.color.gray_1));
            upcomming_bets_view_line.setBackgroundResource(R.color.gray_1);
            my_bet_view.setBackgroundResource(R.color.light_blue);
            searchView.setText("");
            callmyBetsApi();
            tab_selection=1;
            //CustomProgress.getInstance().showProgress(getActivity(), "", false);
           appPreference.savePref(Contents.CREATE_BET_STATUS, "false");
        }else{

            switch (AppPreference.getPrefsHelper().getPref(BET_PAGE_POSICTION,"")){

                case "0":

                    upcomming_bets.performClick();
                    break;
                case "1":
                    if(SLApplication.isBetCreatedOrEdited)
                    my_bets.performClick();
                    break;
                case "3":
                    bt_joinbet.performClick();
                    break;

            }

        }



    }

    private void filter(String text) {
        if(tab_selection==0){

           if(MyBetesDetails!=null)
           if(!text.equals("")){
               upcommingBetsDetailsListAdapter.filterList(text);
           }else{
               upcommingBetsDetailsListAdapter.filterList(" ");
           }


        }else if(tab_selection==1){
            myBetsListAdapter.filterList(text);
        }else{
            joinBetsDetailsListAdapter.filterList(text);
        }
        CustomProgress.getInstance().hideProgress();
    }
    private void callmyBetsApi() {
        if(!CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().showProgress(getActivity(), "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).MyBets(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),"");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("callmyBetsApi === "+bodyString);
                    createMybets(bodyString);

                } catch (Exception e) {
                    CustomProgress.getInstance().hideProgress();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void createMybets(String bodyString) {
        try{
            JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if(data.equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS);
                JSONArray jsonArray = new JSONArray(data1);
                if (jsonArray.length() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);

                    MyBetesDetails = new ArrayList<>();
                    MyBetesDetails.clear();
                    joinDetails = new ArrayList<>();
                    joinDetails.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        MyBets model = new MyBets();
                        model.setBetid(jsonList.getString(Contents.MYBETS_betid));
                        model.setBetname(jsonList.getString(Contents.MYBETS_betname));
                        model.setDescription(jsonList.getString(Contents.MYBETS_description));
                        model.setDate(jsonList.getString(Contents.MYBETS_date));
                        model.setTotal(jsonList.getString(Contents.TOTAL));
                        model.setEnddate(jsonList.getString(Contents.MYBETS_enddate));
                        model.setDistance(jsonList.getString(Contents.MYBETS_distance));
                        model.setStartlocation(jsonList.getString(Contents.MYBETS_startlocation));
                        model.setEndlocation(jsonList.getString(Contents.MYBETS_endlocation));
                        model.setStartlongitude(jsonList.getString(Contents.MYBETS_startlongitude));
                        model.setEndlongitude(jsonList.getString(Contents.MYBETS_endlongitude));
                        model.setStartlatitude(jsonList.getString(Contents.MYBETS_startlatitude));
                        model.setEndlatitude(jsonList.getString(Contents.MYBETS_endlatitude));
                        model.setRoute(jsonList.getString(Contents.MYBETS_route));
                        model.setCredit(jsonList.getString(Contents.MYBETS_credit));
                        model.setWinner(jsonList.getString(Contents.MYBETS_winner));
                        model.setStatus(jsonList.getString(Contents.MYBETS_status));
                        model.setCreatedate(jsonList.getString(Contents.MYBETS_createdate));
                        model.setCreatedby(jsonList.getString(Contents.MYBETS_createdby));
                        model.setBettype(jsonList.getString(Contents.MYBETS_bettype));
                        model.setChallengerid(jsonList.getString(Contents.MYBETS_challengerid));
                        model.setStarted(jsonList.getString(Contents.MYBETS_started));
                        model.setEditstatus(jsonList.getString(Contents.MYBETS_editstatus));
                        MyBetesDetails.add(model);
                    }
                    myBetsListAdapter = new MyBetsDetailsListAdapter(getActivity(), MyBetesDetails, bodyString);
                    list.setHasFixedSize(true);
                    list.setLayoutManager(new LinearLayoutManager(getActivity()));
                    list.setAdapter(myBetsListAdapter);
                }
            }
            if(CustomProgress.getInstance().isShowing())
                CustomProgress.getInstance().hideProgress();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callUpcommingBetsAPi() {
        if(!CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().showProgress(getActivity(), "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UpcommingBet(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),"");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    ResultUpcommingBets(bodyString);

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


    private void ResultJoinBets(String bodyString) {
        try{
            JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if(data.equals("Ok")){
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS);
                JSONArray jsonArray = new JSONArray(data1);
                if(jsonArray.length()==0){
                    no_data.setVisibility(View.VISIBLE);
                }else{
                    no_data.setVisibility(View.GONE);
                }
                MyBetesDetails = new ArrayList<>();
                MyBetesDetails.clear();
                joinDetails = new ArrayList<>();
                joinDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    JoinBets model = new JoinBets();
                    model.setDistance(jsonList.getString(Contents.MYBETS_distance));
                    model.setDescription(jsonList.getString(Contents.MYBETS_description));
                    model.setCreatedate(jsonList.getString(Contents.MYBETS_createdate));
                    model.setRoute(jsonList.getString(Contents.MYBETS_route));
                    model.setCredit(jsonList.getString(Contents.MYBETS_credit));
                    model.setWinner(jsonList.getString(Contents.MYBETS_winner));
                    model.setStatus(jsonList.getString(Contents.MYBETS_status));
                    model.setCreatedby(jsonList.getString(Contents.MYBETS_createdby));
                    model.setDate(jsonList.getString(Contents.MYBETS_date));
                    model.setBetid(jsonList.getString(Contents.MYBETS_betid));
                    model.setBetname(jsonList.getString(Contents.MYBETS_betname));
                    model.setStartlocation(jsonList.getString(Contents.MYBETS_startlocation));
                    model.setEndlocation(jsonList.getString(Contents.MYBETS_endlocation));
                    model.setStartlongitude(jsonList.getString(Contents.MYBETS_startlongitude));
                    model.setEndlongitude(jsonList.getString(Contents.MYBETS_endlongitude));
                    model.setStartlatitude(jsonList.getString(Contents.MYBETS_startlatitude));
                    model.setEndlatitude(jsonList.getString(Contents.MYBETS_endlatitude));
                    model.setBettype(jsonList.getString(Contents.MYBETS_bettype));
                    model.setEnddate(jsonList.getString(Contents.MYBETS_enddate));
                    model.setChallengerid(jsonList.getString(Contents.MYBETS_challengerid));
                    model.setStarted(jsonList.getString(Contents.MYBETS_started));
                    model.setTotal(jsonList.getString(Contents.TOTAL));
                    model.setEditstatus(jsonList.getString(Contents.MYBETS_editstatus));
                    joinDetails.add(model);
                }
                joinBetsDetailsListAdapter = new JoinBetsDetailsListAdapter(getActivity(), joinDetails);
                list.setHasFixedSize(true);
                list.setLayoutManager(new LinearLayoutManager(getActivity()));
                list.setAdapter(joinBetsDetailsListAdapter);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void ResultUpcommingBets(String bodyString) {
        try{
            System.out.println("Upcoming bets == "+bodyString);
            JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if(data.equals("Ok")){
                if(CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS);
                JSONArray jsonArray = new JSONArray(data1);

                if(jsonArray.length()==0){
                    no_data.setVisibility(View.VISIBLE);
                }else{
                    no_data.setVisibility(View.GONE);
                }
                MyBetesDetails = new ArrayList<>();
                MyBetesDetails.clear();
                joinDetails = new ArrayList<>();
                joinDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    MyBets model = new MyBets();
                    model.setBetid(jsonList.getString(Contents.MYBETS_betid));
                    model.setBetname(jsonList.getString(Contents.MYBETS_betname));
                    model.setDescription(jsonList.getString(Contents.MYBETS_description));
                    model.setDate(jsonList.getString(Contents.MYBETS_date));
                    model.setEnddate(jsonList.getString(Contents.MYBETS_enddate));
                    model.setDistance(jsonList.getString(Contents.MYBETS_distance));
                    model.setStartlocation(jsonList.getString(Contents.MYBETS_startlocation));
                    model.setEndlocation(jsonList.getString(Contents.MYBETS_endlocation));
                    model.setStartlongitude(jsonList.getString(Contents.MYBETS_startlongitude));
                    model.setEndlongitude(jsonList.getString(Contents.MYBETS_endlongitude));
                    model.setStartlatitude(jsonList.getString(Contents.MYBETS_startlatitude));
                    model.setEndlatitude(jsonList.getString(Contents.MYBETS_endlatitude));
                    model.setRoute(jsonList.getString(Contents.MYBETS_route));
                    model.setCredit(jsonList.getString(Contents.MYBETS_credit));
                    model.setWinner(jsonList.getString(Contents.MYBETS_winner));
                    model.setStatus(jsonList.getString(Contents.MYBETS_status));
                    model.setCreatedate(jsonList.getString(Contents.MYBETS_createdate));
                    model.setCreatedby(jsonList.getString(Contents.MYBETS_createdby));
                    model.setBettype(jsonList.getString(Contents.MYBETS_bettype));
                    model.setChallengerid(jsonList.getString(Contents.MYBETS_challengerid));
                    model.setStarted(jsonList.getString(Contents.MYBETS_started));
                    model.setEditstatus(jsonList.getString(Contents.MYBETS_editstatus));
                    model.setTotal(jsonList.getString(Contents.TOTAL));
                    MyBetesDetails.add(model);
                }
                upcommingBetsDetailsListAdapter = new UpcommingBetsDetailsListAdapter(getActivity(), MyBetesDetails);
                list.setHasFixedSize(true);
                list.setLayoutManager(new LinearLayoutManager(getActivity()));
                list.setAdapter(upcommingBetsDetailsListAdapter);
            }else{
                String msg = jsonObject.getString("Msg");
                Utils.showCustomToastMsg(getActivity(), msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.bet_fragment_layout,container,false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onLocationReceived(Double lat, Double lon) {
        System.out.println("onLocationReceived bet "+lat+","+lon);

       appPreference.savePref(Contents.FOR_START_BET_LAT,""+lat);

       appPreference.savePref(Contents.FOR_START_BET_LOG,""+lon);

    }
}
