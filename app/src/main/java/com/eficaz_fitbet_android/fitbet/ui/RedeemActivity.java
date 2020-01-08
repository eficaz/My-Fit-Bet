package com.eficaz_fitbet_android.fitbet.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.customview.MyDialog;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.Utils;

import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.DASH_BOARD_CREDIT_SCORE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DASH_BOARD_FIRSTNAME;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DASH_BOARD_USERS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DEFAULT_COUNTRY_SHORT_NAME;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DEFAULT_CURRENCY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.EMAIL;

public class RedeemActivity extends BaseActivity {

    @Bind(R.id.submit)
    Button submit;

    @Bind(R.id.name)
    EditText name;

    @Bind(R.id.email)
    EditText email;

    @Bind(R.id.account_number)
    EditText account_number;

    @Bind(R.id.rooting_number)
    EditText rooting_number;

    @Bind(R.id.amount)
    EditText amount;

    @Bind(R.id.checkBox)
    CheckBox checkBox;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.reduce_amount)
    TextView reduce_amount;

    @Bind(R.id.admin_charges_tv)
    TextView admin_charges_tv;

    @Bind(R.id.strip_charges_tv)
    TextView strip_charges_tv;

    @Bind(R.id.strip_charge)
    TextView strip_charge;

    @Bind(R.id.admin_charge)
    TextView admin_charge;

    String totalCredit="0";
    double passingAmount;

    @Bind(R.id.space)
    Space space;

    int dinamic_adminCharge=0,dinamic_stripe_charge=0;
private MyDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        ButterKnife.bind(this);

        noInternetDialog=new MyDialog(this,null,getString(R.string.no_internet),getString(R.string.no_internet_message),getString(R.string.ok),"",true,"internet");

       if(Utils.isConnectedToInternet(this)) {
           CustomProgress.getInstance().showProgress(RedeemActivity.this, "", false);
           getAdminCharge();
       }else
           noInternetDialog.show();

       submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    if(name.getText().toString().equals("")){

                        Utils.showCustomToastMsg(RedeemActivity.this, R.string.please_enter_name);

                    }else if(!Utils.isValidEmail(email.getText().toString().trim())){
                        Utils.showCustomToastMsg(RedeemActivity.this, R.string.enter_valid_email);
                    }
                    else if(email.getText().toString().equals("")){

                        Utils.showCustomToastMsg(RedeemActivity.this, R.string.please_enter_email);

                    }else if(account_number.getText().toString().equals("")){

                        Utils.showCustomToastMsg(RedeemActivity.this, R.string.please_enter_accountNumber);

                    }else if(rooting_number.getText().toString().equals("")){

                        Utils.showCustomToastMsg(RedeemActivity.this, R.string.please_enter_rootingNumber);

                    }else if(amount.getText().toString().equals("")){

                        Utils.showCustomToastMsg(RedeemActivity.this, R.string.please_enter_amount);

                    }else{
                        if(Utils.isConnectedToInternet(RedeemActivity.this)) {
                            CustomProgress.getInstance().showProgress(RedeemActivity.this, "", false);
                            callRedimApi();
                        }else
                            noInternetDialog.show();

                    }
                }else{
                    Utils.showCustomToastMsg(RedeemActivity.this, R.string.please_agreed_terms_condition);
                }

            }
        });
        amount.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                amount.getWindowVisibleDisplayFrame(r);
                int screenHeight = amount.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    space.setVisibility(View.VISIBLE);
                } else {
                    space.setVisibility(View.GONE);
                }
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    if(amount.getText().toString().startsWith("0")){
                        amount.setText("");
                    }
                    else if(amount.getText().toString().startsWith(".0")){
                        amount.setText("");
                    }
                }
                if (charSequence.length() >= 0) {
                    if (!amount.getText().toString().trim().equals("")) {
                        space.setVisibility(View.VISIBLE);
                        //space1.setVisibility(View.VISIBLE);
                    }
                }else{
                    space.setVisibility(View.GONE);
                    //space1.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                if (editable.length() >= 1) {
                    if(amount.getText().toString().startsWith("0")){
                        amount.setText("");
                    }
                    else if(amount.getText().toString().startsWith(".0")){
                        amount.setText("");
                    }
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    if(!totalCredit.equals("0")){
                        if(Integer.parseInt(totalCredit)<Integer.parseInt(s.toString())){
                            amount.setText(totalCredit);
                        }else{

                            //int dinamic_adminCharge=0,dinamic_stripe_charge=0;

                            double amount1= Integer.parseInt(s.toString());
                            double amount2=(double) amount1*dinamic_adminCharge/100;
                            double adminCharge=amount2;
                            passingAmount=(double)(amount1-amount2);
                            double amount4=(double) passingAmount*dinamic_stripe_charge/100;
                            double stripCharge=amount4;
                            double amonut5=passingAmount-amount4;
                            reduce_amount.setText(""+new DecimalFormat("##.##").format(amonut5)+" AUD");
                            strip_charge.setText(""+new DecimalFormat("##.##").format(stripCharge));
                            admin_charge.setText(""+new DecimalFormat("##.##").format(adminCharge));
                        }
                    }else{

                    }
                      /* if(!amount.getText().toString().equals("")){
                           if(Integer.parseInt(totalCredit)<Integer.parseInt(s.toString())){
                               amount.setText(totalCredit);
                           }else{
                           }
                       }*/
                }else{
                    reduce_amount.setText("0"+" AUD");
                }
            }
        });
    }

    private void getAdminCharge() {

        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).GetAdminCharge();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("getAdminCharge == "+bodyString);
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String status = jsonObject.getString("Status");
                        if (status.trim().equals("Ok")) {
                            String data1 = jsonObject.getString("charges");
                            JSONObject jsonObject1 = new JSONObject(data1);
                             dinamic_adminCharge= Integer.parseInt(jsonObject1.getString("admin_charge"));
                             dinamic_stripe_charge= Integer.parseInt(jsonObject1.getString("stripe_charge"));
                             strip_charges_tv.setText(getResources().getString(R.string.strip_charges)+"("+dinamic_stripe_charge+")%");
                             admin_charges_tv.setText(getResources().getString(R.string.admin_charges)+"("+dinamic_adminCharge+")%");
                             callDashboardDetailsApi();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //DashboardDetailReportapiresult(bodyString);
                    //CustomProgress.getInstance().hideProgress();
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

    private void callDashboardDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardDetails(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
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
                amount.setText(jsonObject1.getString(DASH_BOARD_CREDIT_SCORE));
                email.setText(jsonObject1.getString(EMAIL));
                name.setText(jsonObject1.getString(DASH_BOARD_FIRSTNAME));
                totalCredit=amount.getText().toString();


                double amount1= Integer.parseInt(jsonObject1.getString(DASH_BOARD_CREDIT_SCORE));
                double amount2=(double) amount1*dinamic_adminCharge/100;
                double adminCharge=amount2;
                passingAmount=(double)(amount1-amount2);
                double amount4=(double) passingAmount*dinamic_stripe_charge/100;
                double stripCharge=amount4;
                double amonut5=passingAmount-amount4;
                reduce_amount.setText(""+new DecimalFormat("##.##").format(amonut5)+" AUD");
                strip_charge.setText(""+new DecimalFormat("##.##").format(stripCharge));
                admin_charge.setText(""+new DecimalFormat("##.##").format(adminCharge));


               /* int amount1= Integer.parseInt(amount.getText().toString());
                int amount2=amount1*15/100;
                int amonut3=amount1-amount2;
                int amonut4=amonut3*2/100;
                int amonut5=amonut3-amonut4;
                reduce_amount.setText(amonut5);*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void callRedimApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).StripeTransfer(DEFAULT_COUNTRY_SHORT_NAME, email.getText().toString(),""+passingAmount,
                DEFAULT_CURRENCY,account_number.getText().toString(),rooting_number.getText().toString(),name.getText().toString(),amount.getText().toString(),""+dinamic_stripe_charge,""+dinamic_adminCharge,AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    JSONObject jsonObject = new JSONObject(bodyString);
                    System.out.println("Redeem == "+bodyString);
                    String Msg = jsonObject.getString("Msg");
                    Utils.showCustomToastMsg(RedeemActivity.this, Msg);
                    CustomProgress.getInstance().hideProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}
