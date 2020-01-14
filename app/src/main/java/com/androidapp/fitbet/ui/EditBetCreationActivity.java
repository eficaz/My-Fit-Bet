package com.androidapp.fitbet.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.billingclient.api.BillingClient;
import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.RulesDialog;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_CREDIT_SCORE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_USERS;
import static com.androidapp.fitbet.utils.Contents.PARTICIPANTS;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;

public class EditBetCreationActivity extends BaseActivity {

    private static final String TAG = "Bet creation";
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";



    @Bind(R.id.bet_from_date)
    TextView bet_from_date;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.bet_to_date)
    TextView bet_to_date;

    @Bind(R.id.bet_distance_by_location)
    TextView bet_distance_by_location;

    @Bind(R.id.bet_distance_by_km)
    TextView bet_distance_by_km;

    @Bind(R.id.view1)
    View view1;

    @Bind(R.id.view2)
    View view2;

    @Bind(R.id.btnCreateBet)
    Button btnCreateBet;

    @Bind(R.id.edBet_km)
    EditText edBet_km;

    @Bind(R.id.bet_km)
    TextView bet_km;

    @Bind(R.id.from_address)
    TextView from_address;

    @Bind(R.id.to_address)
    TextView to_address;

    @Bind(R.id.bet_name)
    EditText bet_name;

    @Bind(R.id.bet_credit)
    EditText bet_credit;

    @Bind(R.id.bet_description)
    EditText bet_description;

    @Bind(R.id.toRow)
    TableRow toRow;

    @Bind(R.id.fromRow)
    TableRow fromRow;

    @Bind(R.id.row1)
    TableRow row1;

    @Bind(R.id.row2)
    TableRow row2;

    @Bind(R.id.row4)
    TableRow row4;

    @Bind(R.id.row5)
    TableRow row5;

    @Bind(R.id.row6)
    TableRow row6;

    @Bind(R.id.checkBox_rules)
    CheckBox checkBoxRules;

    @Bind(R.id.txt_rule)
    TextView txtRules;

    @Bind(R.id.space)
    Space space;


    String fromDate="",toDate="";

    String distance="0";

    SimpleDateFormat sdf;
    SimpleDateFormat sdf1;


    boolean fromORto=false;

    boolean distanceKm=false;

    boolean distanceLocation=false;

    private BillingClient billingClient;

    String credits="0";

    String startLat="0.0";
    String startLog="0.0";
    String endLat="0.0";
    String endLog="0.0";
    int kmInDec=0;
    double meter;

    String bet_id="";

    //Bundle bundle;
    String bodyString,chalagerId;

    String distance_draw;
    private LocationManager mLocationManager;
    String overview_polyline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bet_creation);
        ButterKnife.bind(this);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        startLat= bundle.getString(Contents.pass_startlatitude);
        startLog= bundle.getString(Contents.pass_startlongitude);
        endLat= bundle.getString(Contents.pass_endlatitude);
        endLog= bundle.getString(Contents.pass_endlongitude);
        bodyString= bundle.getString(Contents.bet_edit_details);
        chalagerId= bundle.getString(Contents.MYBETS_challengerid);
        distance_draw= bundle.getString(Contents.MYBETS_distance);
        overview_polyline= bundle.getString(Contents.OVERVIEW_POLYLINE);

        callDashboardDetailsApi();


        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //distanceKm=true;

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CallInappPurchass();
            }
        });

        if(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_EDIT_OR_CREATE,"").equals("false")){
            if (bundle != null) {
                int Radius = 6371;
                double lat1 =0.0;
                double lat2 = 0.0;
                double lon1 = 0.0;
                double lon2 = 0.0;
                double dLat = 0.0;
                double dLon = 0.0;
                if(!startLat.equals("")){
                    lat1 = Double.parseDouble(startLat);
                    lat2 = Double.parseDouble(endLat);
                    lon1 = Double.parseDouble(startLog);
                    lon2 = Double.parseDouble(endLog);
                    dLat = Math.toRadians(lat2 - lat1);
                    dLon = Math.toRadians(lon2 - lon1);
                }else{
                    lat1 =0.0;
                    lat2 = 0.0;
                    lon1 = 0.0;
                    lon2 = 0.0;
                    dLat = 0.0;
                    dLon = 0.0;
                }
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
                double c = 2 * Math.asin(Math.sqrt(a));
                double valueResult = Radius * c;
                double km = valueResult / 1;
                DecimalFormat newFormat = new DecimalFormat("####");
                kmInDec = Integer.valueOf(newFormat.format(km));
                meter = valueResult % 1000;
                int meterInDec = Integer.valueOf(newFormat.format(meter));
                try{
                    Geocoder geocoder;
                    List<Address> start_addresses;
                    List<Address> end_addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    start_addresses = geocoder.getFromLocation(Double.valueOf(startLat), Double.valueOf(startLog), 1);
                    end_addresses = geocoder.getFromLocation(Double.valueOf(endLat), Double.valueOf(endLog), 1);
                    String start_address = start_addresses.get(0).getAddressLine(0);
                    String start_city = start_addresses.get(0).getLocality();
                    String start_state = start_addresses.get(0).getAdminArea();
                    String start_country = start_addresses.get(0).getCountryName();
                    String start_postalCode = start_addresses.get(0).getPostalCode();
                    String start_knownName = start_addresses.get(0).getFeatureName();
                    from_address.setText(start_address);
                    String end_address = end_addresses.get(0).getAddressLine(0);
                    String end_city = end_addresses.get(0).getLocality();
                    String end_state = end_addresses.get(0).getAdminArea();
                    String end_country = end_addresses.get(0).getCountryName();
                    String end_postalCode = end_addresses.get(0).getPostalCode();
                    String end_knownName = end_addresses.get(0).getFeatureName();
                    to_address.setText(end_address);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //bet_km.setText(kmInDec +"KM");
                distanceLocation=true;
            }else{
                distanceKm=true;
            }
            if(startLat.equals("0")){
                edBet_km.setVisibility(View.VISIBLE);
                bet_distance_by_km.setTextColor(getResources().getColor(R.color.light_blue));
                bet_distance_by_location.setTextColor(getResources().getColor(R.color.hint_text_color));
                view1.setBackgroundColor(getResources().getColor(R.color.light_blue));
                AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betid, bet_id);
                view2.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                bet_km.setVisibility(View.GONE);
                fromRow.setVisibility(View.GONE);
                toRow.setVisibility(View.GONE);
                distanceKm=true;
                distanceLocation=false;

            }else{
                double dis= Double.parseDouble(distance_draw.replace("km","").replace("m",""))/1000;
                bet_km.setText(""+dis+"km");
                edBet_km.setVisibility(View.GONE);
                bet_km.setVisibility(View.VISIBLE);
                fromRow.setVisibility(View.VISIBLE);
                toRow.setVisibility(View.VISIBLE);
                distanceKm=false;
                distanceLocation=true;
                view1.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                view2.setBackgroundColor(getResources().getColor(R.color.light_blue));
                bet_distance_by_km.setTextColor(getResources().getColor(R.color.hint_text_color));
                bet_distance_by_location.setTextColor(getResources().getColor(R.color.light_blue));

            }
            bet_name.setText(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_betname,""));
            bet_credit.setText(AppPreference.getPrefsHelper().getPref(Contents.CREDIT_SCORE,""));

            try{
                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                String output = null;
                DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date thisMorningMidnight = justDay.parse(AppPreference.getPrefsHelper().getPref(Contents.START_DATE,""));
                output = outputformat.format(thisMorningMidnight);
                bet_from_date.setText(output);
            }catch (Exception e){

            }



            bet_to_date.setText(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_enddate,""));
            fromDate=AppPreference.getPrefsHelper().getPref(Contents.START_DATE,"");
            toDate=AppPreference.getPrefsHelper().getPref(Contents.MYBETS_enddate,"");
            bet_description.setText(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_description,""));
            try{
                DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date thisMorningMidnight = justDay.parse(fromDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(thisMorningMidnight);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String cal_date = sdf.format(cal.getTime());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                toDate=cal_date;
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            callMybetDetailsApi();
            CustomProgress.getInstance().showProgress(this, "", false);
        }
        txtRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RulesDialog(EditBetCreationActivity.this);
            }
        });

        bet_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromORto=true;
                new SingleDateAndTimePickerDialog.Builder(EditBetCreationActivity.this)
                        .bottomSheet()
                        .curved().minutesStep(1)
                        .displayMinutes(true)
                        .displayHours(true)
                        .displayDays(true)
                        .displayMonth(true)
                        .displayYears(true)
                        .mainColor(getResources().getColor(R.color.light_blue))
                        .backgroundColor(getResources().getColor(R.color.pading_gray))
                        .titleTextColor(getResources().getColor(R.color.light_blue))
                        .title(getResources().getString(R.string.select_date_time))
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                try{
                                    sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    //Date date2 = df.parse(String.valueOf(date));
                                    df.setTimeZone(TimeZone.getDefault());
                                    // String formattedDate = df.format(date2);
                                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                                    String output = null;
                                    output = outputformat.format(date);

                                    Date strDate = outputformat.parse(output);
                                    if (System.currentTimeMillis() > strDate.getTime()) {
                                        Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.please_choose_after_date);
                                        fromDate="";
                                        bet_from_date.setText("");
                                        toDate="";
                                        bet_to_date.setText("");
                                        //catalog_outdated = 1;
                                        fromORto=true;
                                        Date currentTime = Calendar.getInstance().getTime();
                                        new SingleDateAndTimePickerDialog.Builder(EditBetCreationActivity.this)
                                                .bottomSheet()
                                                .curved()
                                                .defaultDate(currentTime)
                                                .displayMinutes(true)
                                                .minutesStep(1)
                                                .displayHours(true)
                                                .displayDays(true)
                                                .displayMonth(true)
                                                .displayYears(true)
                                                .mainColor(getResources().getColor(R.color.light_blue))
                                                .backgroundColor(getResources().getColor(R.color.pading_gray))
                                                .titleTextColor(getResources().getColor(R.color.light_blue))
                                                .title(getResources().getString(R.string.select_date_time))
                                                .listener(new SingleDateAndTimePickerDialog.Listener() {
                                                    @Override
                                                    public void onDateSelected(Date date) {
                                                        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                        try{
                                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                                            //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            //Date date1 = df.parse(String.valueOf(date));
                                                            df.setTimeZone(TimeZone.getDefault());
                                                            String formattedDate = df.format(date);
                                                            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                                                            String output = null;
                                                            output = outputformat.format(date);
                                                            Date strDate = outputformat.parse(output);
                                                            if (System.currentTimeMillis() > strDate.getTime()) {
                                                                Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.please_choose_after_date);
                                                                fromDate="";
                                                                bet_from_date.setText("");
                                                                toDate="";
                                                                bet_to_date.setText("");
                                                                //catalog_outdated = 1;
                                                            }else{
                                                                if(fromORto==true){
                                                                    fromDate=sdf.format(date);
                                                                    bet_from_date.setText(""+output);
                                                                } else{
                                                                    toDate=sdf.format(date);
                                                                    bet_to_date.setText(""+output);
                                                                }
                                                            }
                                                            DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                            justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            Date thisMorningMidnight = justDay.parse(fromDate);
                                                            Calendar cal = Calendar.getInstance();
                                                            cal.setTime(thisMorningMidnight);
                                                            cal.set(Calendar.MILLISECOND, 0);
                                                            cal.set(Calendar.SECOND, 59);
                                                            cal.set(Calendar.MINUTE, 59);
                                                            cal.set(Calendar.HOUR_OF_DAY, 23);
                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                            String cal_date = sdf.format(cal.getTime());
                                                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            toDate=cal_date;

                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                })
                                                .display();

                                    }else{
                                        if(fromORto==true){
                                            fromDate=sdf.format(date);
                                            bet_from_date.setText(""+output);
                                        } else{
                                            toDate=sdf.format(date);
                                            bet_to_date.setText(""+output);
                                        }
                                    }
                                    DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date thisMorningMidnight = justDay.parse(fromDate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(thisMorningMidnight);
                                    cal.set(Calendar.MILLISECOND, 0);
                                    cal.set(Calendar.SECOND, 59);
                                    cal.set(Calendar.MINUTE, 59);
                                    cal.set(Calendar.HOUR_OF_DAY, 23);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    String cal_date = sdf.format(cal.getTime());
                                    toDate=cal_date;


                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .display();
            }
        });
        bet_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromORto=false;
                new SingleDateAndTimePickerDialog.Builder(EditBetCreationActivity.this)
                        .bottomSheet()
                        .curved()
                        .displayMinutes(true)
                        .displayHours(true)
                        .displayDays(true)
                        .displayMonth(true)
                        .displayYears(true)
                        .mainColor(getResources().getColor(R.color.light_blue))
                        .backgroundColor(getResources().getColor(R.color.pading_gray))
                        .titleTextColor(getResources().getColor(R.color.light_blue))
                        .title(getResources().getString(R.string.select_date_time))
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                try{
                                    sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    //Date date2 = df.parse(String.valueOf(date));
                                    df.setTimeZone(TimeZone.getDefault());
                                    // String formattedDate = df.format(date2);
                                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                                    String output = null;
                                    output = outputformat.format(date);
                                    if(fromORto==true){
                                        fromDate=sdf.format(date);
                                        bet_from_date.setText(""+output);
                                    } else{
                                        toDate=sdf.format(date);
                                        bet_to_date.setText(""+output);
                                    }
                                    DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date thisMorningMidnight = justDay.parse(fromDate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(thisMorningMidnight);
                                    cal.set(Calendar.MILLISECOND, 0);
                                    cal.set(Calendar.SECOND, 59);
                                    cal.set(Calendar.MINUTE, 59);
                                    cal.set(Calendar.HOUR_OF_DAY, 23);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String cal_date = sdf.format(cal.getTime());
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    toDate=cal_date;
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        })
                        .display();
            }
        });
        bet_distance_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(EditBetCreationActivity.this)) {
                    Log.e("fitbet","Gps already enabled");
                    showSettingsAlert("NETWORK");
                }else{
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betname, bet_name.getText().toString());
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betid, bet_id);
                    AppPreference.getPrefsHelper().savePref(Contents.CREDIT_SCORE, bet_credit.getText().toString());
                    AppPreference.getPrefsHelper().savePref(Contents.START_DATE, fromDate);
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_enddate, toDate);
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_EDIT_OR_CREATE, "false");
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_description,  bet_description.getText().toString());
                    startActivity(new Intent(EditBetCreationActivity.this, MapDistanceByLoctionActivity.class));
                    finish();
                    edBet_km.setVisibility(View.GONE);
                    bet_km.setVisibility(View.VISIBLE);
                    fromRow.setVisibility(View.GONE);
                    toRow.setVisibility(View.GONE);
                    distanceKm=false;
                    distanceLocation=true;
                    bet_km.setText("");
                    view1.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                    view2.setBackgroundColor(getResources().getColor(R.color.light_blue));
                    bet_distance_by_km.setTextColor(getResources().getColor(R.color.hint_text_color));
                    bet_distance_by_location.setTextColor(getResources().getColor(R.color.light_blue));
                }
            }
        });
        bet_distance_by_km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edBet_km.setVisibility(View.VISIBLE);
                distanceLocation=true;
                bet_distance_by_km.setTextColor(getResources().getColor(R.color.light_blue));
                bet_distance_by_location.setTextColor(getResources().getColor(R.color.hint_text_color));
                view1.setBackgroundColor(getResources().getColor(R.color.light_blue));
                AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betid, bet_id);
                view2.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                bet_km.setVisibility(View.GONE);
                fromRow.setVisibility(View.GONE);
                toRow.setVisibility(View.GONE);
                 distanceKm=true;
                 distanceLocation=false;
                 bet_km.setText("");
            }
        });
        btnCreateBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bet_name.getText().toString().equals("")){
                    Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.please_enter_bet_name);
                }else if(bet_credit.getText().toString().equals("")){
                    Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.enter_credit);
                }else if(bet_credit.getText().toString().equals("")){
                    Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.enter_credit);
                }else if(fromDate.equals("")||toDate.equals("")) {
                    Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.select_date);
                } else if(bet_description.getText().toString().trim().equals("")){
                        Utils.showCustomToastMsg(EditBetCreationActivity.this,"Please Enter Description");
                    }else if(!checkBoxRules.isChecked()){

                    Utils.showCustomToastMsg(EditBetCreationActivity.this,"Please agree to the Rules and Regulations");
                } else if(distanceKm==true){
                    if(!edBet_km.getText().toString().equals("")){
                        if(distanceKm==true){
                           try{
                               DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                               Date strDate = outputformat.parse(bet_from_date.getText().toString());
                               if (System.currentTimeMillis() > strDate.getTime()) {
                                   Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.please_choose_after_date);
                               }else{
                                   callAddBetApi();
                                   CustomProgress.getInstance().showProgress(EditBetCreationActivity.this, "", false);
                               }
                           }catch (Exception e){

                           }
                           /* callAddBetApi();
                            CustomProgress.getInstance().showProgress(EditBetCreationActivity.this, "", false);*/
                        }
                    } else if(distanceLocation==true){
                        Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.enter_km);
                    }
                }else if(distanceLocation==true){

                     if(distanceLocation==false){
                        Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.enter_km);
                    }else{
                         try{
                             DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                             Date strDate = outputformat.parse(bet_from_date.getText().toString());
                             if (System.currentTimeMillis() > strDate.getTime()) {
                                 Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.please_choose_after_date);
                             }else{
                                 callAddLocationTypeBetApi();
                                 CustomProgress.getInstance().showProgress(EditBetCreationActivity.this, "", false);
                             }
                         }catch (Exception e){

                         }

                     }
                    if(distance.equals("")){
                        Utils.showCustomToastMsg(EditBetCreationActivity.this, R.string.select_start_end_location);
                    }else{

                    }
                }
            }
        });



        edBet_km.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 0) {
                    if (!edBet_km.getText().toString().trim().equals("")) {
                        space.setVisibility(View.VISIBLE);
                        //space1.setVisibility(View.VISIBLE);
                    }
                }else{
                    space.setVisibility(View.GONE);
                    //space1.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edBet_km.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                edBet_km.getWindowVisibleDisplayFrame(r);
                int screenHeight = edBet_km.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    space.setVisibility(View.VISIBLE);
                } else {
                    space.setVisibility(View.GONE);
                }
            }
        });

    }
    private void callMybetDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Betdetail(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_BET_ID,""),AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject = new JSONObject(bodyString);
                    String data = jsonObject.getString(STATUS_A);
                    if(data.equals("Ok")){


                        CustomProgress.getInstance().hideProgress();

                        bet_name.setText(jsonObject.getString(Contents.MYBETS_betname));
                        bet_credit.setText(jsonObject.getString(Contents.MYBETS_credit));
                        bet_id=jsonObject.getString(Contents.MYBETS_betid);
                        AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betid, bet_id);
                        try{
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date date = df.parse(jsonObject.getString(Contents.MYBETS_date));
                            df.setTimeZone(TimeZone.getDefault());
                            String formattedDate = df.format(date);
                            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                            String output = null;
                            output = outputformat.format(date);
                            bet_from_date.setText(output);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try{
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date date = df.parse(jsonObject.getString(Contents.MYBETS_enddate));
                            df.setTimeZone(TimeZone.getDefault());
                            String formattedDate = df.format(date);
                            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                            String output = null;
                            output = outputformat.format(date);
                            bet_to_date.setText(output);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        bet_description.setText(jsonObject.getString(Contents.DESCRIPTION));
                        from_address.setText(jsonObject.getString(Contents.MYBETS_startlocation));
                        to_address.setText(jsonObject.getString(Contents.MYBETS_endlocation));
                        meter=Double.parseDouble(jsonObject.getString(Contents.DISTANCE))/1000;
                        if(jsonObject.getString(Contents.MYBETS_bettype).equals("distance")){
                            bet_km.setText(jsonObject.getString(Contents.MYBETS_credit));
                           /*edBet_km.setText(""+Double.parseDouble(jsonList.getString(Contents.DISTANCE))/1000);
                           bet_km.setText(""+Double.parseDouble(jsonList.getString(Contents.DISTANCE))/1000); */
                            edBet_km.setText((Double.parseDouble(jsonObject.getString(Contents.DISTANCE))/1000) +"");
                            bet_km.setText((Double.parseDouble(jsonObject.getString(Contents.DISTANCE))/1000) +"");

                            distanceKm=true;
                            distanceLocation=false;
                            bet_distance_by_location.setClickable(true);
                            bet_distance_by_location.setFocusable(true);
                            bet_distance_by_km.setClickable(true);
                            bet_distance_by_km.setFocusable(true);
                            view1.setBackgroundColor(getResources().getColor(R.color.light_blue));
                            view2.setBackgroundColor(getResources().getColor(R.color.hint_text_color ));
                            bet_distance_by_km.setTextColor(getResources().getColor(R.color.light_blue));
                            bet_distance_by_location.setTextColor(getResources().getColor(R.color.hint_text_color));
                            fromRow.setVisibility(View.GONE);
                            toRow.setVisibility(View.GONE);
                            edBet_km.setVisibility(View.VISIBLE);
                            bet_km.setVisibility(View.GONE);

                            startLat= jsonObject.getString(Contents.MYBETS_startlatitude);
                            startLog= jsonObject.getString(Contents.MYBETS_startlongitude);
                            endLat= jsonObject.getString(Contents.MYBETS_endlatitude);
                            endLog= jsonObject.getString(Contents.MYBETS_endlongitude);
                            distance_draw= String.valueOf(Double.parseDouble(jsonObject.getString(Contents.DISTANCE)));
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            // df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            // Date date = df.parse(jsonList.getString(Contents.MYBETS_date));
                            //df.setTimeZone(TimeZone.getDefault());
                            //String formattedDate = df.format(date);
                            Date Todate = df.parse(jsonObject.getString(Contents.MYBETS_enddate));
                            df.setTimeZone(TimeZone.getDefault());
                            String toDatea = df.format(Todate);

                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date date = df1.parse(jsonObject.getString(Contents.MYBETS_date));
                            String formattedDate = df1.format(date);

                            fromDate=formattedDate;

                            toDate=toDatea;
                        }else{
                            //edBet_km.setText(jsonList.getString(Contents.MYBETS_credit));

                            edBet_km.setText((Double.parseDouble(jsonObject.getString(Contents.DISTANCE))/1000) +"");
                            bet_km.setText((Double.parseDouble(jsonObject.getString(Contents.DISTANCE))/1000) +"");
                            view1.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                            view2.setBackgroundColor(getResources().getColor(R.color.light_blue));
                            bet_distance_by_km.setTextColor(getResources().getColor(R.color.hint_text_color));
                            bet_distance_by_location.setTextColor(getResources().getColor(R.color.light_blue));
                            edBet_km.setVisibility(View.GONE);
                            bet_km.setVisibility(View.VISIBLE);
                            fromRow.setVisibility(View.VISIBLE);
                            toRow.setVisibility(View.VISIBLE);
                            distanceKm=false;
                            distanceLocation=true;
                            bet_distance_by_km.setClickable(true);
                            bet_distance_by_km.setFocusable(true);
                            bet_distance_by_location.setClickable(true);
                            bet_distance_by_location.setFocusable(true);
                            distance_draw= String.valueOf(Double.parseDouble(jsonObject.getString(Contents.DISTANCE)));
                            startLat= jsonObject.getString(Contents.MYBETS_startlatitude);
                            startLog= jsonObject.getString(Contents.MYBETS_startlongitude);
                            endLat= jsonObject.getString(Contents.MYBETS_endlatitude);
                            endLog= jsonObject.getString(Contents.MYBETS_endlongitude);
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));

                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date date = df1.parse(jsonObject.getString(Contents.MYBETS_date));
                            String formattedDate = df1.format(date);

                            df.setTimeZone(TimeZone.getDefault());

                            Date Todate = df.parse(jsonObject.getString(Contents.MYBETS_enddate));
                            df.setTimeZone(TimeZone.getDefault());
                            String toDatea = df.format(Todate);
                            fromDate=formattedDate;
                            toDate=toDatea;

                        }
                        String data1 = jsonObject.getString(PARTICIPANTS);
                        JSONArray yukilanarray =  new JSONArray(data1);
                        if(yukilanarray.length()>1){
                            bet_name.setEnabled(false);
                            bet_credit.setEnabled(false);
                            bet_description.setEnabled(false);
                            edBet_km.setEnabled(false);
                            bet_from_date.setEnabled(true);
                            bet_distance_by_km.setEnabled(false);
                            bet_distance_by_location.setEnabled(false);
                            toRow.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                            fromRow.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                            row1.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                            row2.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                            row4.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                            row5.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                            row6.setBackgroundColor(getResources().getColor(R.color.pading_gray1));
                        }else{
                            bet_name.setEnabled(true);
                            bet_credit.setEnabled(true);
                            bet_description.setEnabled(true);
                            bet_from_date.setEnabled(true);
                            bet_distance_by_km.setEnabled(true);
                            bet_distance_by_location.setEnabled(true);
                        }

                       // BetDetailsList(bodyString);

                    }
                    //createMybets(bodyString);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }


    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
    private void callDashboardDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardDetails(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    DashboardDetailReportapiresult(bodyString);
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
    private void DashboardDetailReportapiresult(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                String data1 = jsonObject.getString(DASH_BOARD_USERS);
                JSONObject jsonObject1 = new JSONObject(data1);
                credits=jsonObject1.getString(DASH_BOARD_CREDIT_SCORE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callAddLocationTypeBetApi() {
        //double distance;
        //distance = Double.parseDouble(edBet_km.getText().toString().trim())/1000;
        double dis= Double.parseDouble(distance_draw.replace("km","").replace("m",""));
        String distance_draw_meter= String.valueOf(dis);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UpdateBet(bet_name.getText().toString(),
                bet_description.getText().toString(),fromDate,toDate, distance_draw_meter,
                from_address.getText().toString(),to_address.getText().toString(),startLog,endLog,startLat,endLat,overview_polyline, bet_credit.getText().toString().trim(),
                AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),"location",AppPreference.getPrefsHelper().getPref(Contents.MYBETS_betid,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject = new JSONObject(bodyString);
                    String msg = jsonObject.getString("Msg");
                    String data = jsonObject.getString("Status");
                    if(Integer.parseInt(credits)>Integer.parseInt(bet_credit.getText().toString())){
                        if(data.equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(EditBetCreationActivity.this, msg);
                            AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                            AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");
                            Intent intent = new Intent(EditBetCreationActivity.this, DashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(EditBetCreationActivity.this, msg);
                            CallInappPurchass();
                        }

                    }else{
                        CustomProgress.getInstance().hideProgress();
                        Utils.showCustomToastMsg(EditBetCreationActivity.this, msg);
                        CallInappPurchass();
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
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditBetCreationActivity.this);
        alertDialog.setTitle(provider + " SETTINGS");
        alertDialog.setMessage(provider + " is not enabled! Want to go to Gps menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private void callAddBetApi() {
        double distance;
        distance = Double.parseDouble(edBet_km.getText().toString().trim())*1000;
                //.distanceTo(locationB)/1000;
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UpdateBet(
                bet_name.getText().toString(),
                bet_description.getText().toString(),
                fromDate,
                toDate,
                String.valueOf(distance),
                "","","","","","","",
                bet_credit.getText().toString().trim(),
                AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),
                "distance",
                AppPreference.getPrefsHelper().getPref(Contents.MYBETS_betid,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject = new JSONObject(bodyString);
                    String msg = jsonObject.getString("Msg");
                    String data = jsonObject.getString("Status");

                    if(Integer.parseInt(credits)>Integer.parseInt(bet_credit.getText().toString())){
                        if(data.equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(EditBetCreationActivity.this, msg);
                            AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                            AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");
                            Intent intent = new Intent(EditBetCreationActivity.this, DashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(EditBetCreationActivity.this, msg);
                            CallInappPurchass();
                        }

                    }else{
                        CustomProgress.getInstance().hideProgress();
                        Utils.showCustomToastMsg(EditBetCreationActivity.this, msg);
                        CallInappPurchass();
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

    private void CallInappPurchass() {
        /*Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);*/
    }
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
