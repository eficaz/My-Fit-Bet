package com.androidapp.fitbet.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.androidapp.fitbet.R;

import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.model.Products;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.CREDIT_SCORE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_CREDIT_SCORE;
import static com.androidapp.fitbet.utils.Contents.REG_KEY;

public class CreditPurchaseActivity extends BaseActivity implements PurchasesUpdatedListener,MyDialog.MyDialogClickListener {


    @Bind(R.id.cer1)
    TableRow cer1;

    @Bind(R.id.cer2)
    TableRow cer2;

    @Bind(R.id.cer3)
    TableRow cer3;

    @Bind(R.id.cer4)
    TableRow cer4;

    @Bind(R.id.zero)
    TableRow zero;

    @Bind(R.id.subtration)
    TableRow subtration;

    @Bind(R.id.add)
    TableRow add;

    @Bind(R.id.total_am)
    TextView total_am;

    @Bind(R.id.total_sub)
    TextView total_am_sub;

    @Bind(R.id.bt_purchass)
    Button bt_purchass;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.checkBox)
    CheckBox checkBox;


    private int am1=10,am2=20,am3=30,am4=50;

    String am_zero="0";

    static final int RC_REQUEST = 10001;
    // Graphics for the gas gauge
    static int[] TANK_RES_IDS = { R.drawable.coins, R.drawable.coins, R.drawable.coins,
            R.drawable.coins, R.drawable.coins };
    // How many u

private ArrayList<Products> productsList;

    private BillingClient billingClient;

private MyDialog noInternetDialog;
private AppPreference appPreference;
private String productId,purchaseDate,invoiceId,rate,credit,updatedCredit;
private MyDialog.MyDialogClickListener myDialogClickListener;

            private IntentFilter filter=new IntentFilter("count_down");
            private boolean firstConnect=true;
            private BroadcastReceiver lBroadcastReceiver=new BroadcastReceiver() {
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
        setContentView(R.layout.activity_credit_purchase);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(lBroadcastReceiver, filter);
        appPreference=AppPreference.getPrefsHelper(this);
myDialogClickListener=this;
                noInternetDialog=new MyDialog(this,null,getString(R.string.no_internet),getString(R.string.no_internet_message),getString(R.string.ok),"",true,"internet");

                if(Utils.isConnectedToInternet(CreditPurchaseActivity.this)) {

getProductDetails();
                }else
                    noInternetDialog.show();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setupBillingClient();
                    }
                }, 500);


        Drawable buttonDrawable = cer1.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_blue));
        cer1.setBackground(buttonDrawable);

        Drawable buttonDrawable1 = cer2.getBackground();
        buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
        DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
        cer2.setBackground(buttonDrawable1);

        Drawable buttonDrawable2 = cer3.getBackground();
        buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
        DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
        cer3.setBackground(buttonDrawable2);

        Drawable buttonDrawable3 = cer4.getBackground();
        buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
        DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
        cer4.setBackground(buttonDrawable3);
        bt_purchass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!total_am.getText().toString().equals("0")){

                    credit = total_am.getText().toString();
                rate = total_am_sub.getText().toString();

                String id = getProductId(credit);

                if (Utils.isConnectedToInternet(CreditPurchaseActivity.this)) {

                    if (billingClient.isReady()) {

                        List<String> skuList = new ArrayList<>();
                        skuList.add(id);

                        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.INAPP).build();

                        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(0)).build();

                                billingClient.launchBillingFlow(CreditPurchaseActivity.this, billingFlowParams);

                            }
                        });


                    } else {
                        Utils.showCustomToastMsg(CreditPurchaseActivity.this, "Billing client is not ready");
                    }


                } else {
                    Utils.showCustomToastMsg(CreditPurchaseActivity.this, R.string.no_internet);
                }
                // startActivity(new Intent(CreditPurchaseActivity.this, MainActivity.class));
            }else{
                    Utils.showCustomToastMsg(CreditPurchaseActivity.this,"Please select any one of the pack");
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_am.setText(am_zero);
                total_am_sub.setText(am_zero);
                Drawable buttonDrawable = cer1.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_blue));
                cer1.setBackground(buttonDrawable);

                Drawable buttonDrawable1 = cer2.getBackground();
                buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                cer2.setBackground(buttonDrawable1);

                Drawable buttonDrawable2 = cer3.getBackground();
                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                cer3.setBackground(buttonDrawable2);

                Drawable buttonDrawable3 = cer4.getBackground();
                buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                cer4.setBackground(buttonDrawable3);
            }
        });
        subtration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount=Integer.parseInt(total_am.getText().toString().trim());
                int totalAddAmount= amount-am1;
                if(total_am.getText().toString().trim().equals("0")){
                    total_am.setText(""+am_zero);
                    total_am_sub.setText(""+am_zero);
                }else{

                    total_am.setText(""+totalAddAmount);
                    total_am_sub.setText(getRateFromCredit(totalAddAmount));
                }
                if(totalAddAmount==10){
                    Drawable buttonDrawable = cer1.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer1.setBackground(buttonDrawable);

                    Drawable buttonDrawable1 = cer2.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);
                }else if(totalAddAmount==20){
                    Drawable buttonDrawable = cer2.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer2.setBackground(buttonDrawable);

                    Drawable buttonDrawable1 = cer1.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);
                }else if(totalAddAmount==30){

                    Drawable buttonDrawable1 = cer2.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer1.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);

                    Drawable buttonDrawable = cer3.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer3.setBackground(buttonDrawable);
                }else if(totalAddAmount==50){
                    Drawable buttonDrawable = cer4.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer4.setBackground(buttonDrawable);


                    Drawable buttonDrawable1 = cer1.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer2.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable3);
                }else {
                    Drawable buttonDrawable = cer1.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable);

                    Drawable buttonDrawable1 = cer2.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);
                }

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount=Integer.parseInt(total_am.getText().toString().trim());
                int totalAddAmount= amount+am1;
                total_am.setText(""+totalAddAmount);
                total_am_sub.setText(getRateFromCredit(totalAddAmount));
                if(totalAddAmount==10){
                    Drawable buttonDrawable = cer1.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer1.setBackground(buttonDrawable);

                    Drawable buttonDrawable1 = cer2.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);
                }else if(totalAddAmount==20){
                    Drawable buttonDrawable = cer2.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer2.setBackground(buttonDrawable);


                    Drawable buttonDrawable1 = cer1.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);
                }else if(totalAddAmount==30){

                    Drawable buttonDrawable1 = cer2.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer1.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);

                    Drawable buttonDrawable = cer3.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer3.setBackground(buttonDrawable);
                }else if(totalAddAmount==50){
                    Drawable buttonDrawable = cer4.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                    cer4.setBackground(buttonDrawable);


                    Drawable buttonDrawable1 = cer1.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer2.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable3);
                }else if(totalAddAmount>=100){
                    total_am.setText("100");
                    total_am_sub.setText(getRateFromCredit(100));
                    Utils.showCustomToastMsg(CreditPurchaseActivity.this, R.string.max_alowed_five_cashier);
                }else {
                    Drawable buttonDrawable = cer1.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_blue));
                    cer1.setBackground(buttonDrawable);

                    Drawable buttonDrawable1 = cer2.getBackground();
                    buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                    DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                    cer2.setBackground(buttonDrawable1);

                    Drawable buttonDrawable2 = cer3.getBackground();
                    buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                    DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                    cer3.setBackground(buttonDrawable2);

                    Drawable buttonDrawable3 = cer4.getBackground();
                    buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                    DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                    cer4.setBackground(buttonDrawable3);
                }
            }
        });
        cer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = cer1.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                cer1.setBackground(buttonDrawable);

                Drawable buttonDrawable1 = cer2.getBackground();
                buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                cer2.setBackground(buttonDrawable1);

                Drawable buttonDrawable2 = cer3.getBackground();
                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                cer3.setBackground(buttonDrawable2);

                Drawable buttonDrawable3 = cer4.getBackground();
                buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                cer4.setBackground(buttonDrawable3);
                total_am.setText(""+am1);
                total_am_sub.setText(getRateFromCredit(am1));
            }
        });
        cer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = cer2.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                cer2.setBackground(buttonDrawable);

                Drawable buttonDrawable1 = cer1.getBackground();
                buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                cer1.setBackground(buttonDrawable1);

                Drawable buttonDrawable2 = cer3.getBackground();
                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                cer3.setBackground(buttonDrawable2);

                Drawable buttonDrawable3 = cer4.getBackground();
                buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                cer4.setBackground(buttonDrawable3);
                total_am.setText(""+am2);
                total_am_sub.setText(getRateFromCredit(am2));

            }
        });
        cer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable buttonDrawable1 = cer2.getBackground();
                buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                cer2.setBackground(buttonDrawable1);

                Drawable buttonDrawable2 = cer1.getBackground();
                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                cer1.setBackground(buttonDrawable2);

                Drawable buttonDrawable3 = cer4.getBackground();
                buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                cer4.setBackground(buttonDrawable3);

                Drawable buttonDrawable = cer3.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                cer3.setBackground(buttonDrawable);
                total_am.setText(""+am3);
                total_am_sub.setText(getRateFromCredit(am3));
            }
        });
        cer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable buttonDrawable = cer4.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.light_green));
                cer4.setBackground(buttonDrawable);


                Drawable buttonDrawable1 = cer1.getBackground();
                buttonDrawable1 = DrawableCompat.wrap(buttonDrawable1);
                DrawableCompat.setTint(buttonDrawable1, getResources().getColor(R.color.light_blue));
                cer1.setBackground(buttonDrawable1);

                Drawable buttonDrawable2 = cer3.getBackground();
                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                DrawableCompat.setTint(buttonDrawable2, getResources().getColor(R.color.light_blue));
                cer3.setBackground(buttonDrawable2);

                Drawable buttonDrawable3 = cer2.getBackground();
                buttonDrawable3 = DrawableCompat.wrap(buttonDrawable3);
                DrawableCompat.setTint(buttonDrawable3, getResources().getColor(R.color.light_blue));
                cer2.setBackground(buttonDrawable3);
                total_am.setText(""+am4);
                total_am_sub.setText(getRateFromCredit(am4));


            }
        });
    }

    private String getProductId(String credit) {

        for (Products product:productsList) {

            if(product.getCredit().equals(credit)){
                return product.getProductId();
            }

        }

        return "s";
    }

    private void getProductDetails() {
        CustomProgress.getInstance().showProgress(CreditPurchaseActivity.this,"",false);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).getProducts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
System.out.println("Products "+bodyString);
                    CustomProgress.getInstance().hideProgress();
                 if(new JSONObject(bodyString).getString("Status").equals("Ok"))
       setProductDetails(bodyString);
                 else
                     System.out.println("Error retrieving products");

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

    private void setProductDetails(String response) {


        try {
            JSONObject mainJSONObject = new JSONObject(response);
            JSONArray productsArray = mainJSONObject.getJSONArray("Products");

            productsList=new ArrayList<>();
            Products products;

            for (int i = 0; i <productsArray.length() ; i++) {

                JSONObject arrayObject=productsArray.getJSONObject(i);
                products=new Products();
                products.setId(arrayObject.getString("id"));
                products.setProductId(arrayObject.getString("product_id"));
                products.setName(arrayObject.getString("name"));
                products.setType(arrayObject.getString("type"));
                products.setCredit(arrayObject.getString("credit"));
                products.setRate(arrayObject.getString("rate"));
                products.setStatus(arrayObject.getString("status"));
                productsList.add(products);

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String getRateFromCredit(int c){
                String credit=String.valueOf(c);

        for (Products products:productsList) {

            if(products.getCredit().equals(credit))
                return products.getRate();

        }
        return "9.99";
    }

    private void setupBillingClient() {
                billingClient= BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if(billingResult.getResponseCode()== BillingClient.BillingResponseCode.OK){

                            System.out.println("Billing client enabled successfully");
                           // Toast.makeText(CreditPurchaseActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }else{
                            System.out.println("billingResult.getResponseCode() "+billingResult.getResponseCode());
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {

                    }
                });
    }

    @Override
            protected void onStop() {
                super.onStop();
                LocalBroadcastManager.getInstance(this).unregisterReceiver(lBroadcastReceiver);
            }





    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK&&purchases!=null) {

                    Purchase p=purchases.get(0);
                    productId=p.getSku();
                    purchaseDate=getPurchaseDateTime(p.getPurchaseTime());
                    invoiceId=p.getOrderId();

                   //handlePurchase();

                            //okPurchase();

                    new CallWeb().execute("");


                   System.out.println("onPurchasesUpdated Purchase success "+productId+purchaseDate+invoiceId);
                }else{

                    Utils.showCustomToastMsg(CreditPurchaseActivity.this,"Purchase failed");
                }

    }

    private String getPurchaseDateTime(long time){

            Date d=new Date(time);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(d);
    }

    private void handlePurchase() {
        CustomProgress.getInstance().showProgress(CreditPurchaseActivity.this,"",false);
        System.out.println("handlePurchase Id "+productId+"invoice id "+invoiceId+"date "+purchaseDate+"credit rate"+credit+rate);
        Call  call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).purchaseCredit(appPreference.getPref(REG_KEY,""),productId,invoiceId,purchaseDate,credit,rate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
try {


                String bodyString = new String(response.body().bytes(), "UTF-8");

                System.out.println("Purchase credit "+bodyString);

                if(CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();

                    JSONObject jsonObject = new JSONObject(bodyString);
                    if(jsonObject.getString("Status").equals("Ok")){
                        updatedCredit=jsonObject.getString("Creditscore");
                        MyDialog myDialog=new MyDialog(CreditPurchaseActivity.this,myDialogClickListener,"","Your credit added to account",getString(R.string.ok),"",false,"purchase");
                        myDialog.show();
                    }else{
                        Utils.showCustomToastMsg(CreditPurchaseActivity.this,"Something went wrong!");
                    }


}catch (IOException | JSONException e){
    e.printStackTrace();
}
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(CustomProgress.getInstance().isShowing())
                    CustomProgress.getInstance().hideProgress();
            }
        });



    }

    private void okPurchase(){
        CustomProgress.getInstance().showProgress(CreditPurchaseActivity.this,"",false);
String resp;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("reg_key", appPreference.getPref(REG_KEY,""))
                .addFormDataPart("productid", productId)
                .addFormDataPart("invoiceid", invoiceId)
                .addFormDataPart("purchasedate", purchaseDate)
                .addFormDataPart("credit", credit)
                .addFormDataPart("rate", rate)
                .build();
        Request request = new Request.Builder()
                .url("http://fitbet.com.au/api/user/androidpurchase")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                // ... handle failed request
                CustomProgress.getInstance().hideProgress();
                System.out.println("Handle failed request");
            }else{
                CustomProgress.getInstance().hideProgress();
                ResponseBody responseBody=response.body();
                resp=responseBody.string();

                System.out.println("ok response = "+resp);

                JSONObject jsonObject = new JSONObject(resp);
                if(jsonObject.getString("Status").equals("Ok")){
                    updatedCredit=jsonObject.getString("Creditscore");
                    MyDialog myDialog=new MyDialog(CreditPurchaseActivity.this,myDialogClickListener,"","Your credit added to account",getString(R.string.ok),"",false,"purchase");
                    myDialog.show();
                }else{
                    Utils.showCustomToastMsg(CreditPurchaseActivity.this,"Something went wrong!");
                }



            }

        } catch (IOException  | JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(MyDialog dialog, int type, String flag) {
                if(flag.equals("purchase")){
                    appPreference.savePref(CREDIT_SCORE,updatedCredit);
                finish();}

    }



    class CallWeb extends AsyncTask<String , String , String> {
        String resp;
        @Override
        protected String doInBackground(String... params) {

            System.out.println("handlePurchase Id "+productId+"invoice id "+invoiceId+"date "+purchaseDate+"credit rate"+credit+rate);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("reg_key", appPreference.getPref(REG_KEY,""))
                    .addFormDataPart("productid", productId)
                    .addFormDataPart("invoiceid", invoiceId)
                    .addFormDataPart("purchasedate", purchaseDate)
                    .addFormDataPart("credit", credit)
                    .addFormDataPart("rate", rate)
                    .build();
            Request request = new Request.Builder()
                    .url("http://fitbet.com.au/api/user/androidpurchase")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    // ... handle failed request
                    resp="failed";
                    CustomProgress.getInstance().hideProgress();
                    System.out.println("Handle failed request");
                } else {
                    CustomProgress.getInstance().hideProgress();
                    ResponseBody responseBody = response.body();
                    resp = responseBody.string();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return resp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgress.getInstance().showProgress(CreditPurchaseActivity.this,"",false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("onPostExecute ok response = "+s);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);

            if(jsonObject.getString("Status").equals("Ok")){
                updatedCredit=jsonObject.getString("Creditscore");
                MyDialog myDialog=new MyDialog(CreditPurchaseActivity.this,myDialogClickListener,"","Your credit added to account",getString(R.string.ok),"",false,"purchase");
                myDialog.show();
            }else{
                Utils.showCustomToastMsg(CreditPurchaseActivity.this,"Something went wrong!");
            }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
