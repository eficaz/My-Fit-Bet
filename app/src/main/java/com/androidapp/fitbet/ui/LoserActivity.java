package com.androidapp.fitbet.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.FILE_PATH;
import static com.androidapp.fitbet.utils.Contents.FILE_TYPE;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.WINNER_description;

public class LoserActivity extends BaseActivity {
    Bundle bundle;
    String regNo="";
    String betId="";
    String winName="";
    String won="";
    String file_Type="";

    @Bind(R.id.userPostion)
    TextView userPostion;

    @Bind(R.id.img_user)
    CircleImageView img_user;

    @Bind(R.id.img_type)
    CircleImageView img_type;

    @Bind(R.id.video_type)
    CircleImageView video_type;

    @Bind(R.id.tv_Name)
    TextView tv_Name;

    @Bind(R.id.skip)
    TextView skip;

    @Bind(R.id.wiiner_discreption)
    LinearLayout wiiner_discreption;

    @Bind(R.id.tv_credit)
    TextView tv_credit;

    String imagepath;
    String videopath;

    String uaser_image,winer_name,credit,winner_description;
private AppPreference appPreference;

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
        setContentView(R.layout.activity_loser);
        ButterKnife.bind(this);
        appPreference=AppPreference.getPrefsHelper(this);
        bundle = getIntent().getExtras();
        betId=bundle.getString(MYBETS_betid);
        clearSavedBetItems();
        if(!betId.equals("")){
            CustomProgress.getInstance().showProgress(LoserActivity.this, "", false);
            loserDetail();
        }
        img_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final Dialog dialog = new Dialog(LoserActivity.this,android.R.style.Theme_NoTitleBar);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.imageview_dilog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    final ImageView image = (ImageView) dialog.findViewById(R.id.my_image);
                    final ImageView btn_close = (ImageView) dialog.findViewById(R.id.btn_close);
                    RelativeLayout videorow = (RelativeLayout) dialog.findViewById(R.id.videorow);
                    videorow.setVisibility(View.GONE);
                    btn_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Picasso.get().
                            load(imagepath)
                            .placeholder(R.drawable.image_loader)
                            .into(image);
                    dialog.show();
                }
                catch(Exception ex){
                    Log.e("Exception",ex.toString());
                }
            }
        });
        video_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoserActivity.this, Videoplayer.class);
                i.putExtra("url",videopath);
                startActivity(i);
            }
        });
        wiiner_discreption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final Dialog dialog = new Dialog(LoserActivity.this,android.R.style.Theme_NoTitleBar);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.archive_list_details_dilog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    final CircleImageView img_user = (CircleImageView) dialog.findViewById(R.id.img_user);
                    final TextView name = (TextView) dialog.findViewById(R.id.name);
                    final TextView credit1 = (TextView) dialog.findViewById(R.id.credit);
                    final TextView discreption = (TextView) dialog.findViewById(R.id.discreption);
                    name.setText(winer_name);
                    credit1.setText(credit);
                    discreption.setText(winner_description);
                    if(uaser_image.equals("")){
                        img_user.setImageDrawable(getResources().getDrawable(R.drawable.user_profile_avatar));
                    }else{
                        Picasso.get().load(uaser_image)
                                .placeholder(R.drawable.image_loader)
                                .into(img_user);
                    }
                    final ImageView btn_close = (ImageView) dialog.findViewById(R.id.btn_close);
                    // RelativeLayout videorow = (RelativeLayout) dialog.findViewById(R.id.videorow);
                    //videorow.setVisibility(View.GONE);
                    btn_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                catch(Exception ex){
                    Log.e("Exception",ex.toString());
                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(DASH_BOARD_POSICTION,"0");
                startActivity(new Intent(LoserActivity.this,DashBoardActivity.class));
                finish();
            }
        });
    }

    private void clearSavedBetItems() {
        appPreference.saveDistance("0.0");
        appPreference.savedStatusFlag(false);
        appPreference.saveUserRoute("");
        appPreference.saveOrigin("");
        appPreference.setLatLongList(null);
    }
    private void loserDetail() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).LoserDetail(betId,AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject;
                    jsonObject = new JSONObject(bodyString);
                 System.out.println("Loser details = "+bodyString);
                    String data = jsonObject.getString("Status");
                    CustomProgress.getInstance().hideProgress();
                    if(data.equals("Ok")){

                        tv_Name.setText(jsonObject.getString("winner_name"));
                        tv_credit.setText(jsonObject.getString("winner_credit"));
                        userPostion.setText(jsonObject.getString("user_position"));
                        if (!jsonObject.getString("winner_profile_pic").equals("NA")) {
                            if (jsonObject.getString(IMAGE_STATUS).equals("0")) {
                                Picasso.get().load(Constant.BASE_APP_IMAGE__PATH + jsonObject.getString("winner_profile_pic"))
                                        .placeholder(R.drawable.image_loader)
                                        .into(img_user);
                            }else{
                                Picasso.get().load(jsonObject.getString("winner_profile_pic"))
                                        .placeholder(R.drawable.image_loader)
                                        .into(img_user);
                            }
                        }else{
                            img_user.setImageDrawable(getDrawable(R.drawable.user_profile_avatar));
                        }
                        if(jsonObject.getString(WINNER_description).equals("")){
                            wiiner_discreption.setVisibility(View.GONE);
                        }else{
                            wiiner_discreption.setVisibility(View.VISIBLE);
                        }
                        if(jsonObject.getString(FILE_TYPE).equals("image")){
                            imagepath=""+Constant.BASE_APP_WINNER_IMAGE__PATH+jsonObject.getString(FILE_PATH);
                            String fileType="0";
                            video_type.setVisibility(View.GONE);
                            img_type.setVisibility(View.VISIBLE);
                        }else if(jsonObject.getString(FILE_TYPE).equals("video")){
                            videopath=""+Constant.BASE_APP_WINNER_IMAGE__PATH+jsonObject.getString(FILE_PATH);
                            String fileType="1";
                            video_type.setVisibility(View.VISIBLE);
                            img_type.setVisibility(View.GONE);
                        }else{
                            video_type.setVisibility(View.GONE);
                            img_type.setVisibility(View.GONE);
                        }
                        winer_name=jsonObject.getString("winner_name");
                        credit=jsonObject.getString("winner_credit");
                        winner_description=jsonObject.getString(WINNER_description);
                        if (!jsonObject.getString("winner_profile_pic").equals("NA")) {
                            if (jsonObject.getString("winner_reg_type").equals("normal")&&jsonObject.getString("winner_image_status").equals("0")){
                                uaser_image=Constant.BASE_APP_IMAGE__PATH+jsonObject.getString("winner_profile_pic");
                            }else{
                                uaser_image=jsonObject.getString("winner_profile_pic");
                            }
                        }else{
                            uaser_image="";
                        }
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
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        CustomProgress.getInstance().hideProgress();
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(LoserActivity.this);
        builder.setMessage("Are you sure want to leave this page?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
}
