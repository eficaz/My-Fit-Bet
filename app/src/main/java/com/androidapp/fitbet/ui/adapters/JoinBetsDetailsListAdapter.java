package com.androidapp.fitbet.ui.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.JoinBets;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.ui.UpcomingBetDetailActivity;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.USER_start_latitude;
import static com.androidapp.fitbet.utils.Contents.USER_start_longitude;

public class JoinBetsDetailsListAdapter extends RecyclerView.Adapter{
    Context constant;
    public static ArrayList<JoinBets> groupListModels;
    public static List<JoinBets> selected_usersList=new ArrayList<>();
    private static List<JoinBets> contactListFiltered;
    SimpleDateFormat sdf;
    //Double lat,log;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public JoinBetsDetailsListAdapter(Context context, ArrayList<JoinBets> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_bets_details_item_list, parent, false);
        JoinBetsDetailsListAdapter.ViewHolder vh = new JoinBetsDetailsListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final JoinBets m = groupListModels.get(position);
        final JoinBetsDetailsListAdapter.ViewHolder viewholder = (JoinBetsDetailsListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getBetname());
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(m.getDate());
                df.setTimeZone(TimeZone.getDefault());
                String formattedDate = df.format(date);
                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                String output = null;
                output = outputformat.format(date);
                viewholder.tv_Description.setText(output);
            }catch (Exception e){
                e.printStackTrace();
            }
        //viewholder.tv_Description.setText(m.getDate());
        viewholder.users_count.setText(""+groupListModels.get(position).getCredit());
        viewholder.left_days.setText("Total "+(Double.parseDouble(m.getDistance())/1000) +"Km");
        if(groupListModels.get(position).getChallengerid().equals("null") || groupListModels.get(position).getChallengerid().equals("")){
            viewholder.start.setVisibility(View.GONE);
        }else if(!groupListModels.get(position).getChallengerid().equals("") &&!groupListModels.get(position).getStarted().equals("no")){
            viewholder.start.setVisibility(View.VISIBLE);
        }else{
            viewholder.start.setVisibility(View.GONE);
        }
        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, UpcomingBetDetailActivity.class);
                i.putExtra(Contents.MYBETS_betid,groupListModels.get(position).getBetid());
                i.putExtra(Contents.MYBETS_status,groupListModels.get(position).getStatus());
                i.putExtra(Contents.MYBETS_upcoming,"1");
                constant.startActivity(i);
            }
        });
       if(groupListModels.get(position).getEditstatus().equals("no")){
           viewholder.cancel.setVisibility(View.GONE);
       }else{
           viewholder.cancel.setVisibility(View.VISIBLE);
       }
        viewholder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(constant);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.cancel_alert);
                final EditText editText=dialog.findViewById(R.id.editText);
                TextView cancel=dialog.findViewById(R.id.cancel);
                ImageView close=dialog.findViewById(R.id.btn_close);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dialog.dismiss();
                        if (Utils.isConnectedToInternet(constant)){
                            if(!editText.getText().toString().equals("")){
                                CustomProgress.getInstance().showProgress(constant, "", false);
                                Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).CanceledBet(groupListModels.get(position).getBetid(),AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),editText.getText().toString());
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            String bodyString = new String(response.body().bytes(), "UTF-8");
                                            CustomProgress.getInstance().hideProgress();
                                            final JSONObject jsonObject = new JSONObject(bodyString);
                                            String data = jsonObject.getString(STATUS_A);
                                            if(data.equals("Ok")){
                                                String Msg = jsonObject.getString("Msg");
                                                Utils.showCustomToastMsg(constant,Msg);
                                                groupListModels.remove(position);
                                                notifyDataSetChanged();
                                            }else{
                                                String Msg = jsonObject.getString("Msg");
                                                Utils.showCustomToastMsg(constant,Msg);
                                                //notifyDataSetChanged();
                                                viewholder.cancel.setVisibility(View.GONE);
                                            }
                                            CustomProgress.getInstance().hideProgress();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    }
                                });
                                //sendMessageAPi(groupListModels.get(position).getBetid(),editText.getText().toString(),position);
                                dialog.dismiss();
                            }else{
                                Utils.showCustomToastMsg(constant, R.string.please_type_message);
                            }
                        }else{
                            Utils.showCustomToastMsg(constant, R.string.no_internet);
                            dialog.dismiss();
                        }
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                showAlertInfo(position);

            }
        });
        viewholder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.UPDATE_METER, "0");
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(constant)
                        .setTitle("")
                        .setMessage("Are you sure about starting "+groupListModels.get(position).getBetname())
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Location locationA;
                                        if(groupListModels.get(position).getStartlatitude().equals("")){
                                            locationA = new Location("");
                                            locationA.setLatitude(Double.parseDouble("0"));
                                            locationA.setLongitude(Double.parseDouble("0"));
                                            Location locationB = new Location("");
                                            locationB.setLatitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")));
                                            locationB.setLongitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,"")));
                                            float distance = locationA.distanceTo(locationB);
                                            final DecimalFormat f = new DecimalFormat("##.00");
                                            double l3= Double.parseDouble(f.format(distance));
                                            //Utils.showCustomToastMsg(constant, "---------meter-----------"+""+l3);
                                            Call<ResponseBody> call;
                                            //Utils.showCustomToastMsg(constant, "------latitude------1--"+""+AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")+"------longitude-------1-"+""+AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""));

                                                CustomProgress.getInstance().showProgress(constant, "", false);
                                                call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).StartBet(groupListModels.get(position).getChallengerid(),"0",
                                                        AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,""),
                                                        AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""),
                                                        AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
                                                AppPreference.getPrefsHelper().savePref(USER_start_latitude,   AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,""));
                                                AppPreference.getPrefsHelper().savePref(USER_start_longitude,   AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""));
                                                AppPreference.getPrefsHelper().saveOrigin(  AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")+ "," +   AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""));
                                                AppPreference.getPrefsHelper().savedStatusFlag(true);

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
                                                                viewholder.start.setVisibility(View.GONE);
                                                                viewholder.button_row.setVisibility(View.GONE);
                                                                String msg = jsonObject.getString("Msg");
                                                                //Utils.showCustomToastMsg(constant, msg);
                                                                AppPreference.getPrefsHelper().savePref(Contents.BET_START_STATUS, "true");
                                                                AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "2");
                                                                AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "0");
                                                                Intent intent = new Intent(constant, DashBoardActivity.class);
                                                                constant.startActivity(intent);
                                                            }else{
                                                                CustomProgress.getInstance().hideProgress();
                                                                String msg = jsonObject.getString("Msg");
                                                                Utils.showCustomToastMsg(constant, msg);
                                                                viewholder.start.setVisibility(View.GONE);
                                                                notifyDataSetChanged();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        //groupDetailsList(bodyString,position);
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
                                        }else{
                                            locationA = new Location("");
                                            locationA.setLatitude(Double.parseDouble(groupListModels.get(position).getStartlatitude()));
                                            locationA.setLongitude(Double.parseDouble(groupListModels.get(position).getStartlongitude()));
                                            Location locationB = new Location("");
                                            locationB.setLatitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")));
                                            locationB.setLongitude(Double.parseDouble(AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,"")));
                                            float distance = locationA.distanceTo(locationB);
                                            final DecimalFormat f = new DecimalFormat("##.00");
                                            double l3= Double.parseDouble(f.format(distance));
                                            //Utils.showCustomToastMsg(constant, "---------meter-----------"+""+l3);
                                            Call<ResponseBody> call
                                            //Utils.showCustomToastMsg(constant, "------latitude------2--"+""+AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,"")+"------longitude-------2-"+""+AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""));

                                                = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).StartBet(groupListModels.get(position).getChallengerid(),"0"
                                                       ,AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LAT,""),
                                                        AppPreference.getPrefsHelper().getPref(Contents.FOR_START_BET_LOG,""),
                                                         AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));


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
                                                                viewholder.start.setVisibility(View.GONE);
                                                                viewholder.button_row.setVisibility(View.GONE);
                                                                String msg = jsonObject.getString("Msg");
                                                                Utils.showCustomToastMsg(constant, msg);
                                                                AppPreference.getPrefsHelper().savePref(Contents.BET_START_STATUS, "true");
                                                                clearSavedBetItems();
                                                                constant.startActivity(new Intent(constant, DashBoardActivity.class));
                                                            }else{
                                                                String msg = jsonObject.getString("Msg");
                                                                Utils.showCustomToastMsg(constant, msg);
                                                                viewholder.start.setVisibility(View.GONE);
                                                                notifyDataSetChanged();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        //groupDetailsList(bodyString,position);
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
                                    }
                                }) .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();


            }

        });
    }
    private void clearSavedBetItems() {
        AppPreference.getPrefsHelper().saveDistance("0.0");
        AppPreference.getPrefsHelper().savedStatusFlag(false);
        AppPreference.getPrefsHelper().saveUserRoute("");
        AppPreference.getPrefsHelper().saveOrigin("");
    }

    private void showAlertInfo(final int position) {

    }
    private void sendMessageAPi(String betid, String message, final int position) {
    }
    private void callJoinApi(int position) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).JoinBet(groupListModels.get(position).getBetid(),AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    CustomProgress.getInstance().hideProgress();
                    notifyDataSetChanged();
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
    public void filterList(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        }else {
            for (JoinBets wp : contactListFiltered) {
                if (wp.getBetname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }

            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Description,users_count,left_days;
        ConstraintLayout rowView;
        TableRow button_row;
        Button cancel;
        Button start;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            start= convertView.findViewById(R.id.start);
            left_days= convertView.findViewById(R.id.left_days);
            users_count = convertView.findViewById(R.id.users_count);
            tv_Description = convertView.findViewById(R.id.tv_Description);
            button_row= convertView.findViewById(R.id.button_row);
            cancel= convertView.findViewById(R.id.cancel);
            itemView.setTag(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}
