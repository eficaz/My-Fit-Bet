package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.customview.MyDialog;
import com.androidapp.fitbet.model.MyBets;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.EditBetCreationActivity;
import com.androidapp.fitbet.ui.UpcomingBetDetailActivity;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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

public class UpcommingBetsDetailsListAdapter extends RecyclerView.Adapter  {
    Context constant;
    public static ArrayList<MyBets> groupListModels;
    public static List<MyBets> selected_usersList=new ArrayList<>();
    private static List<MyBets> contactListFiltered;

    Double lat,log;
    SimpleDateFormat sdf;
private MyDialog noInternetDialog;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    private boolean canJoin;
    private int currentPosition;

    public UpcommingBetsDetailsListAdapter(Context context, ArrayList<MyBets> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        noInternetDialog=new MyDialog(context,null,context.getString(R.string.no_internet),context.getString(R.string.no_internet_message),context.getString(R.string.ok),"",true,"internet");
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.up_comming_bets_details_item_list, parent, false);
        UpcommingBetsDetailsListAdapter.ViewHolder vh = new UpcommingBetsDetailsListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        this.currentPosition=position;
        final MyBets m = groupListModels.get(position);
        final UpcommingBetsDetailsListAdapter.ViewHolder viewholder = (UpcommingBetsDetailsListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getBetname());
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

       /* sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(m.getDate());
            sdf.setTimeZone(TimeZone.getDefault());
            viewholder.tv_Description.setText(""+sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        //viewholder.tv_Description.setText(m.getDate());
        viewholder.users_count.setText(""+m.getCredit());
        viewholder.left_days.setText("Total "+(Double.parseDouble(m.getDistance())/1000) +"Km");

        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, UpcomingBetDetailActivity.class);
                i.putExtra(Contents.MYBETS_betid,groupListModels.get(position).getBetid());
                i.putExtra(Contents.MYBETS_status,groupListModels.get(position).getStatus());
                i.putExtra(Contents.MYBETS_upcoming,"0");
                constant.startActivity(i);
            }
        });

        viewholder.join_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(constant)
                        .setTitle("")
                        .setMessage("Are you sure about joining "+groupListModels.get(position).getBetname()+"?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(Utils.isConnectedToInternet(constant)) {

                                            callJoinApi(position);
                                        }else  noInternetDialog.show();
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
        viewholder. decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(constant)
                        .setTitle("")
                        .setMessage("Are you sure about decline "+groupListModels.get(position).getBetname()+"?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(Utils.isConnectedToInternet(constant))
                                        declineBet(position);
                                        else
                                            noInternetDialog.show();
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

    private void declineBet(final int position) {



        if(!CustomProgress.getInstance().isShowing())
            CustomProgress.getInstance().showProgress(constant, "", false);

        System.out.println("bet Id "+groupListModels.get(position).getBetid()+" reg key "+AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));

        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).declineInvite(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),groupListModels.get(position).getBetid());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject;
                    try {
                        CustomProgress.getInstance().hideProgress();
                        jsonObject = new JSONObject(bodyString);
                        System.out.println("Decline bet = "+bodyString);
                        String data = jsonObject.getString("Status");
                        String message=jsonObject.getString("Msg");
                        if(data.equals("Ok")){

                            groupListModels.remove(position);
                            notifyDataSetChanged();

                        }

                      showMessage(message);


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


    private void showMessage(String message){

        MyDialog myDialog=new MyDialog(constant,null,"",message,constant.getString(R.string.ok),"",false,"edit_bet_error");
        myDialog.show();
    }


    private void showConfirmDialog(String message) {

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(constant)
                .setTitle("")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(Utils.isConnectedToInternet(constant)) {
                                    canJoin=true;
callJoinApi(currentPosition);
                                }else  noInternetDialog.show();
                            }
                        }) .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                }).create();
        dialog.show();

    }
    private void callJoinApi(final int position) {
        CustomProgress.getInstance().showProgress(constant, "", false);
        Call<ResponseBody> joinCall;
        if(canJoin){
            joinCall = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).confirmJoinBet(groupListModels.get(position).getBetid(),AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        }else{
            joinCall = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).JoinBet(groupListModels.get(position).getBetid(),AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        }

        joinCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("Join bets "+bodyString);
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        CustomProgress.getInstance().hideProgress();
                        String msg = jsonObject.getString("Msg");
                        CustomProgress.getInstance().hideProgress();
                        if(jsonObject.getString("Status").equals("Ok")){

                            groupListModels.remove(position);
                            notifyDataSetChanged();
                        }else{
                            if(jsonObject.getString("can_join").equals("yes")){
                             showConfirmDialog(msg);
                            }else{
                              showMessage(msg);
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
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
    private void callStartApi(final int position, final ViewHolder viewholder) {
        String latitudeLongitude = Utils.getLocationFromNetwork();
        String[] Lat = latitudeLongitude.split(",");
        lat= Double.valueOf(Lat[0]);
        log= Double.valueOf(Lat[1]);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).StartBet(groupListModels.get(position).getChallengerid(),groupListModels.get(position).getDistance(),log.toString(),lat.toString(),
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
                            viewholder.join_now.setVisibility(View.GONE);
                            viewholder.decline.setVisibility(View.GONE);
                            //notifyDataSetChanged();
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
    public void filterList(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        }else {
            for (MyBets wp : contactListFiltered) {
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
        Button join_now,decline;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            decline= convertView.findViewById(R.id.start);
            left_days= convertView.findViewById(R.id.left_days);
            users_count = convertView.findViewById(R.id.users_count);
            tv_Description = convertView.findViewById(R.id.tv_Description);
            join_now= convertView.findViewById(R.id.join_now);
            button_row= convertView.findViewById(R.id.button_row);
            itemView.setTag(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}
