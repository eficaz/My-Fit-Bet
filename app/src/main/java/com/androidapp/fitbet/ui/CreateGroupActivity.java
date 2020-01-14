package com.androidapp.fitbet.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.interfaces.CameraGalaryCaputer;
import com.androidapp.fitbet.model.CommonUsage;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CACHEDKEY;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.androidapp.fitbet.utils.Contents.CAMERA_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.GALLERY_REQUEST_CODE;

public class CreateGroupActivity extends AppCompatActivity implements CommonUsage {
    @Bind(R.id.content_main)
    FrameLayout content_main;

    @Bind(R.id.btn_back)
    TableRow btn_back;




    CameraGalaryCaputer cameraGalaryCaputer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_main);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,new CreateGroupFragment(), Utils.BetScreenName).commitAllowingStateLoss();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
                Intent intent = new Intent(CreateGroupActivity.this, DashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public <T> void onSubmit(T value1, int type) {
        if (CACHEDKEY.CANCEL.ordinal() == type) {
        }
        if (CACHEDKEY.CAMERA.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(CreateGroupActivity.this,CAMERA_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
            }
        }
        if (CACHEDKEY.GALLERY.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openGallery(CreateGroupActivity.this,GALLERY_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
        Intent intent = new Intent(CreateGroupActivity.this, DashBoardActivity.class);
        startActivity(intent);
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
    //Here is new method
    public void passVal(CameraGalaryCaputer cameraGalaryCaputer) {
        this.cameraGalaryCaputer = cameraGalaryCaputer;

    }
}
