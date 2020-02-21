package com.androidapp.fitbet.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.Message;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.adapters.MessageListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.FIRST_NAME;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.MESSAGE;
import static com.androidapp.fitbet.utils.Contents.MESSAGES;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betname;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.SENDDATE;
import static com.androidapp.fitbet.utils.Contents.SENDER;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.TOTAL_PARTICIPANTS;

public class MessageActivity extends BaseActivity {

    Bundle bundle;
    String betId = "";

    ArrayList<Message> messageList;

    MessageListAdapter betDetailsAdapter;

    @Bind(R.id.invite_group_list)
    RecyclerView bet_list;

    @Bind(R.id.back_btn)
    LinearLayout back_btn;

    @Bind(R.id.chat_row)
    LinearLayout chat_row;

    @Bind(R.id.message_row)
    ConstraintLayout message_row;

    @Bind(R.id.send_button)
    ImageButton send_button;

    @Bind(R.id.ed_message)
    EditText ed_message;

    @Bind(R.id.groupName)
    TextView groupName;

    @Bind(R.id.groupCount)
    TextView groupCount;

    @Bind(R.id.space)
    Space space;

    @Bind(R.id.no_data)
    TextView no_data;
    String chant = "";


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
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        bundle = getIntent().getExtras();

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        callMessageListApi();
        CustomProgress.getInstance().showProgress(this, "", false);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectedToInternet(MessageActivity.this)) {
                    if (!ed_message.getText().toString().equals("")) {
                        chant = ed_message.getText().toString();
                        sendMessageAPi();
                        ed_message.setText("");
                    } else {
                        Utils.showCustomToastMsg(MessageActivity.this, R.string.please_type_message);
                    }
                } else {
                    Utils.showCustomToastMsg(MessageActivity.this, R.string.no_internet);
                }

            }
        });
        groupName.setText(bundle.getString(MYBETS_betname));
        groupCount.setText(bundle.getString(TOTAL_PARTICIPANTS));

        /*ed_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 0) {
                    if (!ed_message.getText().toString().trim().equals("")) {
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
        });*/
        message_row.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                message_row.getWindowVisibleDisplayFrame(r);
                int screenHeight = message_row.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    space.setVisibility(View.VISIBLE);
                    chat_row.setWeightSum(100f);
                } else {
                    space.setVisibility(View.GONE);
                }
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

    private void sendMessageAPi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).SendMessage(bundle.getString(MYBETS_betid), AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""), ed_message.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    MessageSendDeials(bodyString);
                    MessageDetailsList(bodyString);

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

    private void MessageSendDeials(String bodyString) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if (data.equals("Ok")) {
                ed_message.setText("");
                betDetailsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callMessageListApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).MessageList(bundle.getString(MYBETS_betid));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    MessageDetailsList(bodyString);
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

    private void MessageDetailsList(String bodyString) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if (data.equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MESSAGES);
                JSONArray jsonArray = new JSONArray(data1);
                messageList = new ArrayList<>();
                messageList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    Message model = new Message();
                    model.setFirstname(jsonList.getString(FIRST_NAME));
                    model.setProfile_pic(jsonList.getString(PROFILE_PIC));
                    model.setRegType(jsonList.getString(REG_TYPE));
                    model.setMessage(jsonList.getString(MESSAGE));
                    model.setSenddate(jsonList.getString(SENDDATE));
                    model.setSender(jsonList.getString(SENDER));
                    model.setImage_status(jsonList.getString(IMAGE_STATUS));
                    messageList.add(model);
                }
                Collections.reverse(messageList);
                betDetailsAdapter = new MessageListAdapter(this, messageList, bundle.getString(MYBETS_betid));
                bet_list.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                bet_list.setLayoutManager(linearLayoutManager);
                bet_list.setAdapter(betDetailsAdapter);
                bet_list.findViewHolderForAdapterPosition(messageList.size());
                betDetailsAdapter.notifyDataSetChanged();
                if (jsonArray.length() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
