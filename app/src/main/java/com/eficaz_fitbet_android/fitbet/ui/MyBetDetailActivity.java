package com.eficaz_fitbet_android.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.model.BetDetails;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.adapters.MyBetDetailsAdapter;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.COUNTRY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DESCRIPTION;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DISTANCE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.FIRST_NAME;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.IMAGE_STATUS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betid;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betname;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_bettype;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_challengerid;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_credit;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_date;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_endlatitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_endlongitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_route;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_started;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.PARTICIPANTS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.PROFILE_PIC;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_KEY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_KEY1;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_TYPE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.STATUS_A;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.TOTAL_MESSAGE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.TOTAL_PARTICIPANTS;

public class MyBetDetailActivity extends BaseActivity {


    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.discreption)
    TextView discreption;

    @Bind(R.id.no_data)
    TextView no_data;

    @Bind(R.id.participants)
    TextView participants;

    @Bind(R.id.messages)
    TextView messages;


    @Bind(R.id.map_row)
    LinearLayout map_row;

   /* @Bind(R.id.join_now)
    Button join_now;*/

    @Bind(R.id.start)
    Button start;

    @Bind(R.id.calander)
    TextView calander;

    @Bind(R.id.clock)
    TextView clock;

    @Bind(R.id.credits)
    TextView credits;

    @Bind(R.id.invite)
    Button invite;

    @Bind(R.id.invite_group_list)
    RecyclerView bet_list;

    @Bind(R.id.btn_back)
    ImageView btn_back;

    ArrayList<BetDetails>betDetails;

    MyBetDetailsAdapter betDetailsAdapter;

    String betId="";

    Double lat,log;

    String challengerid="";

    String distance="";

    String bettype="";

    String route="";

    Bundle bundle;

    String startlongitude,endlongitude,startlatitude,endlatitude;
    String winerLat="",winerLog="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bet_detail);
        ButterKnife.bind(this);
        bundle = getIntent().getExtras();
        callMybetDetailsApi();
        CustomProgress.getInstance().showProgress(this, "", false);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callJoinApi();
                CustomProgress.getInstance().showProgress(MyBetDetailActivity.this, "", false);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitudeLongitude = Utils.getLocationFromNetwork();
                String[] Lat = latitudeLongitude.split(",");
                lat= Double.valueOf(Lat[0]);
                log= Double.valueOf(Lat[1]);
                Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).StartBet(challengerid,distance,log.toString(),lat.toString(),
                        AppPreference.getPrefsHelper().getPref(REG_KEY,""));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String bodyString = new String(response.body().bytes(), "UTF-8");
                            final JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(bodyString);
                                String data = jsonObject.getString("Status");
                                if(data.equals("Ok")){
                                    CustomProgress.getInstance().hideProgress();
                                    invite.setVisibility(View.GONE);
                                    start.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
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
                CustomProgress.getInstance().showProgress(MyBetDetailActivity.this, "", false);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        map_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!startlongitude.equals("") && !endlongitude.equals("")&& !startlatitude.equals("")&& !endlatitude.equals("")){
                    Intent i = new Intent(MyBetDetailActivity.this, MapRedirectDetailedActivity.class);
                    i.putExtra(Contents.MYBETS_startlongitude,startlongitude);
                    i.putExtra(Contents.MYBETS_endlongitude,endlongitude);
                    i.putExtra(Contents.MYBETS_startlatitude,startlatitude);
                    i.putExtra(Contents.MYBETS_endlatitude,endlatitude);
                    i.putExtra(Contents.POSITION_LATITUDE,winerLat);
                    i.putExtra(Contents.POSITION_LONGITUDE,winerLog);
                    startActivity(i);
                }
            }
        });
    }
    private void callJoinApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).JoinBet(AppPreference.getPrefsHelper().getPref(REG_KEY,""),betId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    //groupDetailsList(bodyString,position);
                    CustomProgress.getInstance().hideProgress();
                    //listOrderGroup(bodyString);
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
    private void callMybetDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Betdetail(bundle.getString(MYBETS_betid),AppPreference.getPrefsHelper().getPref(REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    BetDetailsList(bodyString);
                    //createMybets(bodyString);
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
    private void BetDetailsList(String bodyString) {
        try{
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if(data.equals("Ok")){
                name.setText(jsonObject.getString(MYBETS_betname));
                discreption.setText(jsonObject.getString(DESCRIPTION));
                participants.setText(jsonObject.getString(TOTAL_PARTICIPANTS));
                messages.setText(jsonObject.getString(TOTAL_MESSAGE));
                credits.setText(jsonObject.getString(MYBETS_credit));
                betId= jsonObject.getString(MYBETS_betid);

                try{
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = df.parse(jsonObject.getString(MYBETS_date));
                    df.setTimeZone(TimeZone.getDefault());
                    String formattedDate = df.format(date);
                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat outputformat1 = new SimpleDateFormat("hh:mm aa");
                    String output = null;
                    output = outputformat.format(date);
                    calander.setText(outputformat.format(date));
                    clock.setText(outputformat1.format(date));
                }catch (Exception e){
                    e.printStackTrace();
                }

               /* if(jsonObject.getString(MYBETS_challengerid).equals("null") || jsonObject.getString(MYBETS_challengerid).equals("")){
                   join_now.setVisibility(View.VISIBLE);
                }else{
                    join_now.setVisibility(View.GONE);
                }*/

                challengerid=jsonObject.getString(MYBETS_challengerid);

                distance=jsonObject.getString(DISTANCE);

                bettype=jsonObject.getString(MYBETS_bettype);

                route=jsonObject.getString(MYBETS_route);


                if(bettype.equals("distance")){
                    map_row.setVisibility(View.GONE);
                }else{
                    map_row.setVisibility(View.VISIBLE);
                    startlongitude=jsonObject.getString(MYBETS_startlongitude);
                    endlongitude=  jsonObject.getString(MYBETS_endlongitude);
                    startlatitude=jsonObject.getString(MYBETS_startlatitude);
                    endlatitude=jsonObject.getString(MYBETS_endlatitude);
                }
                if(jsonObject.getString(MYBETS_started).equals("no")){
                   invite.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);

                }else{
                    if(bundle.getString(MYBETS_betid).equals("0")||bundle.getString(MYBETS_betid).equals("4")){
                        invite.setVisibility(View.VISIBLE);
                        start.setVisibility(View.GONE);
                    }
                }
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(PARTICIPANTS);
                JSONArray jsonArray = new JSONArray(data1);
                if(jsonArray.length()==0){
                    no_data.setVisibility(View.VISIBLE);
                }else{
                    no_data.setVisibility(View.GONE);
                }
                betDetails = new ArrayList<>();
                betDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    BetDetails model = new BetDetails();
                    model.setFirstname(jsonList.getString(FIRST_NAME));
                    model.setCountry(jsonList.getString(COUNTRY));
                    model.setProfile_pic(jsonList.getString(PROFILE_PIC));
                    model.setRegType(jsonList.getString(REG_TYPE));
                    model.setImage_status(jsonList.getString(IMAGE_STATUS));
                    model.setReg_key(jsonList.getString(REG_KEY1));
                    betDetails.add(model);
                }
                betDetailsAdapter = new MyBetDetailsAdapter(this, betDetails,jsonObject.getString(MYBETS_betid));
                bet_list.setHasFixedSize(true);
                bet_list.setLayoutManager(new LinearLayoutManager(this));
                bet_list.setAdapter(betDetailsAdapter);
                CustomProgress.getInstance().hideProgress();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
