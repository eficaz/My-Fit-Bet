package com.androidapp.fitbet.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.StaticsUtils_BarChart;
import com.androidapp.fitbet.utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.AVERAGESPEED;
import static com.androidapp.fitbet.utils.Contents.AVERAGE_DISTANCE;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_COUNTRY;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_FIRSTNAME;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_LOST;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_MONTH;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_SIX_MONTH;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_USERS;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_WEEK;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_WON;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_YEAR;
import static com.androidapp.fitbet.utils.Contents.DAY;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.MONTH;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.TOTAL_DISTANCE;
import static com.androidapp.fitbet.utils.Contents.WEEK_DISTANCE;

public class UserProfileDashBoardFragment extends Fragment {

    @Bind(R.id.barchart)
    BarChart chart;

    @Bind(R.id.week)
    TextView week;

    @Bind(R.id.month)
    TextView month;

    @Bind(R.id.sex_month)
    TextView sex_month;

    @Bind(R.id.year)
    TextView year_;

    @Bind(R.id.average_speed)
    TextView average_speed;

    @Bind(R.id.average_km)
    TextView average_km;

    @Bind(R.id.total_km)
    TextView total_km;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.address)
    TextView address;

    @Bind(R.id.won)
    TextView won;

    @Bind(R.id.lost)
    TextView lost;

  /*  @Bind(R.id.credit)
    TextView credit;*/

    @Bind(R.id.img_user)
    CircleImageView img_user;




    ArrayList<String> dateDes = new ArrayList<>();
    ArrayList<String> speed = new ArrayList<>();
private AppPreference appPreference;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appPreference=AppPreference.getPrefsHelper(getActivity());
        intintView();
    }
    private void intintView() {
        intintChart();
        callDashboardDetailsApi();


        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week.setBackgroundResource(R.drawable.shape_button_roud_corner1);
                week.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                week.setTextColor(getResources().getColorStateList(R.color.white));
                month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                month.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                sex_month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                sex_month.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                year_.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                year_.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                dateDes.clear();
                speed.clear();
                callweekApi();
                CustomProgress.getInstance().showProgress(getActivity(), "", false);

            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month.setBackgroundResource(R.drawable.shape_button_roud_corner1);
                month.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                month.setTextColor(getResources().getColorStateList(R.color.white));
                week.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                week.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                sex_month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                sex_month.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                year_.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                year_.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                dateDes.clear();
                speed.clear();
                callmonthApi();
                CustomProgress.getInstance().showProgress(getActivity(), "", false);

            }
        });
        sex_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex_month.setBackgroundResource(R.drawable.shape_button_roud_corner1);
                sex_month.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                sex_month.setTextColor(getResources().getColorStateList(R.color.white));
                month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                month.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                week.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                week.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                year_.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                year_.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                dateDes.clear();
                speed.clear();
                callsex_monthApi();
                CustomProgress.getInstance().showProgress(getActivity(), "", false);

            }
        });
        year_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year_.setBackgroundResource(R.drawable.shape_button_roud_corner1);
                year_.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
                year_.setTextColor(getResources().getColorStateList(R.color.white));
                month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                month.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                sex_month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                sex_month.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                week.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
                week.setBackgroundTintList(getResources().getColorStateList(R.color.gray_2));
                dateDes.clear();
                speed.clear();
                callyear_Api();
                CustomProgress.getInstance().showProgress(getActivity(), "", false);
            }
        });
    }
    private void intintChart() {
        week.setBackgroundResource(R.drawable.shape_button_roud_corner1);
        week.setBackgroundTintList(getResources().getColorStateList(R.color.light_green));
        week.setTextColor(getResources().getColorStateList(R.color.white));
        month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
        month.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        sex_month.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
        sex_month.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        year_.setTextColor(getResources().getColorStateList(R.color.hint_text_color));
        year_.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        callweekApi();
        dateDes.clear();
        speed.clear();
        CustomProgress.getInstance().showProgress(getActivity(), "", false);
    }
    private void callDashboardDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardDetails(AppPreference.getPrefsHelper().getPref(Contents.USER_PROFILE_REG_KEY,""));
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
        System.out.println("dashboard "+bodyString);
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                String data1 = jsonObject.getString(DASH_BOARD_USERS);
                JSONObject jsonObject1 = new JSONObject(data1);
                name.setText(jsonObject1.getString(DASH_BOARD_FIRSTNAME));
                appPreference.saveProfileName(jsonObject1.getString(DASH_BOARD_FIRSTNAME));
                address.setText(jsonObject1.getString(DASH_BOARD_COUNTRY));
                won.setText(jsonObject1.getString(DASH_BOARD_WON));
                lost.setText(jsonObject1.getString(DASH_BOARD_LOST));


                final Context mContext = getActivity() ;
                if (!jsonObject1.getString(PROFILE_PIC).equals("NA")) {
                    if (jsonObject1.getString(REG_TYPE).equals("normal")&&jsonObject1.getString(IMAGE_STATUS).equals("0")){
                        //Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+jsonObject1.getString(PROFILE_PIC)).into(img_user);
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
                }
                else{
                    appPreference.saveProfileImage(jsonObject1.getString(PROFILE_PIC));
                    img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.user_profile_avatar));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void callweekApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardWeek(AppPreference.getPrefsHelper().getPref(Contents.USER_PROFILE_REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    weekReportapiresult(bodyString);
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
    private void callmonthApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardMonth(AppPreference.getPrefsHelper().getPref(Contents.USER_PROFILE_REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    monthReportapiresult(bodyString);
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

    private void monthReportapiresult(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(DASH_BOARD_MONTH);
                JSONArray jsonArray = new JSONArray(data1);
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList year = new ArrayList();
                average_speed.setText(jsonObject.getString(AVERAGESPEED));
                average_km.setText(""+  String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString(AVERAGE_DISTANCE )))/1000 )+" Km");
                total_km.setText(""+ String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString( TOTAL_DISTANCE  )))/1000)+ " Km");
                if(jsonObject.getString("totaldistance").equals("0")){
                    chart.setNoDataText(getResources().getString(R.string.no_chart_data_available));
                    //StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes,0);
                }else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        speed.add(jsonList.getString(WEEK_DISTANCE));
                        dateDes.add(jsonList.getString(DAY));
                    }
                    StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes, 0);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callsex_monthApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Dashboard6Month(AppPreference.getPrefsHelper().getPref(Contents.USER_PROFILE_REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    sexMonthReportapiresult(bodyString);
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

    private void sexMonthReportapiresult(String bodyString) {

        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(DASH_BOARD_SIX_MONTH);
                JSONArray jsonArray = new JSONArray(data1);
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList year = new ArrayList();
                average_speed.setText(jsonObject.getString(AVERAGESPEED ));
                average_km.setText(""+  String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString(AVERAGE_DISTANCE )))/1000 )+" Km");
                total_km.setText(""+ String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString( TOTAL_DISTANCE  )))/1000)+ " Km");
                if(jsonObject.getString("totaldistance").equals("0")){
                    chart.setNoDataText(getResources().getString(R.string.no_chart_data_available));
                    //StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes,0);
                }else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        speed.add(jsonList.getString(WEEK_DISTANCE));
                        dateDes.add(jsonList.getString(MONTH));
                    }
                    StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes, 1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void callyear_Api() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardYear(AppPreference.getPrefsHelper().getPref(Contents.USER_PROFILE_REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    yearReportapiresult(bodyString);
                    //reportapiresult(bodyString);
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

    private void yearReportapiresult(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(DASH_BOARD_YEAR);
                JSONArray jsonArray = new JSONArray(data1);
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList year = new ArrayList();
                average_speed.setText(jsonObject.getString(AVERAGESPEED ));
                average_km.setText(""+  String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString(AVERAGE_DISTANCE )))/1000 )+" Km");
                total_km.setText(""+ String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString( TOTAL_DISTANCE  )))/1000)+ " Km");
                if(jsonObject.getString("totaldistance").equals("0")){
                    chart.setNoDataText(getResources().getString(R.string.no_chart_data_available));
                    //StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes,0);
                }else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        speed.add(jsonList.getString(WEEK_DISTANCE));
                        dateDes.add(jsonList.getString(MONTH));
                    }
                    StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes, 1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void weekReportapiresult(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");

            if (status.trim().equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(DASH_BOARD_WEEK);
                JSONArray jsonArray = new JSONArray(data1);
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList year = new ArrayList();
                average_speed.setText(jsonObject.getString(AVERAGESPEED ));
                average_km.setText(""+  String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString(AVERAGE_DISTANCE )))/1000 )+" Km");
                total_km.setText(""+ String.format("%.2f",Utils.roundTwoDecimals(Double.parseDouble(jsonObject.getString( TOTAL_DISTANCE  )))/1000)+ " Km");
                if(jsonObject.getString("totaldistance").equals("0")){
                    chart.setNoDataText(getResources().getString(R.string.no_chart_data_available));
                    //StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes,0);
                }else{
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        speed.add(jsonList.getString(WEEK_DISTANCE));
                        dateDes.add(jsonList.getString(DAY));
                    }
                    StaticsUtils_BarChart.setBarChartData(getActivity(), chart, dateDes.size(), speed, dateDes,0);
                }

            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_dash_board_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
