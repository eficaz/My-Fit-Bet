package com.androidapp.fitbet.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.old_pass)
    EditText old_pass;

    @Bind(R.id.new_password)
    EditText new_password;

    @Bind(R.id.con_new_pass)
    EditText con_new_pass;

    @Bind(R.id.bt_submit)
    Button bt_submit;


    @Bind(R.id.btn_back)
    TableRow btn_back;


    private IntentFilter filter=new IntentFilter("count_down");
    private boolean firstConnect=true;
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if (firstConnect) {
                    firstConnect = false;

                    String message = intent.getStringExtra("message");
                    onMessageReceived(message);

                }
            }else{
                firstConnect=true;
            }

        }
    };

    @Override
    public void onMessageReceived(String message) {

        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new_password.getText().toString().equals(con_new_pass.getText().toString()) && !con_new_pass.getText().toString().equals("")){
                    callChangePassApi();
                    CustomProgress.getInstance().showProgress(ChangePasswordActivity.this, "", false);
                }else{
                    Utils.showCustomToastMsg(ChangePasswordActivity.this, R.string.password_not_match);
                }

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
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
    }

    private void callChangePassApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UpdatePassword(con_new_pass.getText().toString(),AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                   // DashboardDetailReportapiresult(bodyString);
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String msg = jsonObject.getString("Msg");
                        String status = jsonObject.getString("Status");
                        Utils.showCustomToastMsg(ChangePasswordActivity.this, msg);
                        if(status.trim().equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                            finish();
                        }else{
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(ChangePasswordActivity.this, msg);
                        }
                    }catch (Exception e) {
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
}
