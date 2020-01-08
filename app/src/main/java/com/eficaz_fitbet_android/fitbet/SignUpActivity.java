package com.eficaz_fitbet_android.fitbet;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.DashBoardActivity;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.DEFAULT_COUNTRY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DEFAULT_COUNTRY_SHORT_NAME;

public class SignUpActivity extends AppCompatActivity {


    @Bind(R.id.et_username)
    EditText et_username;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_confirm_password)
    EditText et_confirm_password;

    @Bind(R.id.main_lay)
    LinearLayout main_lay;

    @Bind(R.id.space)
    Space space;

    @Bind(R.id.btn_back)
    TableRow btn_back;


    /*@Bind(R.id.scrollView)
    ScrollView scrollView;*/

    @Bind(R.id.submit)
    Button submit;

    String CountryCode="";

    String CountryName="";
    String token="";
    String deviceID="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        CountryCode= getResources().getConfiguration().locale.getCountry();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg();
            }
        });

        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "0");
        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
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
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    if (!et_username.getText().toString().trim().equals("")) {
                        scrollTo(space);
                    } /*else {
                        Utils.showCustomToastMsg(SignUpActivity.this, R.string.incorrect_credentials);
                    }*/
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    if (!et_email.getText().toString().trim().equals("")) {
                        scrollTo(space);
                        space.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    if (!et_password.getText().toString().trim().equals("")) {
                        scrollTo(space);
                        space.setVisibility(View.VISIBLE);
                    } /*else {
                        Utils.showCustomToastMsg(SignUpActivity.this, R.string.incorrect_credentials);
                    }*/
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    public void scrollTo(View view) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //scrollView.scrollTo(0, scrollView.getBottom());
            }
        });
    }
    private void reg() {
        if(et_username.getText().toString().trim().equals("")){
            Utils.showCustomToastMsg(SignUpActivity.this, R.string.enter_userName);
        }else if(et_email.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(SignUpActivity.this, R.string.enter_valid_email);
        }  else if(!Utils.isValidEmail(et_email.getText().toString().trim())){
            Utils.showCustomToastMsg(SignUpActivity.this, R.string.enter_valid_email);
        }
        else if(et_password.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(SignUpActivity.this, R.string.select_password_type);
        }else if(et_confirm_password.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(SignUpActivity.this, R.string.select_confirm_password_type);
        }
        else if(!et_password.getText().toString().trim().equals(et_confirm_password.getText().toString().trim())) {
            Utils.showCustomToastMsg(SignUpActivity.this, R.string.password_con_password_not_match);
        }
        else {
            if (Utils.isConnectedToInternet(SignUpActivity.this)){
                CustomProgress.getInstance().showProgress(this, "", false);
                callregApi(et_username.getText().toString(),et_email.getText().toString().trim(),et_password.getText().toString().trim());
            } else{
                Utils.showCustomToastMsg(SignUpActivity.this, R.string.no_internet);
            }

        }
    }

    private void callregApi(String username, String email, String password) {

       // String deviceID=Utils.getAndroidDeviceUniqueID(this);
        String CountryName = getResources().getConfiguration().locale.getDisplayCountry();
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).SignUpTrack(username,email,deviceID,password,DEFAULT_COUNTRY,DEFAULT_COUNTRY_SHORT_NAME,"android");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    signUpSucess(bodyString);
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
    private void signUpSucess(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String msg = jsonObject.getString("Msg");
            String status = jsonObject.getString("Status");
            Utils.showCustomToastMsg(SignUpActivity.this, msg);
            if(status.trim().equals("Ok")){
                CustomProgress.getInstance().hideProgress();
                Utils.showCustomToastMsg(SignUpActivity.this, msg);
                startActivity(new Intent(SignUpActivity.this, DashBoardActivity.class));
                finish();
                JSONObject jsonObject1 = new JSONObject( jsonObject.getString("userdetails"));
                String reg_key = jsonObject1.getString("reg_key");
                AppPreference.getPrefsHelper().savePref(Contents.REG_KEY, reg_key);
                //JSONArray jsonArray = new JSONArray(reg_key);
            }else{
                CustomProgress.getInstance().hideProgress();
                Utils.showCustomToastMsg(SignUpActivity.this, msg);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
