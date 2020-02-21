package com.androidapp.fitbet.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.interfaces.CameraGalleryCapture;
import com.androidapp.fitbet.model.CommonUsage;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.CACHEDKEY;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.androidapp.fitbet.utils.Contents.CAMERA_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.GALLERY_REQUEST_CODE;

public class CreateGroupActivity extends BaseActivity implements CommonUsage {
    @Bind(R.id.content_main2)
    FrameLayout content_main;

    @Bind(R.id.btn_back)
    TableRow btn_back;


    CameraGalleryCapture cameraGalaryCaputer;

    private IntentFilter filter = new IntentFilter("count_down");
    private boolean firstConnect = true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (firstConnect) {
                    firstConnect = false;

                    String message = intent.getStringExtra("message");
                    onMessageReceived(message);

                }
            } else {
                firstConnect = true;
            }

        }
    };

    @Override
    public void onMessageReceived(String message) {

        SLApplication.isCountDownRunning = true;
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main2, new CreateGroupFragment(), Utils.BetScreenName).commitAllowingStateLoss();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }


    @Override
    public <T> void onSubmit(T value1, int type) {
        if (CACHEDKEY.CANCEL.ordinal() == type) {
        }
        if (CACHEDKEY.CAMERA.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(CreateGroupActivity.this, CAMERA_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
            }
        }
        if (CACHEDKEY.GALLERY.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openGallery(CreateGroupActivity.this, GALLERY_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                cameraGalaryCaputer.requestSuccess(imageFile);
            }
            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {

            }
        });*/

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFile, EasyImage.ImageSource source, int type) {
                cameraGalaryCaputer.requestSuccess(new File(imageFile.toString().replaceAll("[\\[\\]]", "")));
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });
    }

    //Here is new method
    public void passVal(CameraGalleryCapture cameraGalaryCaputer) {
        this.cameraGalaryCaputer = cameraGalaryCaputer;

    }
}
