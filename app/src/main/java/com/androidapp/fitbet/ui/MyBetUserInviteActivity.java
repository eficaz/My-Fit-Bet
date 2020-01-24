package com.androidapp.fitbet.ui;

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
import com.androidapp.fitbet.model.Invitemembers;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.adapters.MybetUserInviteAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.COUNTRY;
import static com.androidapp.fitbet.utils.Contents.CREDIT_SCORE;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.EMAIL;
import static com.androidapp.fitbet.utils.Contents.FIRST_NAME;
import static com.androidapp.fitbet.utils.Contents.GROUP_ID;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.LOST;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betindividuals;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_KEY;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.WON;

public class MyBetUserInviteActivity extends BaseActivity {

    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.searchview)
    EditText searchView;

    ArrayList<Invitemembers> invitemembersDetails;

    ArrayList<Invitemembers> multiselect_list = new ArrayList<>();


    MybetUserInviteAdapter inviteListAdapter;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bet_user_invite);
        ButterKnife.bind(this);
        bundle =  getIntent().getExtras();
        inviteGroupList();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(MyBetUserInviteActivity.this,DashBoardActivity.class);
                startActivity(intent);*/
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
    private void filter(String text) {
if(inviteListAdapter!=null)
        inviteListAdapter.filterList(text);
    }
    private void inviteGroupList() {
        CustomProgress.getInstance().showProgress(MyBetUserInviteActivity.this, "", false);
        final Bundle bundle = getIntent().getExtras();
        if(bundle.getString(GROUP_ID)!= null ||bundle.getString(GROUP_ID)!= "")
        {
            String search="";
            Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).MybetsUserInvite(bundle.getString(GROUP_ID));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String bodyString = new String(response.body().bytes(), "UTF-8");
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
            String data = jsonObject.getString(STATUS_A);
            String betid = jsonObject.getString(MYBETS_betid);
            if(data.equals("Ok")){
                CustomProgress.getInstance().hideProgress();
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS_betindividuals);
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
                    model.setRegType(jsonList.getString(REG_TYPE));
                    model.setDistance(jsonList.getString(DISTANCE));
                    model.setImage_status(jsonList.getString(IMAGE_STATUS));
                    invitemembersDetails.add(model);
                }
                inviteListAdapter = new MybetUserInviteAdapter(this, invitemembersDetails,multiselect_list,betid);
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(this));
                invite_group_list.setAdapter(inviteListAdapter);
                inviteListAdapter.notifyDataSetChanged();
            }else{
                CustomProgress.getInstance().hideProgress();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* Intent intent=new Intent(this,DashBoardActivity.class);
        startActivity(intent);*/
        finish();
    }
}