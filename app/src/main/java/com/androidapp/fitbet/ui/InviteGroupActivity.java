package com.androidapp.fitbet.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.Invitemembers;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.adapters.InviteDetailsListAdapter;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.RecyclerItemClickListener;
import com.androidapp.fitbet.utils.SLApplication;
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

import static com.androidapp.fitbet.utils.Contents.BASEPATH;
import static com.androidapp.fitbet.utils.Contents.GROUP_IMAGE;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;

public class InviteGroupActivity extends BaseActivity {


    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.no_data)
    TextView no_data;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.btn_invite)
    Button btn_invite;

    @Bind(R.id.delete)
    ImageView delete;


    @Bind(R.id.img_user)
    CircleImageView img_user;

    @Bind(R.id.users_count)
    TextView users_count;

    @Bind(R.id.searchview)
    EditText searchView;

    @Bind(R.id.ed_delete_row)
    TableRow ed_delete_row;

    Bundle bundle;
    boolean isEditable = false;
    boolean clcikable = false;

    ArrayList<Invitemembers> invitemembersDetails;

    ArrayList<Invitemembers> multiselect_list = new ArrayList<>();

    boolean isMultiSelect = false;

    InviteDetailsListAdapter inviteListAdapter;


    private IntentFilter filter = new IntentFilter("count_down");
    private boolean firstConnect = true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (firstConnect) {
                    firstConnect = false;

                    String message = intent.getStringExtra("message");
                    onMessageReceived(message);

                }
            } else {
                firstConnect = true;
            }

        }
    };

    @Override
    public void onMessageReceived(String message) {

        SLApplication.isCountDownRunning = true;
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InviteGroupActivity.this, CreateGroupActivity.class));
                finish();
            }
        });
        bundle = getIntent().getExtras();
        inviteGroupList();

       /* invite_group_list.addOnItemTouchListener(new RecyclerItemClickListener(SLApplication.getContext(), invite_group_list, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //isMultiSelect = false;
            }
            @Override
            public void onItemLongClick(View view, int position) {
                    if (!isMultiSelect) {
                        multiselect_list = new ArrayList<Invitemembers>();
                        isMultiSelect = true;
                    }
                    multi_select(position);
                }

        }));*/


        invite_group_list.addOnItemTouchListener(new RecyclerItemClickListener(SLApplication.getContext(), invite_group_list, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //isMultiSelect;
              /*  if(!clcikable==true){
                    Intent i = new Intent(getActivity(), InviteGroupActivity.class);
                    i.putExtra(Contents.GROUP_ID,groupDetails.get(position).getGroupid());
                    startActivity(i);
                    getActivity().finish();
                }
                else if(multiselect_list.size()==0){
                    Intent i = new Intent(getActivity(), InviteGroupActivity.class);
                    i.putExtra(Contents.GROUP_ID,groupDetails.get(position).getGroupid());
                    startActivity(i);
                    getActivity().finish();
                }*/
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    clcikable = true;
                    multiselect_list = new ArrayList<Invitemembers>();
                    isMultiSelect = true;
                } else {

                }
                multi_select(position);
            }
        }));


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deleteId = null;
                ArrayList<String> deleteIdList = new ArrayList<String>();
                if (multiselect_list.size() > 0) {
                    for (int i = 0; i < multiselect_list.size(); i++) {
                        invitemembersDetails.remove(multiselect_list.get(i));
                        deleteId = String.valueOf(multiselect_list.get(i).getReg_key());
                        deleteIdList.add(deleteId);
                    }
                    callDeleteGroupApi(deleteIdList.toString(), bundle.getString(Contents.GROUP_ID));
                } else {
                    for (int i = 0; i < multiselect_list.size(); i++) {
                        invitemembersDetails.remove(multiselect_list.get(i));
                        deleteId = String.valueOf(multiselect_list.get(i).getReg_key());
                        deleteIdList.add(deleteId);
                    }
                    callDeleteGroupApi(deleteIdList.toString(), bundle.getString(Contents.GROUP_ID));
                }
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InviteGroupActivity.this, CreateGroupActivity.class));
        finish();
        super.onBackPressed();
    }

    private void filter(String text) {
        if (!text.equals("")) {
            inviteListAdapter.filterList(text);
        } else {
            inviteListAdapter.filterList("");
        }

    }

    private void callDeleteGroupApi(String deletearray, String groupid) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DeleteInviteMember(deletearray, groupid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject = new JSONObject(bodyString);
                    String data = jsonObject.getString("Status");
                    if (data.equals("Ok")) {
                        refreshAdapter();
                        delete.setVisibility(View.GONE);
                        searchView.setVisibility(View.VISIBLE);
                        searchView.setText("");
                        ed_delete_row.setVisibility(View.GONE);
                        users_count.setText("");
                        multiselect_list.clear();
                        inviteGroupList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }


    public void multi_select(int position) {
        if (multiselect_list.contains(invitemembersDetails.get(position))) {
            multiselect_list.remove(invitemembersDetails.get(position));
        } else {
            multiselect_list.add(invitemembersDetails.get(position));
        }
        if (multiselect_list.size() > 0) {
            if (multiselect_list.size() > 1) {
                ed_delete_row.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
            } else {
                ed_delete_row.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);

            }
        } else {
            delete.setVisibility(View.GONE);
            ed_delete_row.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }
        users_count.setText("" + multiselect_list.size());
        refreshAdapter();
    }


    /* public void multi_select(int position) {
         if (multiselect_list.contains(invitemembersDetails.get(position))) {
             multiselect_list.remove(invitemembersDetails.get(position));
         }
         else {
             multiselect_list.add(invitemembersDetails.get(position));
         }
         if (multiselect_list.size() > 0){
             if(multiselect_list.size()>1){
                 delete.setVisibility(View.VISIBLE);
                 searchView.setVisibility(View.GONE);
             } else{
                 delete.setVisibility(View.VISIBLE);
                 searchView.setVisibility(View.GONE);

             }
         }else{
             delete.setVisibility(View.GONE);
             searchView.setVisibility(View.VISIBLE);
         }
         refreshAdapter();

     }*/
    public void refreshAdapter() {
        inviteListAdapter.selected_usersList = multiselect_list;
        inviteListAdapter.groupListModels = invitemembersDetails;
        inviteListAdapter.notifyDataSetChanged();
    }

    private void inviteGroupList() {
        CustomProgress.getInstance().showProgress(this, "", false);
        if (bundle.getString(Contents.GROUP_ID) != null || bundle.getString(Contents.GROUP_ID) != "") {
            btn_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(InviteGroupActivity.this, InviteGroupListActivity.class);
                    i.putExtra(Contents.GROUP_ID, bundle.getString(Contents.GROUP_ID));
                    startActivity(i);
                }
            });

            String search = "";
            Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).InviteGroupDeatails(bundle.getString(Contents.GROUP_ID));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String bodyString = new String(response.body().bytes(), "UTF-8");
                        CustomProgress.getInstance().hideProgress();
                        groupDetailsList(bodyString, bundle.getString(Contents.GROUP_ID));
                        //listOrderGroup(bodyString);
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

    private void groupDetailsList(String bodyString, String groupId) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if (data.equals("Ok")) {
                name.setText(jsonObject.getString("name"));
                description.setText(jsonObject.getString("description"));

                if (!jsonObject.getString(GROUP_IMAGE).equals("NA")) {
                    if (jsonObject.getString(GROUP_IMAGE).equals("normal") && jsonObject.getString(IMAGE_STATUS).equals("0")) {
                        Picasso.get().
                                load(jsonObject.getString(BASEPATH) + jsonObject.getString(GROUP_IMAGE))
                                .placeholder(R.drawable.image_loader)
                                .into(img_user);
                     /* Glide.with(this)
                              .load(jsonObject.getString(BASEPATH)+jsonObject.getString(GROUP_IMAGE))
                              .centerCrop()
                              .placeholder(R.drawable.group_icons)
                              .into(img_user);*/

                     /* Glide.with(this)
                              .load( jsonObject.getString(BASEPATH)+jsonObject.getString(GROUP_IMAGE))
                              .listener(new RequestListener<String, GlideDrawable>() {
                                  @Override
                                  public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                      img_user.setImageDrawable(getResources().getDrawable(R.drawable.group_icons));
                                      return false;
                                  }
                                  @Override
                                  public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                      return false;
                                  }
                              })
                              .transform(new CircleTransform(this))
                              .into(img_user);*/
                    } else {


                     /* Glide.with(this)
                              .load(jsonObject.getString(BASEPATH)+jsonObject.getString(GROUP_IMAGE))
                              .centerCrop()
                              .placeholder(R.drawable.group_icons)
                              .into(img_user);*/


                        Picasso.get().
                                load(jsonObject.getString(BASEPATH) + jsonObject.getString(GROUP_IMAGE))
                                .placeholder(R.drawable.image_loader)
                                .into(img_user);
                     /* Glide.with(this)
                              .load( jsonObject.getString(BASEPATH)+jsonObject.getString(GROUP_IMAGE))
                              .listener(new RequestListener<String, GlideDrawable>() {
                                  @Override
                                  public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                      img_user.setImageDrawable(getResources().getDrawable(R.drawable.group_icons));
                                      return false;
                                  }
                                  @Override
                                  public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                      return false;
                                  }
                              })
                              .transform(new CircleTransform(this))
                              .into(img_user);*/
                    }
                } else {
                    img_user.setImageDrawable(getResources().getDrawable(R.drawable.group_icons));
                }
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString("groupmembers");
                String basepath = jsonObject1.getString(BASEPATH);
                JSONArray jsonArray = new JSONArray(data1);
                invitemembersDetails = new ArrayList<>();
                invitemembersDetails.clear();
                if (jsonArray.length() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    Invitemembers model = new Invitemembers();
                    model.setReg_key(jsonList.getString(Contents.REG_KEY));
                    model.setFirstname(jsonList.getString(Contents.FIRST_NAME));
                    model.setEmail(jsonList.getString(Contents.EMAIL));
                    model.setCreditScore(jsonList.getString(Contents.CREDIT_SCORE));
                    model.setWon(jsonList.getString(Contents.WON));
                    model.setLost(jsonList.getString(Contents.LOST));
                    model.setCountry(jsonList.getString(Contents.COUNTRY));
                    model.setProfile_pic(jsonList.getString(Contents.PROFILE_PIC));
                    model.setRegType(jsonList.getString(Contents.REG_TYPE));
                    model.setDistance(jsonList.getString(Contents.DISTANCE));
                    invitemembersDetails.add(model);
                }
                inviteListAdapter = new InviteDetailsListAdapter(this, invitemembersDetails, multiselect_list, groupId, basepath);
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(this));
                invite_group_list.setAdapter(inviteListAdapter);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
