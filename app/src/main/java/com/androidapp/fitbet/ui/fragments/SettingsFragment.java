package com.androidapp.fitbet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidapp.fitbet.LoginActivity;
import com.androidapp.fitbet.R;
import com.androidapp.fitbet.camera.CameraGalleryPickerBottom;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.interfaces.CameraGalaryCaputer;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.service.LocService;
import com.androidapp.fitbet.ui.ChangePasswordActivity;
import com.androidapp.fitbet.ui.CreditPurchaseActivity;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.ui.PrivacyPolicyActivity;
import com.androidapp.fitbet.ui.RedeemActivity;
import com.androidapp.fitbet.ui.TermsAndServiceActivity;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_FIRSTNAME;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_USERS;
import static com.androidapp.fitbet.utils.Contents.DEFAULT_COUNTRY;
import static com.androidapp.fitbet.utils.Contents.DEFAULT_COUNTRY_SHORT_NAME;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;

public class SettingsFragment extends Fragment  {


    @Bind(R.id.name)
    EditText name;

    @Bind(R.id.bt_submit)
    Button bt_submit;

    @Bind(R.id.img_user)
    CircleImageView img_user;

    @Bind(R.id.change_password)
    TableRow change_password;

    @Bind(R.id.redeem)
    TableRow redeem;


    @Bind(R.id.row_privacy_policy)
    TableRow row_privacy_policy;

    @Bind(R.id.terms_and_service)
    TableRow terms_and_service;

    @Bind(R.id.share)
    TableRow share;

    @Bind(R.id.row_credit_purchase)
    TableRow row_credit_purchase;

    @Bind(R.id.rowlogOut)
    TableRow rowlogOut;
    @Bind(R.id.rules_regulations)
TableRow rulesRegulations;
    @Bind(R.id.change_pic)
    RelativeLayout change_pic;

    File filePath;



    private boolean imageupload;
   private  boolean first_time;
private AppPreference appPreference;
private MyDialog noInternetDialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onViewCreated SettingsFragment");
        appPreference=AppPreference.getPrefsHelper(getActivity());
        appPreference.savePref(DASH_BOARD_POSICTION,"4");
        noInternetDialog=new MyDialog(getActivity(),null,getString(R.string.no_internet),getString(R.string.no_internet_message),getString(R.string.ok),"",true,"internet");
        initView();
    }
    private void initView() {
        bt_submit.setVisibility(View.GONE);
        first_time=true;
        if(appPreference.getPref(Contents.REG_WITH_F_OR_G,"").equals("true")){
            change_password.setVisibility(View.GONE);
        }else{
            change_password.setVisibility(View.VISIBLE);
        }

        name.setText(appPreference.getSavedProfileName());


        if (!appPreference.getSavedProfileImage().equals("NA")) {

                Picasso.get().load(appPreference.getSavedProfileImage())
                        .placeholder(R.drawable.image_loader)
                        .into(img_user);

        } else {
            img_user.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user_profile_avatar));
        }

        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RedeemActivity.class));
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });
        //name.setFocusable(false);
        Utils.hideKeyboard(getActivity());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name.setFocusable(true);
                //showKeyboard(name);

            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(first_time){

                    bt_submit.setVisibility(View.GONE);
                    first_time=false;
                }else if(!first_time){

                    bt_submit.setVisibility(View.VISIBLE);
                }

            }
        });
        row_credit_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreditPurchaseActivity.class);
                startActivity(i);
            }
        });
        terms_and_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TermsAndServiceActivity.class);
                startActivity(i);
            }
        });
        row_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PrivacyPolicyActivity.class);
                i.putExtra("name","privacy");
                startActivity(i);
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!name.getText().toString().equals("")){
                    submitSettingsDetailsApi();
                    CustomProgress.getInstance().showProgress(getActivity(), "", false);
                    if(imageupload) {
                        updateProfilePic();

                    }//CustomProgress.getInstance().showProgress(getActivity(), "", false);

                }else{
                    Utils.showCustomToastMsg(getActivity(), R.string.please_enter_name);
                }


                //Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
            }
        });
        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CameraGalleryPickerBottom();
                bottomSheetDialogFragment.setCancelable(false);
                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), CameraGalleryPickerBottom.class.getName());
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.eficaztechsol.com/");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        rulesRegulations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PrivacyPolicyActivity.class);
                i.putExtra("name","rules");
                startActivity(i);
            }
        });
        rowlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isConnectedToInternet(getActivity()))
                LogOutApi();
                else
                    noInternetDialog.show();

            }
        });

    }

    private void clearSavedBetItems() {
        appPreference.saveDistance("0.0");
        appPreference.savedStatusFlag(false);
        appPreference.saveUserRoute("");
        appPreference.saveOrigin("");
        appPreference.setLatLongList(null);
    }
    public void showKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editText, 0);
        bt_submit.setVisibility(View.VISIBLE);
    }

    public void logoutFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
        System.out.println("fb already logged out");

            return;
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {

            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    private void LogOutApi() {
        CustomProgress.getInstance().showProgress(getActivity(), "", false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LogOutApi(appPreference.getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                System.out.println("logout == "+bodyString);
                CustomProgress.getInstance().hideProgress();
                JSONObject jsonObject=new JSONObject(bodyString);

                if(jsonObject.getString("Status").equals("Ok")){

                    appPreference.savePref(Contents.REG_KEY, "");
                    clearSavedBetItems();

                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           logoutFromFacebook();
                       }
                   },500);


                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    SLApplication.removeLocationUpdates();
                    if(SLApplication.isServiceRunning)
                        new LocService().stopSelf();
                    if(getActivity()!=null)
                        getActivity().finish();
                }else{
                    Utils.showCustomToastMsg(getActivity(),jsonObject.getString("Msg"));
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
    private void updateProfilePic() {

        RequestBody reg_key = RequestBody.create(MediaType.parse("text/plain"),appPreference.getPref(Contents.REG_KEY,""));

        RequestBody fBody = RequestBody.create(MediaType.parse("image/jpg"), filePath);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", filePath.getName(),fBody);


        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UploadProfilePic(filePart,reg_key);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("settings update pic = "+bodyString);

                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        CustomProgress.getInstance().hideProgress();
                        if(status.trim().equals("Ok")){
                            callDashboardDetailsApi();
                            Utils.showCustomToastMsg(getActivity(), jsonObject.getString("Msg"));
                            bt_submit.setVisibility(View.GONE);
                            first_time=true;

                            File fdelete = new File(filePath.getPath());
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    System.out.println("file Deleted :" + fdelete.getName());
                                } else {
                                    System.out.println("file not Deleted :" + fdelete.getName());
                                }
                            }

                        }
                    }catch (Exception e){
                        //CustomProgress.getInstance().hideProgress();
                    }
                    //CustomProgress.getInstance().hideProgress();;
                } catch (Exception e) {
                    e.printStackTrace();
                    //CustomProgress.getInstance().hideProgress();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void submitSettingsDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UpdateSettingsDetails(name.getText().toString(),DEFAULT_COUNTRY,DEFAULT_COUNTRY_SHORT_NAME,appPreference.getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        System.out.println("submit settings = "+bodyString);
                        if(status.trim().equals("Ok")){

                            //Utils.showCustomToastMsg(getActivity(), R.string.updated_successfully);
                            bt_submit.setVisibility(View.GONE);
                            first_time=true;
                            if(!imageupload){
                                Utils.showCustomToastMsg(getActivity(), jsonObject.getString("Msg"));
                                CustomProgress.getInstance().hideProgress();
                            }
                            callDashboardDetailsApi();
                            //CustomProgress.getInstance().hideProgress();
                        }
                    }catch (Exception e){

                    }
                   // CustomProgress.getInstance().hideProgress();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        ButterKnife.bind(this, view);




        ((DashBoardActivity) getActivity()).passVal(new CameraGalaryCaputer() {
            @Override
            public <T> void requestFailure(int requestCode, T data) {
            }
            @Override
            public <T> File requestSuccess(File imageFile) {
                if(!imageFile.toString().equals("")){
                    imageupload=true;
                }
                try{
                    filePath= new Compressor(getActivity()).compressToFile(imageFile);
                }catch (Exception e){

                }

                if (!imageFile.equals("")) {
                    bt_submit.setVisibility(View.VISIBLE);
                    try {
                        Bitmap myBitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
                        img_user.setImageBitmap(myBitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Drawable d = getActivity().getResources().getDrawable(R.drawable.user_profile_avatar);
                    d.setBounds(0, 0, 80, 80);
                    img_user.setImageDrawable(d);
                }
                return imageFile;
            }
        });
        return view;
    }
    private void callDashboardDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardDetails(appPreference.getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    DashboardDetailReportapiresult(bodyString);
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
    private void DashboardDetailReportapiresult(String bodyString) {

        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                String data1 = jsonObject.getString(DASH_BOARD_USERS);
                JSONObject jsonObject1 = new JSONObject(data1);

                name.setText(jsonObject1.getString(DASH_BOARD_FIRSTNAME));
                appPreference.saveProfileName(jsonObject1.getString(DASH_BOARD_FIRSTNAME));

                final Context mContext = getActivity() ;
                if (!jsonObject1.getString(PROFILE_PIC).equals("NA")) {
                    if (jsonObject1.getString(REG_TYPE).equals("normal")&&jsonObject1.getString(IMAGE_STATUS).equals("0")){
                        Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+jsonObject1.getString(PROFILE_PIC))
                                .placeholder(R.drawable.image_loader)
                                .into(img_user);
                        appPreference.saveProfileImage(Constant.BASE_APP_IMAGE__PATH+jsonObject1.getString(PROFILE_PIC));
                    }else{
                        Picasso.get().load(jsonObject1.getString(PROFILE_PIC))
                                .placeholder(R.drawable.image_loader)
                                .into(img_user);
                        appPreference.saveProfileImage(jsonObject1.getString(PROFILE_PIC));
                    }
                } else {
                    img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.user_profile_avatar));
                    appPreference.saveProfileImage(jsonObject1.getString(PROFILE_PIC));
                }
                CustomProgress.getInstance().hideProgress();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
