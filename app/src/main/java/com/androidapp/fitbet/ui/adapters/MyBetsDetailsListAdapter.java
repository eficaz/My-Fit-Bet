package com.androidapp.fitbet.ui.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.MyBets;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.EditBetCreationActivity;
import com.androidapp.fitbet.ui.MyBetDetailActivity;
import com.androidapp.fitbet.ui.MyBetGroupInviteActivity;
import com.androidapp.fitbet.ui.MyBetUserInviteActivity;
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

public class MyBetsDetailsListAdapter extends RecyclerView.Adapter {
    Context constant;
    public static ArrayList<MyBets> groupListModels;
    public static List<MyBets> selected_usersList = new ArrayList<>();
    private static List<MyBets> contactListFiltered;
    Double lat, log;
    String bodyString;
    SimpleDateFormat sdf;
    String distance_draw = "0";
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public MyBetsDetailsListAdapter(Context context, ArrayList<MyBets> myDataset, String bodyString) {
        this.constant = context;
        this.groupListModels = myDataset;
        this.bodyString = bodyString;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bet_details_item_list, parent, false);
        MyBetsDetailsListAdapter.ViewHolder vh = new MyBetsDetailsListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final MyBets m = groupListModels.get(position);
        final MyBetsDetailsListAdapter.ViewHolder viewholder = (MyBetsDetailsListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getBetname());
        try {
            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String output = null;
            DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date thisMorningMidnight = justDay.parse(m.getDate());
            output = outputformat.format(thisMorningMidnight);
            viewholder.tv_Description.setText(output);
        } catch (Exception e) {

        }
        viewholder.users_count.setText("" + groupListModels.get(position).getCredit());
        viewholder.left_days.setText("Total " + (Double.parseDouble(m.getDistance()) / 1000) + "Km");
        viewholder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertInfo(position);
            }
        });
        if (groupListModels.get(position).getEditstatus().equals("yes")) {
            viewholder.edit.setVisibility(View.VISIBLE);
        } else {
            viewholder.edit.setVisibility(View.GONE);
        }
        viewholder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupListModels.get(position).getEditstatus().equals("yes")) {
                    Intent i = new Intent(constant, EditBetCreationActivity.class);
                    i.putExtra(Contents.pass_startlatitude, "0.0");
                    i.putExtra(Contents.pass_startlongitude, "0.0");
                    i.putExtra(Contents.pass_endlatitude, "0.0");
                    i.putExtra(Contents.pass_endlongitude, "0.0");
                    i.putExtra(Contents.MYBETS_challengerid, groupListModels.get(position).getChallengerid());
                    i.putExtra(Contents.MYBETS_distance, String.valueOf(distance_draw));
                    i.putExtra(Contents.bet_edit_details, bodyString);
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_EDIT_OR_CREATE, "true");
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_BET_ID, groupListModels.get(position).getBetid());
                    constant.startActivity(i);
                } else {
                    Utils.showCustomToastMsg(constant, R.string.you_can_not_edit);
                }
            }
        });
        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, MyBetDetailActivity.class);
                i.putExtra(Contents.MYBETS_betid, groupListModels.get(position).getBetid());
                i.putExtra(Contents.MYBETS_status, groupListModels.get(position).getStatus());
                constant.startActivity(i);
            }
        });
        if (!m.getStarted().equals("no")) {
          /*  viewholder.invite.setVisibility(View.GONE);
            viewholder.start.setVisibility(View.GONE);*/
        } else {
            if (m.getStatus().equals("0") || m.getStatus().equals("4")) {
               /* viewholder.invite.setVisibility(View.GONE);
                viewholder.start.setVisibility(View.GONE);*/
            }
        }
        viewholder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitudeLongitude = Utils.getLocationFromNetwork();
                String[] Lat = latitudeLongitude.split(",");
                lat = Double.valueOf(Lat[0]);
                log = Double.valueOf(Lat[1]);
                Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).StartBet(groupListModels.get(position).getChallengerid(), groupListModels.get(position).getDistance(), log.toString(), lat.toString(),
                        AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String bodyString = new String(response.body().bytes(), "UTF-8");
                            final JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(bodyString);
                                String data = jsonObject.getString("Status");
                                if (data.equals("Ok")) {

                                    CustomProgress.getInstance().hideProgress();
                                    groupListModels.remove(position);
                                }
                            } catch (JSONException e) {
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
        });
    }

    private void clearSavedBetItems() {
        AppPreference.getPrefsHelper().saveDistance("0.0");
        AppPreference.getPrefsHelper().savedStatusFlag(false);
        AppPreference.getPrefsHelper().saveUserRoute("");
        AppPreference.getPrefsHelper().saveOrigin("");
        AppPreference.getPrefsHelper().setLatLongList(null);
        AppPreference.getPrefsHelper().savePositionLatitude("0.0");
        AppPreference.getPrefsHelper().savePositionLongitude("0.0");
    }

    private void showAlertInfo(final int position) {
        final Dialog dialog = new Dialog(constant);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_invite_sagection);
        TextView user = dialog.findViewById(R.id.user);
        TextView group = dialog.findViewById(R.id.group);
        TextView cancel = dialog.findViewById(R.id.cancel);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, MyBetUserInviteActivity.class);
                i.putExtra(Contents.GROUP_ID, groupListModels.get(position).getBetid());
                constant.startActivity(i);
                dialog.dismiss();
                // constant.startActivity(new Intent(constant, InviteGroupListActivity.class));
            }
        });
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, MyBetGroupInviteActivity.class);
                i.putExtra(Contents.GROUP_ID, groupListModels.get(position).getBetid());
                constant.startActivity(i);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void filterList(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        } else {
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
        TextView tv_Name, tv_Description, users_count, left_days;
        LinearLayout rowView;
        Button start;
        ImageView invite, edit;
        TableRow button_row;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            left_days = convertView.findViewById(R.id.left_days);
            users_count = convertView.findViewById(R.id.users_count);
            tv_Description = convertView.findViewById(R.id.tv_Description);
            invite = convertView.findViewById(R.id.invite);
            edit = convertView.findViewById(R.id.edit);
            start = convertView.findViewById(R.id.start);
            button_row = convertView.findViewById(R.id.button_row);
            itemView.setTag(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}
