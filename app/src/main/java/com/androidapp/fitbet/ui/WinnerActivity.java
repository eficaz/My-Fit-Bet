package com.androidapp.fitbet.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.camera.CameraGalleryVideoPickerBottom;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.interfaces.CameraGalleryCapture;
import com.androidapp.fitbet.model.CommonUsage;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CACHEDKEY;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.loopj.android.http.BuildConfig;
import com.nagihong.videocompressor.VideoCompressor;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.androidapp.fitbet.utils.Contents.CAMERA_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.CAMERA_VIDEO_REQUEST_CODE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.WON;

public class WinnerActivity extends BaseActivity implements CommonUsage {
    Bundle bundle;

    String betId = "";

    String won = "";
    String file_Type = "";

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

    File mFileFetched;
    boolean imageupload = false;

    CameraGalleryCapture cameraGalaryCaputer;
    private AppPreference appPreference;


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
        setContentView(R.layout.activity_winner);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        appPreference = AppPreference.getPrefsHelper(this);
        clearSavedBetItems();
        (WinnerActivity.this).passVal(new CameraGalleryCapture() {
            @Override
            public <T> void requestFailure(int requestCode, T data) {
            }

            @Override
            public <T> File requestSuccess(File imageFile) {
                if (!imageFile.toString().equals("")) {
                    imageupload = true;
                }
                try {
                    mFileFetched = new Compressor(WinnerActivity.this).compressToFile(imageFile);
                    System.out.println("filepath inside requestSuccess  " + mFileFetched.getName());
                } catch (Exception e) {

                }
                mFileType = "image";
                updateProfilePic();
                CustomProgress.getInstance().showProgress(WinnerActivity.this, "", false);
                if (!imageFile.equals("")) {
                } else {
                }
                return imageFile;
            }
        });
        bundle = getIntent().getExtras();
        betId = bundle.getString(MYBETS_betid);
        won = bundle.getString(WON);
        name.setText(appPreference.getSavedProfileName());
        price_amount.setText(won);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_discreption.getText().toString().equals("")) {
                    callDiscreptionUpdateionAPI();
                    CustomProgress.getInstance().showProgress(WinnerActivity.this, "", false);
                } else {
                    CustomProgress.getInstance().hideProgress();
                    appPreference.savePref(DASH_BOARD_POSICTION, "0");
                    startActivity(new Intent(WinnerActivity.this, DashBoardActivity.class));
                    finish();
                }
            }
        });


        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
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
        } else {
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

    private void callDiscreptionUpdateionAPI() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).SkipWinner(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""), betId, ed_discreption.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        String Msg = jsonObject.getString("Msg");
                        if (status.trim().equals("Ok")) {
                            ed_discreption.setText("");
                            CustomProgress.getInstance().hideProgress();
                            finish();
                        }
                        Utils.showCustomToastMsg(WinnerActivity.this, Msg);
                    } catch (Exception e) {
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


    void compressVideo(final String path) {

        final String output = Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".mp4";
        new Thread() {
            @Override
            public void run() {
                super.run();
                new VideoCompressor().compressVideo(path, output);
                mFileFetched = new File(output);
                updateProfilePic();
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_VIDEO_REQUEST_CODE) {
                if (!data.getData().equals("")) {
                    Uri uri = data.getData();
                    String selectedImagePath = getPath(uri);
                    CustomProgress.getInstance().hideProgress();
                    if (!uri.toString().equals("")) {

                        mFileType = "video";
                        CustomProgress.getInstance().showProgress(this, "", false);
                        compressVideo(getPath(uri));


                    }

                }
            } else {
                EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
                    @Override
                    public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {

                    }

                    @Override
                    public void onImagesPicked(@NonNull List<File> imageFile, EasyImage.ImageSource source, int type) {
                        cameraGalaryCaputer.requestSuccess(new File(imageFile.toString().replaceAll("[\\[\\]]", "")));
                        //updateProfilePic();
                    }

                    @Override
                    public void onCanceled(EasyImage.ImageSource source, int type) {

                    }
                });
            }
        } else {
            Utils.showCustomToastMsg(WinnerActivity.this, R.string.file_upload_failed);
        }

    }

    public String getPath1(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
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
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private String mDescription = "";
    private String mFileType;

    private void updateProfilePic() {


        if (!ed_discreption.getText().toString().equals(""))
            mDescription = ed_discreption.getText().toString();

        RequestBody regKey = RequestBody.create(MediaType.parse("text/plain"), appPreference.getPref(Contents.REG_KEY, ""));
        RequestBody betID = RequestBody.create(MediaType.parse("text/plain"), betId);
        RequestBody fileType = RequestBody.create(MediaType.parse("text/plain"), mFileType);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), mDescription);

        RequestBody fBody = null;
        if (mFileType.equals("image"))
            fBody = RequestBody.create(MediaType.parse("image/*"), mFileFetched);
        else
            fBody = RequestBody.create(MediaType.parse("video/mp4"), mFileFetched);


        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", mFileFetched.getName(), fBody);
        System.out.println("Filepath " + mFileFetched + " size " + String.valueOf(mFileFetched.length() / 1024));
        System.out.println("mFileFetched.getName() " + mFileFetched.getName());


        Call<ResponseBody> call = getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UploadVideoOrImage(filePart, regKey, betID, fileType, description);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("Response code update video " + response.code());
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("updateProfilePic " + bodyString);
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        String Msg = jsonObject.getString("Msg");
                        if (status.trim().equals("Ok")) {
                            ed_discreption.setText("");
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(WinnerActivity.this, Msg);
                            File fdelete = new File(mFileFetched.getPath());
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    System.out.println("file Deleted :" + fdelete.getName());
                                } else {
                                    System.out.println("file not Deleted :" + fdelete.getName());
                                }
                            }

                            clearSavedBetItems();
                            startActivity(new Intent(WinnerActivity.this, DashBoardActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                        clearSavedBetItems();
                        System.out.println("upload exception " + e.getLocalizedMessage());
                    }
                    CustomProgress.getInstance().hideProgress();
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void passVal(CameraGalleryCapture cameraGalaryCaputer) {
        this.cameraGalaryCaputer = cameraGalaryCaputer;

    }


    @Override
    public <T> void onSubmit(T value1, int type) {
        if (CACHEDKEY.CANCEL.ordinal() == type) {

        }
        if (CACHEDKEY.CAMERA.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(WinnerActivity.this, CAMERA_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
            }
            file_Type = "image";
        }
        if (CACHEDKEY.ACTION_VIDEO_CAPTURE.ordinal() == type) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent captureVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                captureVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                /* captureVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 2491520L);*/
       /*         captureVideoIntent.putExtra(MediaStore.Video.Thumbnails.HEIGHT, 360);
                captureVideoIntent.putExtra(MediaStore.Video.Thumbnails.WIDTH, 480);*/
                startActivityForResult(captureVideoIntent, CAMERA_VIDEO_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
            file_Type = "video";
        }

    }


    @Override
    public void onBackPressed() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        CustomProgress.getInstance().hideProgress();
                        clearSavedBetItems();
                        Intent intent = new Intent(WinnerActivity.this, DashBoardActivity.class);
                        startActivity(intent);
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
        appPreference.setLatLongList(null);
        appPreference.savePositionLatitude("0.0");
        appPreference.savePositionLongitude("0.0");
    }


    private Retrofit getClient(String URL) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.callTimeout(3, TimeUnit.MINUTES);
        okHttpBuilder.connectTimeout(240, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(240, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(240, TimeUnit.SECONDS);
        okHttpBuilder.addInterceptor(getInterceptor());
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            okHttpBuilder.addNetworkInterceptor(loggingInterceptor);
        }
        return okHttpBuilder.build();
    }

    private Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request;
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                request = builder
                        .addHeader("Content-Type", "application/json")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        };
    }
}
