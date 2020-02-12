package com.androidapp.fitbet;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.DEFAULT_COUNTRY;
import static com.androidapp.fitbet.utils.Contents.DEFAULT_COUNTRY_SHORT_NAME;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    @Bind(R.id.bt_reg)
    Button reg;

    @Bind(R.id.et_username)
    EditText et_username;

    @Bind(R.id.et_password)
    EditText et_password;

    @Bind(R.id.bt_login)
    Button bt_login;

    /*@Bind(R.id.bt_facebook)
    Button bt_facebook;

    @Bind(R.id.bt_google)
    Button bt_google;*/

private GoogleSignInClient googleSignInClient;
    TextView textView;
    private static final int RC_SIGN_IN = 1;

    private CallbackManager mCallbackManager;
    private static final String EMAIL = "email";
    private static final String AUTH_TYPE = "rerequest";
    String deviceID="";
    String CountryName="";
    String CountryCode="";

    @Bind(R.id.login_button)
    LoginButton mLoginButton;

    @Bind(R.id.sign_in_button)
    SignInButton signInButton;

    @Bind(R.id.fg_row)
    TableRow fg_row;

    @Bind(R.id.google_row)
    TableRow google_row;

    /*@Bind(R.id.scrollView)
    ScrollView scrollView;*/

   /* @Bind(R.id.space)
    Space space;*/

    @Bind(R.id.main_lay)
    LinearLayout main_lay;

    String token="";

   /* @Bind(R.id.space1)
    Space space1;*/

    private MyDialog noInternetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this);

        mCallbackManager = CallbackManager.Factory.create();
        AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
        AppPreference.getPrefsHelper().savePref(Contents.BAT_POSICTION, "0");


        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "0");
        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
        noInternetDialog=new MyDialog(this,null,getString(R.string.no_internet),getString(R.string.no_internet_message),getString(R.string.ok),"",true,"internet");
        mLoginButton.setReadPermissions(Arrays.asList("public_profile,email,user_birthday"));
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult().getToken();
                    deviceID=token;
                }
            }
        });

        CountryName = getResources().getConfiguration().locale.getDisplayCountry();
        CountryCode= getResources().getConfiguration().locale.getCountry();


        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        google_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "true");
                if (Utils.isConnectedToInternet(LoginActivity.this)){
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent,RC_SIGN_IN);
                } else{
                  noInternetDialog.show();
                }
            }
        });
        main_lay.getViewTreeObserver().addOnGlobalLayoutListener(new             ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                main_lay.getWindowVisibleDisplayFrame(r);
                int screenHeight = main_lay.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    //space.setVisibility(View.VISIBLE);
                    //space1.setVisibility(View.VISIBLE);
                } else {
                    //space.setVisibility(View.GONE);
                    //space1.setVisibility(View.GONE);
                }
            }
        });
        fg_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "true");
                if (Utils.isConnectedToInternet(LoginActivity.this)){
                    mLoginButton.performClick();
                } else{
             noInternetDialog.show();
                }
               // CustomProgress.getInstance().showProgress(LoginActivity.this, "", false);
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomProgress.getInstance().showProgress(LoginActivity.this, "", false);
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "false");
                if (Utils.isConnectedToInternet(LoginActivity.this)){
                    reg();
                } else{
           noInternetDialog.show();
                }
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "false");
                if (Utils.isConnectedToInternet(LoginActivity.this)){
                    login();
                } else{
                    noInternetDialog.show();
                }

            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    if (!et_password.getText().toString().trim().equals("")) {
                       //space.setVisibility(View.VISIBLE);
                        //space1.setVisibility(View.VISIBLE);
                    } else {
                        Utils.showCustomToastMsg(LoginActivity.this, R.string.incorrect_credentials);
                    }
                }else{
                    //space.setVisibility(View.GONE);
                    //space1.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");

                getUserProfile(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                System.out.println("onCancel");
                CustomProgress.getInstance().hideProgress();
            }
            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
//                Log.v("LoginActivity", exception.getCause().toString());
                CustomProgress.getInstance().hideProgress();
            }
        });
    }


    private void login() {
        if(et_username.getText().toString().trim().equals("")){
            Utils.showCustomToastMsg(LoginActivity.this, R.string.incorrect_credentials);
        }else if(et_password.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(LoginActivity.this, R.string.incorrect_credentials);
        }else if(!Utils.isValidEmail(et_username.getText().toString().trim())){
            Utils.showCustomToastMsg(LoginActivity.this, R.string.enter_valid_email);
        } else {
            if (Utils.isConnectedToInternet(LoginActivity.this)){
                callloginApi(et_username.getText().toString().trim(),et_password.getText().toString().trim());
                CustomProgress.getInstance().showProgress(this, "", false);
            } else{
   noInternetDialog.show();
            }

        }
    }
    String msg="Error";
    private void callloginApi(String username, String password) {


        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LoginTrack(username,password,deviceID,"android");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

              msg=msg+response.code();
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    loginSuccess(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                CustomProgress.getInstance().hideProgress();
                msg=msg+"Call failed:"+t.getLocalizedMessage();


            }
        });

       // showError(msg);
    }

    private void showError(String message){


        MyDialog eDialog=new MyDialog(this,null,"",message,"OK","",false,"error");
        eDialog.show();
    }

           private void loginSuccess(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            String msg = jsonObject.getString("Msg");
            if(status.trim().equals("Ok")){
                AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "true");
                CustomProgress.getInstance().hideProgress();
                startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                finish();

                JSONObject jsonObject1 = new JSONObject( jsonObject.getString("userdetails"));
                String reg_key = jsonObject1.getString("reg_key");
                System.out.println("REG KEY=== "+reg_key);
                AppPreference.getPrefsHelper().savePref(Contents.REG_KEY, reg_key);
                //JSONArray jsonArray = new JSONArray(reg_key);


            }else{
                CustomProgress.getInstance().hideProgress();
                Utils.showCustomToastMsg(LoginActivity.this, msg);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void reg() {
    startActivity(new Intent(this,SignUpActivity.class));
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    @Override
    public void onBackPressed() {
        finish();
        CustomProgress.getInstance().hideProgress();
        super.onBackPressed();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            System.out.println("Google sign in success ");
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Login", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    private void updateUI(@Nullable GoogleSignInAccount account) {
        String name="";
        if (account != null) {
            CountryName=getResources().getConfiguration().locale.getDisplayCountry();

                name= account.getDisplayName();

            Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LoginTrackWithGoogle(
                    name,
                    account.getEmail(),
                    deviceID,
                   account.getId(),
                    String.valueOf(account.getPhotoUrl()),
                    DEFAULT_COUNTRY,DEFAULT_COUNTRY_SHORT_NAME,"android");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String bodyString = new String(response.body().bytes(), "UTF-8");
                        System.out.println("Google login resp "+bodyString);
                        AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "true");
                        loginSuccess(bodyString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        } else {

        }
    }

    private void gotoProfile(GoogleSignInResult result){

        String name="";
        System.out.println("Google sign in result "+result.toString());
        CountryName = getResources().getConfiguration().locale.getDisplayCountry();
        if(result.getSignInAccount().getDisplayName().equals(null)){
            name= result.getSignInAccount().getEmail();
        }else{
            name= result.getSignInAccount().getDisplayName();
        }
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LoginTrackWithGoogle(
                name,
                result.getSignInAccount().getEmail(),
                deviceID,
                result.getSignInAccount().getId(),
                String.valueOf(result.getSignInAccount().getPhotoUrl()),
                DEFAULT_COUNTRY,DEFAULT_COUNTRY_SHORT_NAME,"android");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    AppPreference.getPrefsHelper().savePref(Contents.REG_WITH_F_OR_G, "true");
                    loginSuccess(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }


    private void getUserProfile(AccessToken currentAccessToken) {
        if(!CustomProgress.getInstance().isShowing())
        CustomProgress.getInstance().showProgress(this, "", false);
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            System.out.println("Graph request "+first_name+" , "+last_name+" , "+email+" , "+id+" , "+image_url);
                            gotoFbProfile(first_name+" "+last_name,email,deviceID,id,image_url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void gotoFbProfile(String f_firstname, String f_email, String deviceID, String f_facebookID, String profile_pic) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LoginTrackWithfb(
                f_firstname,
                f_email,
                deviceID,
                f_facebookID,
                profile_pic,
                DEFAULT_COUNTRY,DEFAULT_COUNTRY_SHORT_NAME,"android");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body()!=null){
                        String bodyString = new String(response.body().bytes(), "UTF-8");

                        loginSuccess(bodyString);
                    }else{
                        Utils.showCustomToastMsg(LoginActivity.this, ""+response.body());
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





}
