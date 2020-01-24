package com.androidapp.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.Archives;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.adapters.ArchivesListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.RecyclerItemClickListener;
import com.androidapp.fitbet.utils.SLApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.DESCRIPTION;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.MYBETS;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betname;
import static com.androidapp.fitbet.utils.Contents.MYBETS_bettype;
import static com.androidapp.fitbet.utils.Contents.MYBETS_createdate;
import static com.androidapp.fitbet.utils.Contents.MYBETS_createdby;
import static com.androidapp.fitbet.utils.Contents.MYBETS_credit;
import static com.androidapp.fitbet.utils.Contents.MYBETS_date;
import static com.androidapp.fitbet.utils.Contents.MYBETS_enddate;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlocation;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlongitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_route;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlocation;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_status;
import static com.androidapp.fitbet.utils.Contents.MYBETS_winner;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.TOTAL;

public class JoinBetsActivity extends BaseActivity {


    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.searchview)
    EditText searchView;

    ArrayList<Archives> archivesListDetails;

    ArchivesListAdapter archivesListAdapter;


    @Override
    protected void onMessageReceived(String message) {
        super.onMessageReceived(message);
        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_bets);
        ButterKnife.bind(this);
        //bundle = getIntent().getExtras();
        archivesGroupList();
        CustomProgress.getInstance().showProgress(this, "", false);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinBetsActivity.this, DashBoardActivity.class);
                startActivity(intent);
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
                    filter(" ");
                } else {
                    filter(editable.toString());
                }

            }
        });
        invite_group_list.addOnItemTouchListener(new RecyclerItemClickListener(SLApplication.getContext(), invite_group_list, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(JoinBetsActivity.this, ArchiveDetailsActivity.class);
                i.putExtra(Contents.MYBETS_betid, archivesListDetails.get(position).getBetid());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

        }));
    }

    private void filter(String text) {
        if (text.equals("")) {
            archivesListAdapter.filterList(" ");
        } else {
            archivesListAdapter.filterList(text);
        }

    }

    private void archivesGroupList() {
        String search = "";
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).JoinBetList(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                   // groupDetailsList(bodyString);
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
                archivesListAdapter = new ArchivesListAdapter(this, archivesListDetails);
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(this));
                invite_group_list.setAdapter(archivesListAdapter);
                CustomProgress.getInstance().hideProgress();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
        finish();
    }
}