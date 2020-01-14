package com.androidapp.fitbet.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.camera.CameraGalleryVideoPickerBottom;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.interfaces.CameraGalaryCaputer;
import com.androidapp.fitbet.model.CommonUsage;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CACHEDKEY;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.CAMERA_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.CAMERA_VIDEO_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.FIRST_NAME;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.REG_KEY;
import static com.androidapp.fitbet.utils.Contents.WON;

public class WinnerActivity extends BaseActivity implements CommonUsage {
    Bundle bundle;
    String regNo="";
    String betId="";
    String winName="";
    String won="";
    String file_Type="";

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.skip)
    TextView skip;

    @Bind(R.id.space)
    Space space;

    @Bind(R.id.main_lay)
    LinearLayout main_lay;

    @Bind(R.id.price_amount)
    TextView price_amount;

    @Bind(R.id.ed_discreption)
    EditText ed_discreption;

    @Bind(R.id.bt_login)
    Button bt_login;

    File filePath;
    boolean imageupload =false;
    public static int VIDEO_CAPTURED = 1;
    CameraGalaryCaputer cameraGalaryCaputer;
private AppPreference appPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);
        ButterKnife.bind(this);
        appPreference=AppPreference.getPrefsHelper(this);
        clearSavedBetItems();
        ((WinnerActivity) WinnerActivity.this).passVal(new CameraGalaryCaputer() {
            @Override
            public <T> void requestFailure(int requestCode, T data) {
            }
            @Override
            public <T> File requestSuccess(File imageFile) {
                if(!imageFile.toString().equals("")){
                    imageupload=true;
                }
                try{
                    filePath= new Compressor(WinnerActivity.this).compressToFile(imageFile);
                }catch (Exception e){

                }
                updateProfilePic();
                CustomProgress.getInstance().showProgress(WinnerActivity.this, "", false);
                if (!imageFile.equals("")) {
                } else {
                }
                return imageFile;
            }
        });
        bundle = getIntent().getExtras();
        regNo=bundle.getString(REG_KEY);
        betId=bundle.getString(MYBETS_betid);
        winName=bundle.getString(FIRST_NAME);
        won=bundle.getString(WON);
        name.setText(winName);
        price_amount.setText(won);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ed_discreption.getText().toString().equals("")){
                callDiscreptionUpdateionAPI();
                CustomProgress.getInstance().showProgress(WinnerActivity.this, "", false);
                }else{
                    CustomProgress.getInstance().hideProgress();
                    AppPreference.getPrefsHelper().savePref(DASH_BOARD_POSICTION,"0");
                    startActivity(new Intent(WinnerActivity.this,DashBoardActivity.class));
                    finish();
                }
            }
        });


        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M){
            main_lay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    main_lay.getWindowVisibleDisplayFrame(r);
                    int screenHeight = main_lay.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        space.setVisibility(View.VISIBLE);
                    } else {
                        space.setVisibility(View.GONE);
                    }
                }
            });
            // Do something for lollipop and above versions
        } else{
            main_lay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    main_lay.getWindowVisibleDisplayFrame(r);
                    int screenHeight = main_lay.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        space.setVisibility(View.GONE);
                    } else {
                        space.setVisibility(View.GONE);
                    }
                }
            });
            // do something for phones running an SDK before lollipop
        }
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CameraGalleryVideoPickerBottom();
                bottomSheetDialogFragment.setCancelable(false);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), CameraGalleryVideoPickerBottom.class.getName());
            }
        });

    }
    private void callDiscreptionUpdateionAPI() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).SkipWinner(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),betId,ed_discreption.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        String Msg = jsonObject.getString("Msg");
                        if (status.trim().equals("Ok")){
                            ed_discreption.setText("");
                            CustomProgress.getInstance().hideProgress();
                            finish();
                        }
                        Utils.showCustomToastMsg(WinnerActivity.this, Msg);
                    }catch (Exception e){
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                        if(resultCode == RESULT_OK)
                        {
                            if (requestCode == CAMERA_VIDEO_REQUEST_CODE) {
                                if (!data.getData().equals("")) {
                                    Uri uri = data.getData();
                                    String selectedImagePath = getPath(uri);
                                    CustomProgress.getInstance().hideProgress();
                                    if(!uri.equals("")){
                                        filePath = new File(getPath(uri));
                                        if (filePath != null) {
                                            updateProfilePic();
                                            CustomProgress.getInstance().showProgress(this, "", false);
                                        }
                                    }
                                }
                            } else {
                                    EasyImage.handleActivityResult(requestCode, resultCode, data, this,new EasyImage.Callbacks() {
                                        @Override
                                        public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

                                        }
                                        @Override
                                        public void onImagesPicked(@NonNull List<File> imageFile, EasyImage.ImageSource source, int type) {
                                            cameraGalaryCaputer.requestSuccess(new File(imageFile.toString().replaceAll("[\\[\\]]","")));
                                            //updateProfilePic();
                                        }
                                        @Override
                                        public void onCanceled(EasyImage.ImageSource source, int type) {

                                        }
                                    });
                                }
                        }else {
                            Utils.showCustomToastMsg(WinnerActivity.this, R.string.file_upload_failed);
                        }

           }
    public String getPath1(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
    private void updateProfilePic() {
        RequestBody reg_key=RequestBody.create(MediaType.parse("multipart/from-data"), AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        RequestBody discreption;
        if(!ed_discreption.getText().toString().equals("")){
             discreption=RequestBody.create(MediaType.parse("multipart/from-data"), ed_discreption.getText().toString());
        }else{
             discreption=RequestBody.create(MediaType.parse("multipart/from-data"), "");
        }
        RequestBody betId1=RequestBody.create(MediaType.parse("multipart/from-data"), betId);
        RequestBody fileType=RequestBody.create(MediaType.parse("multipart/from-data"), file_Type);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePath);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", filePath.getName().replace(".mp4",".mp4"), requestFile);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UploadVideoOrImage(part,reg_key,betId1,fileType,discreption);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        String Msg = jsonObject.getString("Msg");
                        if(status.trim().equals("Ok")){
                            ed_discreption.setText("");
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(WinnerActivity.this, Msg);
                            File fdelete = new File(filePath.getPath());
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    System.out.println("file Deleted :" + fdelete.getName());
                                } else {
                                    System.out.println("file not Deleted :" + fdelete.getName());
                                }
                            }
                            finish();
                        }
                    }catch (Exception e){

                    }
                    CustomProgress.getInstance().hideProgress();;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void passVal(CameraGalaryCaputer cameraGalaryCaputer) {
        this.cameraGalaryCaputer = cameraGalaryCaputer;

    }


    @Override
    public <T> void onSubmit(T value1, int type) {
        if (CACHEDKEY.CANCEL.ordinal() == type) {

        }
        if (CACHEDKEY.CAMERA.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(WinnerActivity.this,CAMERA_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
            }
            file_Type="image";
        }
        if (CACHEDKEY.ACTION_VIDEO_CAPTURE.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent captureVideoIntent =new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                captureVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                captureVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 2491520L);
                startActivityForResult(captureVideoIntent,CAMERA_VIDEO_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
            file_Type="video";
        }

    }



    @Override
    public void onBackPressed() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        CustomProgress.getInstance().hideProgress();
                       /* AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "2");
                        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
                        Intent intent = new Intent(WinnerActivity.this, DashBoardActivity.class);
                        startActivity(intent);*/
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(WinnerActivity.this);
        builder.setMessage("Are you sure want to leave this page?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        //super.onBackPressed();
    }


    private void clearSavedBetItems() {
        appPreference.saveDistance("0.0");
        appPreference.savedStatusFlag(false);
        appPreference.saveUserRoute("");
        appPreference.saveOrigin("");
    }
}
