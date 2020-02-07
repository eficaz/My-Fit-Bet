package com.androidapp.fitbet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.forceupdate.ForceUpdateChecker;
import com.androidapp.fitbet.ui.BaseActivity;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.androidapp.fitbet.utils.Utils.permissionStatus;

public class SplashActivity extends BaseActivity implements ForceUpdateChecker.OnUpdateNeededListener, MyDialog.MyDialogClickListener {
    private final int SPLASH_TIME=1000;
    private Handler handler=new Handler();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";
private MyDialog.MyDialogClickListener mMyDialogClickListener;
    private String updateUrl;
    private MyDialog noInternetDialog=null;
    //https://medium.com/@sembozdemir/force-your-users-to-update-your-app-with-using-firebase-33f1e0bcec5a

    @Override
    public void onMessageReceived(String message) {

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        //mClassName=LoginActivity.class;
        permissionStatus = true;
        Utils.permissionDeclineStatus = false;
        mMyDialogClickListener=this;
        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "0");
        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
        AppPreference.getPrefsHelper().savePref(Contents.START_STATUS, "true");
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        noInternetDialog=new MyDialog(this,null,getString(R.string.no_internet),getString(R.string.no_internet_message),getString(R.string.ok),"",true,"internet");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkAndRequestPermissions()) {
                if (Utils.isConnectedToInternet(SplashActivity.this)){
                    startActivity();
                } else{
                    noInternetDialog.show();
                }

            }
        }
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                       // String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("----------------TAG", token);
                        //Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }
    @Override
    public void onUpdateNeeded(final String updateUrl) {

        this.updateUrl=updateUrl;
        MyDialog myDialog=new MyDialog(this,mMyDialogClickListener,getString(R.string.new_version),getString(R.string.update_app),getString(R.string.update),getString(R.string.no_thanks),false,"update");
        myDialog.show();
    }

    private void redirectStore(String updateUrl) {

        if (Utils.isConnectedToInternet(SplashActivity.this)){
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else{
       if(!noInternetDialog.isShowing())
            noInternetDialog.show();
        }
    }
    private  boolean checkAndRequestPermissions() {
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
/*        int ACCESS_BACKGROUND_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.Loca);*/
        int CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int ACCESS_WIFI_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int CHANGE_NETWORK_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE);
        int CHANGE_WIFI_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        /*if (GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
        }*/
        if (CAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (ACCESS_WIFI_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (CHANGE_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CHANGE_NETWORK_STATE);
        }
        if (CHANGE_WIFI_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CHANGE_WIFI_STATE);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }         private Intent intent;
    private void startActivity() {
        handler = new Handler();

            handler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    if(!AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,"").equals(""))
                        intent=new Intent(SplashActivity.this, DashBoardActivity.class);
                    else
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);

                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }, SPLASH_TIME);
        }



    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INSTALL_SHORTCUT, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CHANGE_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CHANGE_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.INSTALL_SHORTCUT) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow

                        if (Utils.isConnectedToInternet(SplashActivity.this)){
                            startActivity();
                        } else{
                          if(! noInternetDialog.isShowing())
                              noInternetDialog.show();
                        }

                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INSTALL_SHORTCUT)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_NETWORK_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                            MyDialog myDialog=new MyDialog(this,mMyDialogClickListener,getString(R.string.alert),getString(R.string.permission_required),getString(R.string.ok),getString(R.string.cancel),false,"permission");
                            myDialog.show();

                        }

                    }
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkAndRequestPermissions()) {
                if (Utils.isConnectedToInternet(SplashActivity.this)){
                    startActivity();
                } else{
                   if(!noInternetDialog.isShowing())
                       noInternetDialog.show();
                }

            }
        }
    }

    @Override
    public void onClick(MyDialog dialog, int type,String flag) {

               switch (flag){
                   case "permission":
                    switch (type){

                        case 0:
                            finish();
                        break;
                        case 1:
                            checkAndRequestPermissions();
                            break;
                    }



                   break;
                   case "update":
                       switch (type){
                           case 0:
                               finish();
                               break;
                           case 1:
                               redirectStore(updateUrl);
                               break;
                       }

                       break;
               }



    }
}
