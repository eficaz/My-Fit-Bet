package com.androidapp.fitbet.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.ArchivesDetails;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.adapters.ArchiveDetailsListAdapter;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.SLApplication;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
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

import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_COUNTRY;
import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_CREDIT_SCORE;
import static com.androidapp.fitbet.utils.Contents.DESCRIPTION;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.EMAIL;
import static com.androidapp.fitbet.utils.Contents.FILE_PATH;
import static com.androidapp.fitbet.utils.Contents.FILE_TYPE;
import static com.androidapp.fitbet.utils.Contents.FIRST_NAME;
import static com.androidapp.fitbet.utils.Contents.IMAGE_STATUS;
import static com.androidapp.fitbet.utils.Contents.LOST;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betname;
import static com.androidapp.fitbet.utils.Contents.MYBETS_bettype;
import static com.androidapp.fitbet.utils.Contents.MYBETS_credit;
import static com.androidapp.fitbet.utils.Contents.MYBETS_date;
import static com.androidapp.fitbet.utils.Contents.MYBETS_distance;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlocation;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlongitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_route;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlocation;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.androidapp.fitbet.utils.Contents.PARTICIPANT;
import static com.androidapp.fitbet.utils.Contents.POSITION;
import static com.androidapp.fitbet.utils.Contents.POSITION_LATITUDE;
import static com.androidapp.fitbet.utils.Contents.POSITION_LONGITUDE;
import static com.androidapp.fitbet.utils.Contents.PROFILE_PIC;
import static com.androidapp.fitbet.utils.Contents.REG_KEY;
import static com.androidapp.fitbet.utils.Contents.REG_TYPE;
import static com.androidapp.fitbet.utils.Contents.START_DATE;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.TOTAL_MESSAGE;
import static com.androidapp.fitbet.utils.Contents.TOTAL_PARTICIPANTS;
import static com.androidapp.fitbet.utils.Contents.WINNER_CREDIT;
import static com.androidapp.fitbet.utils.Contents.WINNER_description;
import static com.androidapp.fitbet.utils.Contents.WON;

public class ArchiveDetailsActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    Bundle bundle;
    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.group_name)
    TextView group_name;

    @Bind(R.id.participants)
    TextView participants;

    @Bind(R.id.messages)
    TextView messages;

    @Bind(R.id.wiinerName)
    TextView wiinerName;

    @Bind(R.id.wiiner_discreption)
    CircleImageView wiiner_discreption;

    @Bind(R.id.country)
    TextView country;

    @Bind(R.id.discreption)
    TextView discreption;

    @Bind(R.id.calander)
    TextView calander;

    @Bind(R.id.clock)
    TextView clock;

    @Bind(R.id.credits)
    TextView credits;

    @Bind(R.id.user_count)
    TextView user_count;

    @Bind(R.id.video_type)
    CircleImageView video_type;

    @Bind(R.id.img_type)
    CircleImageView img_type;

    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.message_row)
    LinearLayout message_row;

    @Bind(R.id.map_row)
    LinearLayout map_row;

    @Bind(R.id.table_winner_card)
    TableRow tableWinnerCard;

    @Bind(R.id.txt_bet_not_completed)
    TextView txtBetNotCompleted;

    String winnerPositionLat ="", winnerPositionLog ="";
    String imagepath;
    String videopath;
    String uaser_image,winer_name,credit,winner_description;
    private MediaController mediaController;
    ProgressDialog progressdialog;
    public static final int Progress_Dialog_Progress = 0;
    URL url;
    URLConnection urlconnection ;
    int FileSize;
    InputStream inputstream;
    OutputStream outputstream;
    byte dataArray[] = new byte[1024];
    long totalSize = 0;
    //ImageView imageview;
    String GetPath ;

    SimpleExoPlayer exoPlayer;

    String startlongitude="",endlongitude="",startlatitude="",endlatitude="";

    String betName="";
    String betid="";
    String bet_TOTAL_PARTICIPANTS="";

    ArrayList<ArchivesDetails> archivesDetailsList;

    ArchiveDetailsListAdapter archiveDetailsListAdapter;
    private String originalRoute;
    private String originalStartLat;
    private String originalStartLog;
    private String originalDistance, originalEndLat, originalEndLog,startAddress,endAddress;


    private IntentFilter filter=new IntentFilter("count_down");
    private boolean firstConnect=true;
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null) {
                if (firstConnect) {
                    firstConnect = false;

                    String message = intent.getStringExtra("message");
                    onMessageReceived(message);

                }
            }else{
                firstConnect=true;
            }

        }
    };

    @Override
    public void onMessageReceived(String message) {

        SLApplication.isCountDownRunning=true;
        startActivity(new Intent(this,DashBoardActivity.class));
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_details);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
        bundle = getIntent().getExtras();
        archivesDetailsGroupList();
        CustomProgress.getInstance().showProgress(this, "", false);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wiiner_discreption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final Dialog dialog = new Dialog(ArchiveDetailsActivity.this,android.R.style.Theme_NoTitleBar);
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
        img_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final Dialog dialog = new Dialog(ArchiveDetailsActivity.this,android.R.style.Theme_NoTitleBar);
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
                Intent i=new Intent(ArchiveDetailsActivity.this, Videoplayer.class);
                i.putExtra("url",videopath);
                startActivity(i);
            }
        });
        message_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ArchiveDetailsActivity.this, MessageActivity.class);
                i.putExtra(Contents.MYBETS_betid,betid);
                i.putExtra(Contents.MYBETS_betname,betName);
                i.putExtra(Contents.TOTAL_PARTICIPANTS,bet_TOTAL_PARTICIPANTS);
                startActivity(i);
            }
        });
        map_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ArchiveDetailsActivity.this, MapRedirectDetailedActivity.class);
                Bundle b=new Bundle();;
               b.putString(Contents.POSITION_LATITUDE, originalEndLat);
                b.putString(Contents.POSITION_LONGITUDE, originalEndLog);
                b.putString(MYBETS_startlongitude,originalStartLog);
                b.putString(MYBETS_startlatitude,originalStartLat);
                b.putString("original route",originalRoute);
                b.putString("original distance",originalDistance);
                b.putString("start address",startAddress);
                b.putString("end address",endAddress);
                System.out.println("Extraaaa "+ originalEndLat +","+ originalEndLog +","+originalStartLog+","+originalStartLat);
                i.putExtras(b);

                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Progress_Dialog_Progress:
                progressdialog = new ProgressDialog(ArchiveDetailsActivity.this);
                progressdialog.setMessage("Downloading Image From Server...");
                progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressdialog.setCancelable(false);
                progressdialog.show();
                return progressdialog;
            default:
                return null;
        }
    }
    private void archivesDetailsGroupList() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Archivebetdetail(bundle.getString(Contents.MYBETS_betid));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    Archivebetdetail(bodyString);
                    System.out.println("Archive details == "+bodyString);
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
    private void Archivebetdetail(String bodyString) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if (data.equals("Ok")) {


                if(jsonObject.getString("winnerkey").equals("no_winner")){
                    tableWinnerCard.setVisibility(View.GONE);
                    txtBetNotCompleted.setVisibility(View.VISIBLE);
                }

                     String data1 = jsonObject.getString(PARTICIPANT);
                     JSONArray jsonArray = new JSONArray(data1);
                     archivesDetailsList = new ArrayList<>();
                     archivesDetailsList.clear();
                     betName=jsonObject.getString(MYBETS_betname);
                     betid=jsonObject.getString(MYBETS_betid);
                     bet_TOTAL_PARTICIPANTS=jsonObject.getString(TOTAL_PARTICIPANTS);
                     group_name.setText(jsonObject.getString(MYBETS_betname));
                     DecimalFormat decimalFormat = new DecimalFormat("#.##");
                     double decimal= Double.parseDouble(String.valueOf(jsonObject.getString(MYBETS_distance)).replace("-",""));
                     String input;
                    if(jsonObject.getString(MYBETS_distance).length()>5){
                         input = String.valueOf(decimal).substring(0,5);
                    }else{
                         input = String.valueOf(decimal);
                    }
                    double numberAsString= Double.parseDouble(input);
                    participants.setText(""+decimalFormat.format(numberAsString/1000));
                    if(jsonObject.getString(WINNER_description).equals("")){
                        wiiner_discreption.setVisibility(View.GONE);
                    }
                    messages.setText(jsonObject.getString(TOTAL_MESSAGE));
                    discreption.setText(jsonObject.getString(DESCRIPTION));
                    credits.setText(jsonObject.getString(MYBETS_credit));
                    user_count.setText(jsonObject.getString(TOTAL_PARTICIPANTS));
                    startlatitude=jsonObject.getString(MYBETS_startlatitude);
                    startlongitude=jsonObject.getString(MYBETS_startlongitude);
                    endlatitude=jsonObject.getString(MYBETS_endlatitude);
                    endlongitude=jsonObject.getString(MYBETS_endlongitude);
                    country.setText(jsonObject.getString(WINNER_CREDIT));
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
                    if(jsonObject.getString(MYBETS_bettype).equals("distance")){
                        map_row.setVisibility(View.GONE);
                        originalRoute=jsonObject.getString("route");
                        originalDistance=jsonObject.getString("distance");

                        originalStartLat=jsonObject.getString("startlatitude");
                        originalStartLog=jsonObject.getString("startlongitude");
                        originalEndLat =jsonObject.getString("endlatitude");
                        originalEndLog =jsonObject.getString("endlongitude");
                        startAddress=jsonObject.getString("startlocation");
                        endAddress=jsonObject.getString("endlocation");

                    }else{
                        map_row.setVisibility(View.VISIBLE);
                        originalRoute=jsonObject.getString("route");
                        originalDistance=jsonObject.getString("distance");

                        originalStartLat=jsonObject.getString("startlatitude");
                        originalStartLog=jsonObject.getString("startlongitude");
                        originalEndLat =jsonObject.getString("endlatitude");
                        originalEndLog =jsonObject.getString("endlongitude");
                        startAddress=jsonObject.getString("startlocation");
                        endAddress=jsonObject.getString("endlocation");
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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        if(jsonList.getString(POSITION).equals("1")){
                            wiinerName.setText(jsonList.getString(FIRST_NAME));
                            winnerPositionLat =jsonList.getString(POSITION_LATITUDE);
                            winnerPositionLog =jsonList.getString(POSITION_LONGITUDE);

                            winer_name=jsonList.getString(FIRST_NAME);
                            credit=jsonObject.getString(WINNER_CREDIT);
                            winner_description=jsonObject.getString(WINNER_description);
                            if (!jsonList.getString(PROFILE_PIC).equals("NA")) {
                                if (jsonList.getString(REG_TYPE).equals("normal")&&jsonList.getString(IMAGE_STATUS).equals("0")){
                                    uaser_image=Constant.BASE_APP_IMAGE__PATH+jsonList.getString(PROFILE_PIC);
                                }else{
                                    uaser_image=jsonList.getString(PROFILE_PIC);
                                }
                            }else{
                                uaser_image="";
                            }
                        }
                        ArchivesDetails model = new ArchivesDetails();
                        model.setPosition(jsonList.getString(POSITION));
                        model.setReg_key(jsonList.getString(REG_KEY));
                        model.setFirstname(jsonList.getString(FIRST_NAME));
                        model.setEmail(jsonList.getString(EMAIL));
                        model.setCreditScore(jsonList.getString(DASH_BOARD_CREDIT_SCORE));
                        model.setWon(jsonList.getString(WON));
                        model.setLost(jsonList.getString(LOST));
                        model.setCountry(jsonList.getString(DASH_BOARD_COUNTRY));
                        model.setProfile_pic(jsonList.getString(PROFILE_PIC));
                        model.setRegType(jsonList.getString(REG_TYPE));
                        model.setImage_status(jsonList.getString(IMAGE_STATUS));
                        model.setDistance(jsonList.getString(DISTANCE));
                        model.setStartdate(jsonList.getString(START_DATE));
                        model.setPositionlongitude(jsonList.getString(POSITION_LONGITUDE));
                        model.setPositionlatitude(jsonList.getString(POSITION_LATITUDE));
                        model.setStartlocation(jsonList.getString(MYBETS_startlocation));
                        model.setEndlocation(jsonList.getString(MYBETS_endlocation));
                        model.setStartlatitude(jsonList.getString(MYBETS_startlatitude));
                        model.setStartlongitude(jsonList.getString(MYBETS_startlongitude));
                        model.setRoute(jsonList.getString(MYBETS_route));
                        archivesDetailsList.add(model);
                    }
                archiveDetailsListAdapter = new ArchiveDetailsListAdapter(ArchiveDetailsActivity.this, archivesDetailsList);
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(ArchiveDetailsActivity.this));
                invite_group_list.setAdapter(archiveDetailsListAdapter);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
