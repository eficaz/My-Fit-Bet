package com.eficaz_fitbet_android.fitbet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.model.Invitemembers;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.adapters.InviteListAdapter;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.BASEPATH;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.COUNTRY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.CREDIT_SCORE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DISTANCE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.EMAIL;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.FIRST_NAME;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.GROUP_ID;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.IMAGE_STATUS;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.LOST;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.PROFILE_PIC;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_KEY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_TYPE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.WON;

public class InviteGroupListActivity extends BaseActivity {

    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.searchview)
    EditText searchView;

    ArrayList<Invitemembers>invitemembersDetails;

    ArrayList<Invitemembers> multiselect_list = new ArrayList<>();

    InviteListAdapter inviteListAdapter;

    Bundle bundle;

    String group_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_list);
        ButterKnife.bind(this);
        bundle = getIntent().getExtras();
        group_id=bundle.getString(Contents.GROUP_ID);
        if (Utils.isConnectedToInternet(InviteGroupListActivity.this)){
            CustomProgress.getInstance().showProgress(InviteGroupListActivity.this, "", false);
            inviteGroupList();
        } else{
            Utils.showCustomToastMsg(InviteGroupListActivity.this, R.string.no_internet);
        }
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InviteGroupListActivity.this, InviteGroupActivity.class);
                i.putExtra(Contents.GROUP_ID,group_id);
                startActivity(i);
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
                filter(editable.toString());
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(InviteGroupListActivity.this, InviteGroupActivity.class);
        i.putExtra(Contents.GROUP_ID,group_id);
        startActivity(i);
        finish();

    }

    private void filter(String text) {
        inviteListAdapter.filterList(text);
    }
    private void inviteGroupList() {
        final Bundle bundle = getIntent().getExtras();
        if(bundle.getString(GROUP_ID)!= null ||bundle.getString(GROUP_ID)!= "")
        {
            String search="";
            Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).InviteGroupList(bundle.getString(GROUP_ID),"");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String bodyString = new String(response.body().bytes(), "UTF-8");
                        CustomProgress.getInstance().hideProgress();
                        CustomProgress.getInstance().showProgress(InviteGroupListActivity.this, "", false);
                        groupDetailsList(bodyString,bundle.getString(GROUP_ID));
                        //listOrderGroup(bodyString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }else{

        }
    }
    private void groupDetailsList(String bodyString,String groupId) {
        try{
            final JSONObject jsonObject = new JSONObject(bodyString);
            CustomProgress.getInstance().hideProgress();
            String data = jsonObject.getString("Status");
            //String msg = jsonObject.getString("Msg");
            //Utils.showCustomToastMsg(InviteGroupListActivity.this, msg);
            if(data.equals("Ok")){
                CustomProgress.getInstance().hideProgress();
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString("invitemembers");
                String basepath=jsonObject1.getString(BASEPATH);
                JSONArray jsonArray = new JSONArray(data1);
                invitemembersDetails = new ArrayList<>();
                invitemembersDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    Invitemembers model = new Invitemembers();
                    model.setReg_key(jsonList.getString(REG_KEY));
                    model.setFirstname(jsonList.getString(FIRST_NAME));
                    model.setEmail(jsonList.getString(EMAIL));
                    model.setCreditScore(jsonList.getString(CREDIT_SCORE));
                    model.setWon(jsonList.getString(WON));
                    model.setLost(jsonList.getString(LOST));
                    model.setCountry(jsonList.getString(COUNTRY));
                    model.setProfile_pic(jsonList.getString(PROFILE_PIC));
                    model.setImage_status(jsonList.getString(IMAGE_STATUS));
                    model.setRegType(jsonList.getString(REG_TYPE));
                    model.setDistance(jsonList.getString(DISTANCE));
                    invitemembersDetails.add(model);
                }
                inviteListAdapter = new InviteListAdapter(this, invitemembersDetails,multiselect_list,groupId,basepath);
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(this));
                invite_group_list.setAdapter(inviteListAdapter);
                //CustomProgress.getInstance().hideProgress();
            }else{
                CustomProgress.getInstance().hideProgress();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
