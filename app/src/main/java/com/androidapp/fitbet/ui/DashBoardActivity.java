package com.androidapp.fitbet.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.interfaces.CameraGalaryCaputer;
import com.androidapp.fitbet.model.CommonUsage;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.presenter.DashBoardPresenter;
import com.androidapp.fitbet.ui.fragments.ArchivesListFragment;
import com.androidapp.fitbet.ui.fragments.BetFragment;
import com.androidapp.fitbet.ui.fragments.DashBoardFragment;
import com.androidapp.fitbet.ui.fragments.LiveBetFragment;
import com.androidapp.fitbet.ui.fragments.SettingsFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CACHEDKEY;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.androidapp.fitbet.utils.Contents.BET_START_STATUS;
import static com.androidapp.fitbet.utils.Contents.CAMERA_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.GALLERY_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.USER_start_latitude;
import static com.androidapp.fitbet.utils.Contents.USER_start_longitude;

public class DashBoardActivity extends BaseActivity implements CommonUsage, BottomNavigationView.OnNavigationItemSelectedListener,MyDialog.MyDialogClickListener {

/*    @Bind(R.id.tabs)
    TabLayout tabLayout;*/
@Bind(R.id.content_main)
FrameLayout content_main;

@Bind(R.id.navigationView)
    BottomNavigationView mBottomNavigationView;

@Bind(R.id.fab)
ImageView mFab;

BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    final static int REQUEST_LOCATION = 199;

private int flag=0;

     LocationManager manager;
    AlertDialog.Builder builder;

    DashBoardPresenter dashBoardPresenter;

    boolean live_bet=false;


    CameraGalaryCaputer cameraGalaryCaputer;
    private MyDialog noInternetDialog;
    private int REQUEST_CHECK_SETTINGS=111;
private AppPreference appPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sactivity_dashboard);
        ButterKnife.bind(this);
        appPreference=AppPreference.getPrefsHelper(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        dashBoardPresenter = new DashBoardPresenter(this);
        mOnNavigationItemSelectedListener=this;
mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
disableShiftMode(mBottomNavigationView);
mBottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        noInternetDialog=new MyDialog(this,null,getString(R.string.no_internet),getString(R.string.no_internet_message),getString(R.string.ok),"",true,"internet");

        if(Utils.isConnectedToInternet(this)) {
            callLiveBetApi();}else
        if(!noInternetDialog.isShowing())
            noInternetDialog.show();


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!appPreference.getPref(DASH_BOARD_POSICTION,"").equals("2")) {
                    live_bet = true;
                    mFab.setImageResource(R.drawable.tab_center_icon_active1);
                    if (Utils.isConnectedToInternet(DashBoardActivity.this)) {
                        mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                        mBottomNavigationView.getMenu().getItem(1).setChecked(false);
                        mBottomNavigationView.getMenu().getItem(3).setChecked(false);
                        mBottomNavigationView.getMenu().getItem(4).setChecked(false);
                        mBottomNavigationView.getMenu().setGroupCheckable(0, false, true);

                        callLiveBetApi();
                    } else if (!noInternetDialog.isShowing())
                        noInternetDialog.show();
                }
            }
        });

    }

public void setImageToFab(){

    mFab.setImageResource(R.drawable.tab_center_icon_active1);
    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
    mBottomNavigationView.getMenu().getItem(1).setChecked(false);
    mBottomNavigationView.getMenu().getItem(3).setChecked(false);
    mBottomNavigationView.getMenu().getItem(4).setChecked(false);
    mBottomNavigationView.getMenu().setGroupCheckable(0, false, true);

}


    public void hideBottomNavigationView(){
        mBottomNavigationView.setVisibility(GONE);
        mFab.setVisibility(GONE);


    }
    public void showBottomNavigationView(){
        mBottomNavigationView.setVisibility(View.VISIBLE);
        mFab.setVisibility(View.VISIBLE);
    }
    private void init() {


            switch (appPreference.getPref(Contents.DASH_BOARD_POSICTION, "")) {


                case "0":
                    if (CustomProgress.getInstance().isShowing())
                        CustomProgress.getInstance().hideProgress();
                    // if(mBottomNavigationView.getSelectedItemId()!=R.id.navigation_me)
                    mBottomNavigationView.setSelectedItemId(R.id.navigation_me);


                    break;
                case "1":
                    if (CustomProgress.getInstance().isShowing())
                        CustomProgress.getInstance().hideProgress();
                    if (mBottomNavigationView.getSelectedItemId() != R.id.navigation_bet)
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_bet);

                    break;

                case "3":
                    if (CustomProgress.getInstance().isShowing())
                        CustomProgress.getInstance().hideProgress();
                    if (mBottomNavigationView.getSelectedItemId() != R.id.navigation_archive)
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_archive);

                    break;
                case "4":
                    if (CustomProgress.getInstance().isShowing())
                        CustomProgress.getInstance().hideProgress();
                    if (mBottomNavigationView.getSelectedItemId() != R.id.navigation_settings)
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_settings);

                    break;

        }



    }
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShifting(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            //Timber.e(e, "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            //Timber.e(e, "Unable to change value of shift mode");
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




    private void callLiveBetApi() {
        CustomProgress.getInstance().showProgress(DashBoardActivity.this, "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LiveBetDetails(appPreference.getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("callLiveBetApi dashboard"+bodyString);
                    publishLiveDetails(bodyString);

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

    public void setLive_bet(boolean live_bet) {
        this.live_bet = live_bet;
    }

    private void publishLiveDetails(String bodyString) {

        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString(STATUS_A);

            if (status.trim().equals("Ok")) {
               if(!appPreference.getSavedStatusFlag()) {
                    JSONObject betDetailsObject = jsonObject.getJSONObject("betdetails");
                    appPreference.savePref(USER_start_latitude, betDetailsObject.getString(USER_start_latitude));
                    appPreference.savePref(USER_start_longitude, betDetailsObject.getString(USER_start_longitude));
                    appPreference.saveOrigin(betDetailsObject.getString(USER_start_latitude) + "," + betDetailsObject.getString(USER_start_longitude));
                    appPreference.savedStatusFlag(true);
                }
                System.out.println("inside  ok");
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(DashBoardActivity.this)) {

                            createLocationRequest();
                            flag=1;
                        }else{


                            mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                            mBottomNavigationView.getMenu().getItem(1).setChecked(false);
                            mBottomNavigationView.getMenu().getItem(3).setChecked(false);
                            mBottomNavigationView.getMenu().getItem(4).setChecked(false);
                            mBottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                            mFab.setImageResource(R.drawable.tab_center_icon_active1);
                            if(CustomProgress.getInstance().isShowing())
                                CustomProgress.getInstance().hideProgress();
                            appPreference.savePref(BET_START_STATUS,"true");
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new LiveBetFragment(),Utils.BetScreenName).commitAllowingStateLoss();

                        }



            }else{
                if(CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
                String message=jsonObject.getString("Msg");
                //callJoinBetsAPi();
                System.out.println("inside error - not ok");
                if(live_bet){
                MyDialog myDialog=new MyDialog(this,this,"",message,getString(R.string.ok),"",false,"livebet");
                myDialog.show();
                live_bet=false;
                }else{
                    init();
                    clearSavedBetItems();
                }



            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void clearSavedBetItems() {
        appPreference.saveDistance("0.0");
        appPreference.savedStatusFlag(false);
        appPreference.saveUserRoute("");
        appPreference.saveOrigin("");
    }


    @Override
    public <T> void onSubmit(T value1, int type) {
        if (CACHEDKEY.CANCEL.ordinal() == type) {
        }
        if (CACHEDKEY.CAMERA.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(DashBoardActivity.this,CAMERA_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
            }
        }
        if (CACHEDKEY.GALLERY.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openGallery(DashBoardActivity.this,GALLERY_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        }
    }




    public void passVal(CameraGalaryCaputer cameraGalaryCaputer) {
        this.cameraGalaryCaputer = cameraGalaryCaputer;

    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this,new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFile, EasyImage.ImageSource source, int type) {
                cameraGalaryCaputer.requestSuccess(new File(imageFile.toString().replaceAll("[\\[\\]]","")));
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });

    }
private int currentTabSelected;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment=null;
        mBottomNavigationView.getMenu().setGroupCheckable(0, true, true);
        switch (menuItem.getItemId()){
            case R.id.navigation_me:
                if(currentTabSelected==R.id.navigation_me)
                    return true;

                menuItem.setChecked(true);
                mFab.setImageResource(R.drawable.tab_center_icon1);
                fragment=new DashBoardFragment();

                break;
            case R.id.navigation_bet:
                if(currentTabSelected==R.id.navigation_bet)
                    return true;
                menuItem.setChecked(true);
                mFab.setImageResource(R.drawable.tab_center_icon1);
       fragment=new BetFragment();
                break;
            case R.id.navigation_archive:
                if(currentTabSelected==R.id.navigation_archive)
                    return true;
                menuItem.setChecked(true);
                mFab.setImageResource(R.drawable.tab_center_icon1);
        fragment=new ArchivesListFragment();
                break;
            case R.id.navigation_settings:
                if(currentTabSelected==R.id.navigation_settings)
                    return true;
                menuItem.setChecked(true);
                mFab.setImageResource(R.drawable.tab_center_icon1);
             fragment=new SettingsFragment();
                break;
        }
        currentTabSelected=menuItem.getItemId();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment,Utils.BetScreenName).commitAllowingStateLoss();
        return true;
    }


    protected void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                Log.d("Location","High accuracy location enabled");
               // Toast.makeText(DashBoardActivity.this, "addOnSuccessListener", Toast.LENGTH_SHORT).show();
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                if(flag==1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new LiveBetFragment(),Utils.BetScreenName).commitAllowingStateLoss();
                    flag=0;
                }

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(DashBoardActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onClick(MyDialog dialog, int type, String flag) {

        switch (flag){
            case "livebet":
                mFab.setImageResource(R.drawable.tab_center_icon1);
                mBottomNavigationView.setSelectedItemId(R.id.navigation_bet);

                break;
        }

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Utils.showCustomToastMsg(this,"Please click BACK again to exit");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
