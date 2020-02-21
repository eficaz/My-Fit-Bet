package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.Invitemembers;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.ProfileActivity;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MybetUserInviteAdapter extends RecyclerView.Adapter {
    Context constant;
    public ArrayList<Invitemembers> groupListModels;
    public List<Invitemembers> selected_usersList = new ArrayList<>();
    private List<Invitemembers> contactListFiltered;
    String groupId;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public MybetUserInviteAdapter(Context context, ArrayList<Invitemembers> myDataset, ArrayList<Invitemembers> selectedList, String groupId) {
        this.constant = context;
        this.groupListModels = myDataset;
        this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.groupId = groupId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_item_list, parent, false);
        MybetUserInviteAdapter.ViewHolder vh = new MybetUserInviteAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Invitemembers m = groupListModels.get(position);
        final MybetUserInviteAdapter.ViewHolder viewholder = (MybetUserInviteAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getFirstname());
        viewholder.tv_Country.setText(m.getCountry());
        if (m.getDistance().equals("null") || m.getDistance().equals("")) {

            if (m.getDistance().length() > 4) {
                DecimalFormat newFormat = new DecimalFormat("0.00");
                double i2 = (Double.parseDouble(m.getDistance().substring(0, 5)) / 1000);
                viewholder.tv_km.setText("" + Double.valueOf(newFormat.format(Double.valueOf(m.getDistance()) / 1000)) + " KM");
            } else {
                DecimalFormat newFormat = new DecimalFormat("0.00");
                double i2 = (Double.parseDouble(m.getDistance()) / 1000);
                viewholder.tv_km.setText("" + Double.valueOf(newFormat.format(Double.valueOf(m.getDistance()) / 1000)) + " KM");
            }


        } else {


            if (m.getDistance().length() > 4) {
                DecimalFormat newFormat = new DecimalFormat("0.00");
                double i2 = (Double.parseDouble(m.getDistance().substring(0, 5)) / 1000);
                viewholder.tv_km.setText("" + newFormat.format(i2) + " Km");
            } else {
                DecimalFormat newFormat = new DecimalFormat("0.00");
                double i2 = (Double.parseDouble(m.getDistance()) / 1000);
                viewholder.tv_km.setText("" + newFormat.format(i2) + " Km");
            }


        }
        if (!m.getProfile_pic().equals("NA")) {
            if (m.getRegType().equals("normal") && m.getImage_status().equals("0")) {
                //Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+jsonObject1.getString(PROFILE_PIC)).into(img_user);
                Picasso.get().load(Constant.BASE_APP_IMAGE__PATH + m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
            } else {
                Picasso.get().load(m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
            }
        } else {
            viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
        }


        viewholder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callincviteApi(m.getReg_key(), groupId, position);
                CustomProgress.getInstance().showProgress(constant, "", false);
            }
        });
        viewholder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.USER_PROFILE_REG_KEY, m.getReg_key());
                Intent i = new Intent(constant, ProfileActivity.class);
                constant.startActivity(i);
            }
        });
       /* if(selected_usersList.contains(groupListModels.get(position)))
            viewholder.card_view.setBackgroundColor(ContextCompat.getColor(constant, R.color.gray_1));
        else
            viewholder.card_view.setBackgroundColor(ContextCompat.getColor(constant, R.color.white));*/
    }

    private void callincviteApi(String regKey, String groupId, final int position) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).MybetUserInvite(regKey, groupId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    groupDetailsList(bodyString, position);
                    notifyDataSetChanged();
                    CustomProgress.getInstance().hideProgress();
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

    private void groupDetailsList(String bodyString, int position) {
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if (data.equals("Ok")) {
                Utils.showCustomToastMsg(constant, R.string.invited_successfully);
                groupListModels.remove(position);
                contactListFiltered.remove(position);
                notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void filterList(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        } else {
            for (Invitemembers wp : contactListFiltered) {
                if (wp.getFirstname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Country, tv_km;
        Button invite;
        LinearLayout row;

        //CardView card_view;
        public ViewHolder(View convertView) {
            super(convertView);
            //card_view=  convertView.findViewById(R.id.card_view);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            tv_km = convertView.findViewById(R.id.tv_km);
            tv_Country = convertView.findViewById(R.id.tv_Country);
            invite = convertView.findViewById(R.id.invite);
            row = convertView.findViewById(R.id.row);
            itemView.setTag(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}
