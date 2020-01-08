package com.eficaz_fitbet_android.fitbet.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.model.Archives;
import com.eficaz_fitbet_android.fitbet.model.MessageChatListUpCommingBets;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.adapters.MessageArchivesListAdapter;
import com.eficaz_fitbet_android.fitbet.ui.adapters.MessageUpcommingListAdapter;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.Contents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.DESCRIPTION;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DISTANCE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betid;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betname;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_bettype;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_createdate;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_createdby;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_credit;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_date;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_enddate;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_endlatitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_endlocation;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_endlongitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_route;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlocation;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_status;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_winner;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.STATUS_A;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.TOTAL;

public class MessageListActivity extends BaseActivity {

    @Bind(R.id.row_upcomming)
    TableRow row_upcomming;


    @Bind(R.id.row_archives)
    TableRow row_archives;


    @Bind(R.id.view_line)
    View view_line;

    @Bind(R.id.view_line1)
    View view_line1;

    @Bind(R.id.view_line2)
    View view_line2;

    @Bind(R.id.view_line3)
    View view_line3;

    @Bind(R.id.back_btn)
    TableRow back_btn;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.no_data)
    TextView no_data;

    @Bind(R.id.searchview)
    EditText searchView;

    ArrayList<Archives> archivesListDetails;
    ArrayList<MessageChatListUpCommingBets> messageChatListUpCommingBets;

    MessageArchivesListAdapter archivesListAdapter;

    MessageUpcommingListAdapter messageUpcommingListAdapter;

    boolean findrow=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        view_line.setVisibility(View.VISIBLE);
        view_line1.setVisibility(View.GONE);
        view_line2.setVisibility(View.GONE);
        view_line3.setVisibility(View.VISIBLE);
        intintView();
    }

    private void intintView() {
        upCommingBets();
        CustomProgress.getInstance().showProgress(MessageListActivity.this, "", false);
        findrow=false;
        row_upcomming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_line.setVisibility(View.VISIBLE);
                view_line1.setVisibility(View.GONE);
                view_line2.setVisibility(View.GONE);
                view_line3.setVisibility(View.VISIBLE);
                upCommingBets();
                CustomProgress.getInstance().showProgress(MessageListActivity.this, "", false);
                findrow=false;
            }
        });

        row_archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_line.setVisibility(View.GONE);
                view_line1.setVisibility(View.VISIBLE);
                view_line2.setVisibility(View.VISIBLE);
                view_line3.setVisibility(View.GONE);
                archivesGroupList();
                CustomProgress.getInstance().showProgress(MessageListActivity.this, "", false);
                findrow=true;
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                if (editable.toString().equals("")) {
                    filter("");
                } else {
                    filter(editable.toString());
                }

            }
        });

    }

    private void upCommingBets() {
        String search = "";
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Listjoinedbet(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    upcomminggroupList(bodyString);
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

    private void upcomminggroupList(String bodyString) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if (data.equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS);
                JSONArray jsonArray = new JSONArray(data1);
                if(jsonArray.length()==0){
                    no_data.setVisibility(View.VISIBLE);
                }else{
                    no_data.setVisibility(View.GONE);
                }
                messageChatListUpCommingBets = new ArrayList<>();
                messageChatListUpCommingBets.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    MessageChatListUpCommingBets model = new MessageChatListUpCommingBets();
                    model.setDistance(jsonList.getString(DISTANCE));
                    model.setDescription(jsonList.getString(DESCRIPTION));
                    model.setCreatedate(jsonList.getString(MYBETS_createdate));
                    model.setRoute(jsonList.getString(MYBETS_route));
                    model.setCredit(jsonList.getString(MYBETS_credit));
                    model.setWinner(jsonList.getString(MYBETS_winner));
                    model.setStatus(jsonList.getString(MYBETS_status));
                    model.setCreatedby(jsonList.getString(MYBETS_createdby));
                    model.setDate(jsonList.getString(MYBETS_date));
                    model.setBetid(jsonList.getString(MYBETS_betid));
                    model.setBetname(jsonList.getString(MYBETS_betname));
                    model.setStartlocation(jsonList.getString(MYBETS_startlocation));
                    model.setEndlocation(jsonList.getString(MYBETS_endlocation));
                    model.setEndlongitude(jsonList.getString(MYBETS_endlongitude));
                    model.setEndlatitude(jsonList.getString(MYBETS_endlatitude));
                    model.setStartlatitude(jsonList.getString(MYBETS_startlatitude));
                    model.setStartlongitude(jsonList.getString(MYBETS_startlongitude));
                    model.setBettype(jsonList.getString(MYBETS_bettype));
                    model.setEnddate(jsonList.getString(MYBETS_enddate));
                    model.setTotal(jsonList.getString(TOTAL));
                    messageChatListUpCommingBets.add(model);
                }
                messageUpcommingListAdapter = new MessageUpcommingListAdapter(this, messageChatListUpCommingBets);
                list.setHasFixedSize(true);
                list.setLayoutManager(new LinearLayoutManager(this));
                list.setAdapter(messageUpcommingListAdapter);
                CustomProgress.getInstance().hideProgress();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void filter(String text) {

        if(findrow==true){
            if (text.equals("")) {
                archivesListAdapter.filterList(" ");
            } else {
                archivesListAdapter.filterList(text);
            }
        }else{
            if (text.equals("")) {
                messageUpcommingListAdapter.filterList(" ");
            } else {
                messageUpcommingListAdapter.filterList(text);
            }
        }
    }
    private void archivesGroupList() {
        String search = "";
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Archivebet(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    groupDetailsList(bodyString);
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

    private void groupDetailsList(String bodyString) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if (data.equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS);
                JSONArray jsonArray = new JSONArray(data1);
                if(jsonArray.length()==0){
                    no_data.setVisibility(View.VISIBLE);
                }else{
                    no_data.setVisibility(View.GONE);
                }
                archivesListDetails = new ArrayList<>();
                archivesListDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    Archives model = new Archives();
                    model.setDistance(jsonList.getString(DISTANCE));
                    model.setDescription(jsonList.getString(DESCRIPTION));
                    model.setCreatedate(jsonList.getString(MYBETS_createdate));
                    model.setRoute(jsonList.getString(MYBETS_route));
                    model.setCredit(jsonList.getString(MYBETS_credit));
                    model.setWinner(jsonList.getString(MYBETS_winner));
                    model.setStatus(jsonList.getString(MYBETS_status));
                    model.setCreatedby(jsonList.getString(MYBETS_createdby));
                    model.setDate(jsonList.getString(MYBETS_date));
                    model.setBetid(jsonList.getString(MYBETS_betid));
                    model.setBetname(jsonList.getString(MYBETS_betname));
                    model.setStartlocation(jsonList.getString(MYBETS_startlocation));
                    model.setEndlocation(jsonList.getString(MYBETS_endlocation));
                    model.setEndlongitude(jsonList.getString(MYBETS_endlongitude));
                    model.setEndlatitude(jsonList.getString(MYBETS_endlatitude));
                    model.setStartlatitude(jsonList.getString(MYBETS_startlatitude));
                    model.setStartlongitude(jsonList.getString(MYBETS_startlongitude));
                    model.setBettype(jsonList.getString(MYBETS_bettype));
                    model.setEnddate(jsonList.getString(MYBETS_enddate));
                    model.setTotal(jsonList.getString(TOTAL));
                    archivesListDetails.add(model);
                }
                archivesListAdapter = new MessageArchivesListAdapter(this, archivesListDetails);
                list.setHasFixedSize(true);
                list.setLayoutManager(new LinearLayoutManager(this));
                list.setAdapter(archivesListAdapter);
                CustomProgress.getInstance().hideProgress();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
